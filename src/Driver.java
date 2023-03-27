import java.util.*;
import edu.du.dudraw.DUDraw;
import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Driver {

	public static void main(String[] args) {
		
		// ask user for input
		System.out.println("Would you like to extract (1) or embed (2) an image?");

		//make scanner
		Scanner theScanner = new Scanner(System.in);
		
		// check which mode user chose
		int mode = 0;
		mode = theScanner.nextInt();
		
		//if mode 1 then embed the file
		if (mode == 1) {
			
			//ask user for file name
			System.out.println("Please enter the embeded file's name.");
			
			//get file name from user
			theScanner = new Scanner(System.in);
			String answer = theScanner.nextLine();
			
			//create new color array
			Color[][] theColors = null;
			
			//try and get color array from reading in the file to bmpio.readbmpfile
			try {
				theColors = BMPIO.readBMPFile(answer);
			} catch (IOException | BMPIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//get the secret colors from steganography class
			Color[][] secretColors = Steganography.extractSecretImage(theColors);
			
			//create the dudraw canvas n stuff
			DUDraw.setCanvasSize(theColors.length + secretColors[0].length, theColors[0].length);
			DUDraw.setXscale(0, theColors.length + secretColors[0].length);
			DUDraw.setYscale(0, theColors[0].length);
			DUDraw.enableDoubleBuffering();
			
			//draw original image
			for (int col = 0; col < theColors[0].length; col++) {
				for (int row = 0; row < theColors.length; row++) {
					DUDraw.setPenColor(theColors[row][col]);
					DUDraw.point(row, col);
				}
			}
			
			//draw extracted image
			for (int col = 0; col < secretColors[0].length; col++) {
				for (int row = 0; row < secretColors.length; row++) {
					DUDraw.setPenColor(secretColors[row][col]);
					DUDraw.point(theColors.length+col, row);
				}
			}
			
			//show images!!!!
			DUDraw.show();
			
		}
		
		// if extracting
		else if (mode == 2) {
			
			//ask user for public and secret image
			System.out.println("Please enter the name of the public image and secret image");
			
			//crate new scanner
			theScanner = new Scanner(System.in);
			
			//get names of public and secret images
			String publicImageName = theScanner.nextLine();
			String secretImageName = theScanner.nextLine();
			
			//declare images
			Color[][] originalImage = null;
			Color[][] publicImage = null;
			Color[][] secretImage = null;
			
			//get the public and secret image color array data
			try {
				publicImage = BMPIO.readBMPFile(publicImageName);
				secretImage = BMPIO.readBMPFile(secretImageName);
			} catch (IOException | BMPIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//get the color data for the embeded images
			Color[][] embededImage = Steganography.embedSecretImage(publicImage, secretImage);
			
			//du draw n stuff
			DUDraw.setCanvasSize(publicImage.length*2, publicImage[0].length);
			DUDraw.setXscale(0, publicImage.length*2);
			DUDraw.setYscale(0, publicImage[0].length);
			DUDraw.enableDoubleBuffering();
			
			//draw original image
			for (int col = 0; col < publicImage[0].length; col++) {
				for (int row = 0; row < publicImage.length; row++) {
					DUDraw.setPenColor(publicImage[row][col]);
					DUDraw.point(row, col);
				}
			}
			
			//draw embeded image
			for (int col = 0; col < embededImage[0].length; col++) {
				for (int row = 0; row < embededImage.length; row++) {
					DUDraw.setPenColor(embededImage[row][col]);
					DUDraw.point(publicImage.length+row, col);
				}
			}
			
			//show
			DUDraw.show();
			
			//write new image to a bmp file
			try {
				writeBMPCopy(publicImageName, "Secret" + publicImageName, embededImage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	/*
	 * This method should copy the BMP file from 'in_path' to 'out_path'
	 * and then use the contents of the 'image' array to overwrite the
	 * image data in the appropriate place in the copy.
	 *
	 * Usage:
	 *         writeBMPCopy("original.bmp", "copy.bmp", <2D array of embedded image color data>)
	 */

	public static void writeBMPCopy(String in_path, String out_path, Color[][] image) throws IOException {
	    Path src = Paths.get(in_path);
	    Path dst = Paths.get(out_path);
	    
	    // Copy the file from input to output
	    Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
	    
	    RandomAccessFile out = new RandomAccessFile(out_path, "rw");
	    
	    //go to color data start and write color stuff
	    out.seek(54);
	    for (int r = 0; r < image[0].length; r++) {
	    	for (int c = 0; c < image.length; c++) {
	    		out.write(image[c][r].getBlue());
	    		out.write(image[c][r].getGreen());
	    		out.write(image[c][r].getRed());
		    }
	    }
	    
	    out.close();
	    
	}

}
