import java.awt.*;
import java.io.*;

public class BMPIO {
	/* Essential information on BMP files format:
	   See https://en.wikipedia.org/wiki/BMP_file_format for more information

	   The first byte of the file must be 'B' == 0x42
	   The second byte of the file must be 'M' == 0x4D
	   At byte 10 of  the file is a four-byte integer giving the offset
	       where the pixel map starts. We require this value to be 54.
	       If it's not, we will report an error in the bitmap format
	       and fail to read the file. 
	       Note: integers in BMP files are stored in little-endian form,
	          which means that the least-significant byte comes first
	          But RandomAccessFile assumes big-endian form.
	          After reading an integer from the file, use Integer.reverseBytes(num)
	          to convert from little-endian to big-endian
	   // At byte 18 of the file are two four-byte integers giving the width
	      and height (in that order) of the image. Again, these are in little-endian form.
	      We require that each of these numbers be divisible by 4.
	      If either are not, we will report an error in the bitmap format
	      and fail to read the file.
	   At byte 28 of the file is a two-byte integer giving the number of bits per pixel
	      We require this number must be 24 (one byte for each color)
	      If it's not, we will report an error in the bitmap format
	      and fail to read the file.
	      Note: this two-byte integer is also in little-endian form,
	      so use Short.reverseBytes() after reading the number from the file.
	   At byte 54, the pixel map data begins. Each pixel is represented
	      by three bytes (blue, then green, then red). The image usually starts at the
	      lower left and proceeds upwards. Recall that colors are always positive, so
	      read these from the file as unsigned bytes.
	   */
	
		public static Color[][] readBMPFile(String fileName) throws IOException, FileNotFoundException, BMPIOException {
			
			//create instance variables
			byte char1, char2;
			int offset;
			
			//make new raf objects
			RandomAccessFile raf = new RandomAccessFile(fileName, "r");
			
			//get the chars to read in b and m
			char1 = raf.readByte();
			char2 = raf.readByte();
			
			//check if the bmp file has b and m
			if (char1 != 'B' || char2 != 'M') {
				raf.close();
				throw new BMPIOException("BMPIO: unsupported document type");
			}
			
			//see if the pixel data start at 54
			raf.seek(10);
			offset = raf.readInt();
			offset = Integer.reverseBytes(offset);
			
			if (offset != 54) {
				raf.close();
				throw new BMPIOException("BMPIO: Pixel data does not start at 54");
			}
			
			// get the width and 
			raf.seek(18);
			int width = raf.readInt();
			int height = raf.readInt();

			//reverse bits
			width = Integer.reverseBytes(width);
			height = Integer.reverseBytes(height);
			
			//check if width and height are divisible by four
			if (width%2 != 0 || height%2 != 0) {
				raf.close();
				throw new BMPIOException("BMPIO: Image width or height not divisible by 4");
			}
			
			// see if image is 24 bit color
			raf.seek(28);
			short bitsPixel = raf.readShort();
			bitsPixel = Short.reverseBytes(bitsPixel);
			
			if (bitsPixel != 24) {
				raf.close();
				throw new BMPIOException("BMPIO: Image does not have a 24-bit color");
			}
			
			//start at color data
			raf.seek(54);
			
			//create new color array
			Color[][] colorArray = new Color[width][height];
			
			//start reading in color data
			for (int h = 0; h < height; h++) {
				for (int w = 0; w < width; w++) {
					
					int blue = (int) raf.readUnsignedByte();
					//blue = Integer.reverseBytes(blue);
					int green = (int) raf.readUnsignedByte();
					//green = Integer.reverseBytes(green);
					int red = (int) raf.readUnsignedByte();
					//red = Integer.reverseBytes(red);
					
					colorArray[w][h] = new Color(red, green, blue);
				}
			}
			
			raf.close();
			
			return colorArray;
			
		}
		
}
