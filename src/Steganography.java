import java.awt.Color;

public class Steganography {

	public static Color[][] extractSecretImage(Color[][] EmbededImage) {
		
		//return null if image passed is null
		if (EmbededImage == null) {
			return null;
		}
		
		// create secret image with same size of embeded image
		Color[][] secretImage = new Color[EmbededImage[0].length][EmbededImage.length];
		
		for (int r = 0; r < EmbededImage.length; r++) {
			for (int c = 0; c < EmbededImage[0].length; c++) {
			
				//get red, green, blue and shift the bits
				int R = EmbededImage[r][c].getRed();
				R %= 16;
				R *= 16;
				int G = EmbededImage[r][c].getGreen();
				G %= 16;
				G *= 16;
				int B = EmbededImage[r][c].getBlue();
				B %= 16;
				B *= 16;
				
				//make new color in each element of the array
				secretImage[c][r] = new Color(R, G, B);
				
			}
		}
		
		return secretImage;
	}
	
	public static Color[][] embedSecretImage(Color[][] publicImage, Color[][] secretImage) {
		
		//create new array for embeded image of the public image size 
		Color[][] embededImage = new Color[publicImage.length][publicImage[0].length];
		
		//nested for loop
		for (int r = 0; r < publicImage.length; r++) {
			for (int c = 0; c < publicImage[0].length; c++) {
				
				//get red green and blue from public image and get rid of last 4 bits
				int R = publicImage[r][c].getRed();
				R = R - (R%16);
				int G = publicImage[r][c].getGreen();
				G = G - (G%16);
				int B = publicImage[r][c].getBlue();
				B = B - (B%16);
				
				int R2, G2, B2;
				
				//get the important bits and shift them down to last four
				if (r < secretImage.length && c < secretImage[0].length) {
					R2 = secretImage[r][c].getRed();
					R2 /= 16;
					G2 = secretImage[r][c].getGreen();
					G2 /= 16;
					B2 = secretImage[r][c].getBlue();
					B2 /= 16;
				}
				else {
					//if we are past color array data for secret image just make it black
					R2 = 0;
					G2 = 0;
					B2 = 0;
				}
				
				int R3 = R + R2;
				int G3 = G + G2;
				int B3 = B + B2;
				
				//draw color at each element of array
				embededImage[r][c] = new Color(R3, G3, B3);
					
			}
		}
		
		return embededImage;
		
	}
	
}
