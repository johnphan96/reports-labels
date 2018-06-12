package dina.LabelCreator.Options;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder.PageSizeUnits;

import dina.LabelCreator.Helper.Helper;;

public class Options 
{
	// set default values
	public String templateFile 			= "templates/template.html";
	public String outputFile 			= "labels.pdf";
	public String tmpDir 				= "tmp";
	public float pageWidth 				= 210;
	public float pageHeight 			= 297;
	public PageSizeUnits pageUnit 		= PageSizeUnits.MM;
	public String sizeUnit 				= "mm";
	public int codeWidth;
	public int codeHeight;
	
	public Options() {
		
	}
	
	public void setOptions(String[] options) {
		
		for(int i=0; i<options.length; i++) {
			
			if(options[i]=="templateFile")
				templateFile = Helper.parseArgs(options, "templateFile").trim();
			
			if(options[i]=="outputFile")
				outputFile = "public/"+Helper.parseArgs(options, "outputFile").trim();
			
			if(options[i]=="tmpDir")
				tmpDir = Helper.parseArgs(options, "tmpDir").trim();
			
			if(options[i]=="pageWidth")
				pageWidth =  Float.parseFloat(Helper.parseArgs(options, "pageWidth").trim());
			
			if(options[i]=="pageHeight")
				pageHeight = Float.parseFloat(Helper.parseArgs(options, "pageHeight").trim());
			
			if(options[i]=="codeWidth")
				codeWidth =  Integer.parseInt(Helper.parseArgs(options, "codeWidth").trim());
			
			if(options[i]=="codeHeight")
				codeHeight = Integer.parseInt(Helper.parseArgs(options, "codeHeight").trim());
			
			// unit defaults to millimeters as the default page size is specified in millimeters
			if(options[i]=="sizeUnit")
				sizeUnit = Helper.parseArgs(options, "sizeUnit").trim();
			
			if (sizeUnit == "inches")
				pageUnit = PageSizeUnits.INCHES;
			else
				pageUnit = PageSizeUnits.MM;
			
		}
		
	}
}