package com.freire.tools;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.xml.transform.TransformerConfigurationException;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
//import org.dcm4che2.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.tool.dcm2xml.Dcm2Xml;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;

public class ExportaDicom {
	
	private DicomObject dcmObj;
	private ByteArrayInputStream dcmArq;
	private String savePath;
	private File dcmFile;
	private String[] dcmData;
	
	public ExportaDicom(byte[] dcmByteArray, String savePath) {
		setDicomArq(dcmByteArray);
		setDicomObject(this.dcmArq);
		setSavePath(this.dcmObj.getString(Tag.SOPInstanceUID));
		setDcmFile(dcmByteArray, "dcmTempFile.dcm");
		carregaDadosDicom();
	}
	
	public ExportaDicom(String dcmFilePath) {
		try {
			this.dcmFile = new File(dcmFilePath);
			DicomInputStream dcmIS;
			dcmIS = new DicomInputStream(this.dcmFile);
			setDicomObject(dcmIS);
			this.dcmArq = null;
			setSavePath(dcmFilePath.replace(".dcm", ""));
			carregaDadosDicom();
		} catch (IOException e) {
			System.out.println("Não é possível ler o arquivo");
			e.printStackTrace();
		}
	}
	
	public ExportaDicom(File dcmFile) {
		
		try {
			this.dcmFile = dcmFile;
			DicomInputStream dcmIS = new DicomInputStream(this.dcmFile);
			setDicomObject(dcmIS);
			this.dcmArq = null;
			setSavePath(this.savePath = this.dcmFile.getPath().replace(".dcm", ""));
			carregaDadosDicom();
		} catch (IOException e) {
			System.out.println("Não é possível ler o arquivo");
			e.printStackTrace();
		}
	}
	
	private void setDicomObject(InputStream dcmArq) {
		DicomInputStream dcmIS;
		try {
			dcmIS = new DicomInputStream(dcmArq);
			this.dcmObj = dcmIS.readDicomObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setDicomArq(byte[] dcmByteArray) {
		this.dcmArq = new ByteArrayInputStream(dcmByteArray);
	}
	
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	
	private void setDcmFile(byte[] dcmByteArray, String dcmFilePath) {
		try {
			this.dcmFile = new File(dcmFilePath);
			OutputStream fos = new FileOutputStream(this.dcmFile);
			fos.write(dcmByteArray, 0, dcmByteArray.length);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	
	public void export2Xml() {
		
		try{
			Dcm2Xml xmlFile = new Dcm2Xml();
			xmlFile.convert(this.dcmFile, new File(this.savePath.concat(".xml")));
		}catch(IOException e) {
			return;
		}catch(TransformerConfigurationException e) {
			return;
		}
	}
	
	public void export2Jpg(){
		try {
			ImageIO.scanForPlugins();
			Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
			ImageReader imgReader = (ImageReader) iter.next();
			
			DicomImageReadParam param = (DicomImageReadParam) imgReader.getDefaultReadParam();

			ImageInputStream iis;
			if(this.dcmFile == null) {
				iis = ImageIO.createImageInputStream(this.dcmArq);
			} else {
				iis = ImageIO.createImageInputStream(this.dcmFile);
			}

			imgReader.setInput(iis, false);
			BufferedImage bfImg = imgReader.read(0, param);

			if(bfImg == null) {
				System.out.println("Arquivo vazio ou inexistente.");
				return;
			}

			File jpgImgFile = new File(this.savePath.concat(".jpg"));

			OutputStream output = new BufferedOutputStream(new FileOutputStream(jpgImgFile));

			ImageIO.write(bfImg, "jpeg", output);

			//Fechamento dos streams
			iis.close();
			output.close();		
		}catch(IOException e) {
			e.printStackTrace(); 
			return;
		}		
	}
	
	private void carregaDadosDicom() {
		dcmData = new String[12];
		
		this.dcmData[0] = this.dcmObj.getString(Tag.PatientID);
		this.dcmData[1] = this.dcmObj.getString(Tag.PatientName);
		this.dcmData[2] = this.dcmObj.getString(Tag.PatientBirthDate);
		this.dcmData[3] = this.dcmObj.getString(Tag.PatientSex);
		this.dcmData[4] = this.dcmObj.getString(Tag.StudyID);
		this.dcmData[5] = this.dcmObj.getString(Tag.InstitutionName);
		this.dcmData[6] = this.dcmObj.getString(Tag.AccessionNumber);
		this.dcmData[7] = this.dcmObj.getString(Tag.AcquisitionDate);
		this.dcmData[8] = this.dcmObj.getString(Tag.AcquisitionTime);
		this.dcmData[9] = this.dcmObj.getString(Tag.AcquisitionNumber);
		this.dcmData[10] = this.dcmObj.getString(Tag.Laterality);
		this.dcmData[11] = this.dcmObj.getString(Tag.ProtocolName);
	}
	
	public String[] getDicomData() {
		return this.dcmData;
	}
	
	public BufferedImage getDicomImage() {		
		ImageIO.scanForPlugins();
		Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
		ImageReader imgReader = (ImageReader) iter.next();
			
		DicomImageReadParam param = (DicomImageReadParam) imgReader.getDefaultReadParam();
		
		try {
			ImageInputStream iis;
			
			if(this.dcmFile == null) {
				iis = ImageIO.createImageInputStream(this.dcmArq);
			} else {
				iis = ImageIO.createImageInputStream(this.dcmFile);
			}
	
			imgReader.setInput(iis, false);
			BufferedImage bfImg = imgReader.read(0, param);
			
			iis.close();
			
			if(bfImg == null) {
				System.out.println("Arquivo vazio ou inexistente.");
				return null;
			}
			
			return bfImg;
		}catch(IOException e) {
			e.printStackTrace(); 
			return null;
		}	
	}
	
	public byte[] getDicomImageByteArray() {				
		try {
			ImageIO.scanForPlugins();
			Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("DICOM");
			ImageReader imgReader = (ImageReader) iter.next();	
			DicomImageReadParam param = (DicomImageReadParam) imgReader.getDefaultReadParam();
			
			ImageInputStream iis;
			
			if(this.dcmFile == null) {
				iis = ImageIO.createImageInputStream(this.dcmArq);
			} else {
				iis = ImageIO.createImageInputStream(this.dcmFile);
			}

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
	
	
//	Número da Tag. Exemplo: nome do paciente na tabela 0010:0010, como parametro 0x00100010
	public String getTagByNumber(int tagNumber) {
		return dcmObj.getString(tagNumber);
	}
}
