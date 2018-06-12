/**
 * 
 */
package dina.BarCoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.UUID;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;


/**
 * @author Falko Gloeckler
 *
 */
public class BarCoder {
	
	public enum codeFormats { QR_CODE, CODE_128, DATA_MATRIX };
	// set minimum size for code images
	public static int width = 100;
	public static int height = 100;
	private static String csvSeparator;
	
	private static QRCodeWriter qrCodeWriter = new QRCodeWriter();
	private static Code128Writer barCodeWriter = new Code128Writer();
	private static DataMatrixWriter dataMatrixWriter = new DataMatrixWriter();
	private static BitMatrix bitMatrix = new BitMatrix(width, height);
	
	public BarCoder(){
		setCsvSeparator(",");
		width = 300;
		height = 300;
	}
	
	public static String getCsvSeparator() {
		return csvSeparator;
	}

	public static void setCsvSeparator(String csvSeparator) {
		BarCoder.csvSeparator = csvSeparator;
	}

	static public String[] createQRCodes(File csvFile, String outputImagesPath) throws Exception {
		return createCodes(csvFile, outputImagesPath, codeFormats.QR_CODE);
	}

	
	public static String[] createCodes(File csvFile, String outputImagesPath, codeFormats codeFormat) throws Exception {
		
		String[] files = null;
		
		if(!csvFile.exists())
		{			
			throw(new Exception("File doesn't exist."));
		}
		
		if(!csvFile.canRead())
		{
			throw(new Exception("Can't read file."));
		}
	
		File checkDir = new File(outputImagesPath);
		if(!checkDir.exists())
		{
			throw(new Exception("The output directory does not exist!"));
		}
		
		if(!checkDir.canWrite())
		{
			throw(new Exception("Can not write into output directory!"));
		}

		File checkFile = File.createTempFile(".barcoder_", "_check");
		try {
			checkFile.createNewFile();
		} catch (IOException e) {
			System.out.println("Can not write into output directory!\n" +checkDir.getAbsolutePath()+ ".\n\nMake shure that you have permissions for writing!");
		}

		if(!checkFile.exists())
		{
			System.out.println("Can not write into output directory!\n" +checkDir.getAbsolutePath()+ "\n\nMake shure that you have permissions for writing!");
		};
		
	
		// else read the file 		
		
		BufferedReader bufRdr;
		String line = null;
		int row = 0;
		int col = 0;
		
		try {
			bufRdr = new BufferedReader(new FileReader(csvFile));
			
			String[] columns = new String[2];
			String imgFile = null;
			//read each line of text file
			try {
				while((line = bufRdr.readLine()) != null)
				{
					StringTokenizer st = new StringTokenizer(line, getCsvSeparator());
					col = 0;
					while (st.hasMoreTokens() && col<2)
					{
						//get next token and store it in the array
						columns[col] = st.nextToken().replace("\\n", System.getProperty("line.separator"));
						col++;
					}				
					imgFile = createCode(columns, outputImagesPath, codeFormat);
					if(!imgFile.isEmpty()) {
						files[row] = imgFile;
						row++;
					}
					// clear the array
					Arrays.fill(columns, null);					
				}
				bufRdr.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	
		return files;
	}
	
	
	static public String createQRCode(String[] data, String outputImagePath) {	
		return createCode(data, outputImagePath, codeFormats.QR_CODE);
	}
	
	static public String createCode(String[] data, String outputImagePath, codeFormats codeFormat) {		
		
		File imgFile = new File(outputImagePath + "/" + data[1] + "__" + UUID.randomUUID().toString() + ".png");
		try {
			if(!imgFile.createNewFile())
				return null;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(codeFormat==codeFormats.QR_CODE){
			try {
				bitMatrix = qrCodeWriter.encode(data[0], BarcodeFormat.QR_CODE, width, height);
			} catch (WriterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(codeFormat==codeFormats.CODE_128){
			try {
				bitMatrix = barCodeWriter.encode(data[0], BarcodeFormat.CODE_128, width, height);
			} catch (WriterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(codeFormat==codeFormats.DATA_MATRIX){
			bitMatrix = dataMatrixWriter.encode(data[0], BarcodeFormat.DATA_MATRIX, width, height);
		}		
		
		try {
			MatrixToImageWriter.writeToPath(bitMatrix, "png", imgFile.toPath());
			//TODO optional: maybe the function's variant with config parameter useful?
			// MatrixToImageWriter.writeToPath(bitMatrix, "png", file, config);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// return path for further processing
		// backslashes are replaced by slashes (Windows hack)
		//return imgFile.getAbsolutePath().replace("\\", "/");
		return imgFile.getName();
	}
	
}
