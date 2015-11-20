package com.freire.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ConversaoSimples {
	
	private BufferedImage bfImg;
	
	public void gravaImagemDisco(String srcImg, String destImg) {
		try {
			bfImg = ImageIO.read(new File(srcImg));
			ImageIO.write(bfImg, "jpeg", new File(destImg));
		} catch (IOException e) {
			System.out.println("Não é possível abrir o arquivo.");
			e.printStackTrace();
		}
	}

}
