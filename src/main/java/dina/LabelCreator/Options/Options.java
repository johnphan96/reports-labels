package dina.LabelCreator.Options;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.PageSizeUnits;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import dina.LabelCreator.Helper.Helper;

public class Options 
{
	// set default values
	public boolean debug				= false;
	public String templateFile 			= "template.html";
	public String outputFile 			= "tmp/labels.pdf";
	public String tmpDir 				= "tmp";
	public String tmpPath				= "tmp";
	public String baseURL 				= "";
	public float pageWidth 				= 210;
	public float pageHeight 			= 297;
	public static PageSizeUnits pageUnit = PageSizeUnits.MM;
	public String sizeUnit 				= "mm";
	public int codeWidth;
	public int codeHeight;
	public String templateDir			= "templates";
	public boolean sessionIsSet         = false;;
	
	public Options() {
		templateFile = templateDir+"/"+templateFile;
	}
	
	public void setOptions(String[] options) {
		
		for(int i=0; i<options.length; i++) {
			
			if(options[i]=="templateFile")
				templateFile = Helper.parseArgs(options, "templateFile").trim();
			
			if(options[i]=="outputFile")
				outputFile = "public/"+Helper.parseArgs(options, "outputFile").trim();
			
			if(options[i]=="tmpDir")
				tmpDir = Helper.parseArgs(options, "tmpDir").trim();
			
			if(options[i]=="baseURL")
				baseURL = Helper.parseArgs(options, "baseURL").trim();
			
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
	
	public void cleanUp() {
	
		Long now = System.currentTimeMillis();
		Long deleteAfter = new Long(300000); //5 minutes
		final File folder = new File(tmpDir);
		
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            // skip
	        } else {
	        	//check timestamp
	        	//System.out.println("delete tmp file: "+fileEntry.lastModified()+"    "+fileEntry.getName()+"\nnow:"+now);
	        	if((fileEntry.lastModified()+deleteAfter)<now)
	        	{
	        		if(debug)
	        			System.out.println("delete tmp file: "+fileEntry.getName());
	        		fileEntry.delete();
	        	}
	        }
	    }
	}
}