package Teste;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.freire.tools.ExportaDicom;

public class DicomTeste {
	
	private static JFrame fatherFrame;
	private static String dcmPath = "C:/Users/Otávio/Desktop/DICOM/1.2.410.200048.36260.20151105081113.1.1.1.dcm";

	public static void main(String[] args) throws IOException {
		JOptionPane.showMessageDialog(fatherFrame, "Carregando arquivo DICOM.", "Carregando dcm...", JOptionPane.WARNING_MESSAGE);
		ExportaDicom expDicom = new ExportaDicom(dcmPath);
		JOptionPane.showMessageDialog(fatherFrame, "Exportando arquivos XML e JPEG.", "Exportando...", JOptionPane.WARNING_MESSAGE);
		expDicom.export2Xml();
		expDicom.export2Jpg();
		
		BufferedImage dcmImage = expDicom.getDicomImage();
		String[] dcmData = expDicom.getDicomData();
		fatherFrame = new JFrame(dcmData[1] + " - " + dcmData[11]);
		fatherFrame.setSize(dcmImage.getWidth(), dcmImage.getHeight());
		JLabel dcmImageLbl = new JLabel(new ImageIcon(dcmImage));
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(dcmImageLbl);
		fatherFrame.add(mainPanel);
		fatherFrame.setVisible(true);
		
		JOptionPane.showMessageDialog(fatherFrame, "ID: " + dcmData[0] + "\n" +
												   "Paciente: " + dcmData[1] + "\n" +
												   "Nascimento: " + dcmData[2] + "\n" +
												   "Sexo: " + dcmData[3] + "\n" +
												   "ID estudo: " + dcmData[4] + "\n" +
												   "Instituição: " + dcmData[5] + "\n" +
												   "Profissional: " + dcmData[6] + "\n" +
												   "Data exame: " + dcmData[7] + "\n" +
												   "Hora exame: " + dcmData[8] + "\n" +
												   "Série: " + dcmData[9] + "\n" +
												   "Lateralidade: " + dcmData[10] + "\n" +
												   "Modo: " + dcmData[11] + "\n", 
												   "Dados DICOM",
												   JOptionPane.WARNING_MESSAGE);
		
		fatherFrame.dispose();
		
		//Testando o retorno do método ByteArray
		OutputStream fos = new FileOutputStream(new File(dcmPath.replace("dcm", "txt")));
		ByteArrayOutputStream baos = new ByteArrayOutputStream(expDicom.getDicomImageByteArray().length);
		baos.write(expDicom.getDicomImageByteArray());
		baos.writeTo(fos);
		baos.close();
		fos.close();		
	}

}
