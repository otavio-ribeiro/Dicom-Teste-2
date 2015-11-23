package com.freire.tools;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.xml.transform.TransformerConfigurationException;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.tool.dcm2xml.Dcm2Xml;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;

public class DicomUtils {
	
	private File dcmFile;
	private DicomObject dcmObj;
	private String savePath;
	
	public DicomUtils(byte[] dcmByteArray, String savePath) {
		setDcmFile(dcmByteArray, savePath);
		this.savePath = savePath;
		setDcmObject();
	}
	
	public DicomUtils(String dcmFilePath, String savePath) {
		this.dcmFile = new File(dcmFilePath);
		this.savePath = savePath;
		setDcmObject();
	}
	
	private void setDcmFile(byte[] dcmByteArray, String savePath) {
		try {
			this.dcmFile = new File(savePath);
			OutputStream fos = new FileOutputStream(this.dcmFile);
			fos.write(dcmByteArray, 0, dcmByteArray.length);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	private void setDcmObject() {
		DicomInputStream dcmIS;
		try {
			dcmIS = new DicomInputStream(this.dcmFile);
			this.dcmObj = dcmIS.readDicomObject();
		} catch (IOException e) {
			System.out.println("Não é possível ler o arquivo DICOM selecionado.");
			e.printStackTrace();
		}
	}
	
//======================================================================================================
//							Funções de manipulação dcm
//======================================================================================================
	
	
	//Exportação para arquivo XML
	public void export2Xml(String savePath) {	
		try{
			Dcm2Xml xmlFile = new Dcm2Xml();
			xmlFile.convert(this.dcmFile, new File(savePath.concat(".xml")));
		}catch(IOException e) {
			return;
		}catch(TransformerConfigurationException e) {
			return;
		}
	}
	
	//Função para salvamento de imagem dicom em disco
	public void export2Jpg(String savePath){
		try {
			ImageIO.scanForPlugins();
			Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
			ImageReader imgReader = (ImageReader) iter.next();
			
			DicomImageReadParam param = (DicomImageReadParam) imgReader.getDefaultReadParam();

			ImageInputStream iis = ImageIO.createImageInputStream(this.dcmFile);;
			
			imgReader.setInput(iis, false);
			BufferedImage bfImg = imgReader.read(0, param);

			if(bfImg == null) {
				System.out.println("Arquivo vazio ou inexistente.");
				return;
			}

			File jpgImgFile = new File(this.savePath.concat(".jpg"));

			OutputStream output = new BufferedOutputStream(new FileOutputStream(jpgImgFile));

			ImageIO.write(bfImg, "jpeg", output);

			iis.close();
			output.close();		
		}catch(IOException e) {
			e.printStackTrace(); 
			return;
		}		
	}
	
	public String[] getDicomData() {
		String[] dcmData = new String[12];
		
		dcmData[0] = this.dcmObj.getString(Tag.PatientID);
		dcmData[1] = this.dcmObj.getString(Tag.PatientName);
		dcmData[2] = this.dcmObj.getString(Tag.PatientBirthDate);
		dcmData[3] = this.dcmObj.getString(Tag.PatientSex);
		dcmData[4] = this.dcmObj.getString(Tag.StudyID);
		dcmData[5] = this.dcmObj.getString(Tag.InstitutionName);
		dcmData[6] = this.dcmObj.getString(Tag.AccessionNumber);
		dcmData[7] = this.dcmObj.getString(Tag.AcquisitionDate);
		dcmData[8] = this.dcmObj.getString(Tag.AcquisitionTime);
		dcmData[9] = this.dcmObj.getString(Tag.AcquisitionNumber);
		dcmData[10] = this.dcmObj.getString(Tag.Laterality);
		dcmData[11] = this.dcmObj.getString(Tag.ProtocolName);
		
		return dcmData;
	}
	
	public byte[] getDicomImage() {				
		try {
			ImageIO.scanForPlugins();
			Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
			ImageReader imgReader = (ImageReader) iter.next();	
			DicomImageReadParam param = (DicomImageReadParam) imgReader.getDefaultReadParam();
			
			ImageInputStream iis = ImageIO.createImageInputStream(this.dcmFile);

			imgReader.setInput(iis, false);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(imgReader.read(0, param), "jpeg", baos);
			baos.flush(); //TROCAR A POSICAO DO FLUSH CASO NAO ESTEJA DANDO CERTO
			byte[] imageInBytes = baos.toByteArray();
			baos.close();
			iis.close();
			
			return imageInBytes;
			
		}catch(IOException e) {
			e.printStackTrace(); 
			return null;
		}
	}
	
	//Nome da Tag do mesmo jeito que consta na tabela
	public String getTagByName(String tagName) {
		return dcmObj.getString(Tag.toTag(tagName));
	}
	
	
	//Número da Tag. Exemplo: nome do paciente na tabela 0010:0010, como parametro 0x00100010
	public String getTagByNumber(int tagNumber) {
		return dcmObj.getString(tagNumber);
	}

}
