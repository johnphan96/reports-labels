package dina.LabelCreator.Options;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.PageSizeUnits;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import dina.LabelCreator.Helper.Helper;

public class Options 
{
	// set default values
	public boolean debug				= false;
	public String templateFile 			= "defaultTemplate.twig";
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
		setDefaultOptions();
	}
	
	private void setDefaultOptions() {
		setDefaultOptions("config.ini");
	}
	private void setDefaultOptions(String configFile) {
		
		File config = new File(configFile);
		if(config.exists())
		{
			try {
				Ini ini = new Ini(config);

				String debugString = ini.get("system", "debug") ==null ? "false" : ini.get("system", "debug");
				if(debugString.equalsIgnoreCase("true") || debugString.equalsIgnoreCase("1"))
					debug = true;
		
				baseURL = ini.get("system", "baseURL") ==null ? "" : ini.get("system", "baseURL"); 
				
				tmpDir = ini.get("system", "tmpDir") ==null ? "tmp" : ini.get("system", "tmpDir"); 
				//remove leading slash in path
				if(tmpDir.substring(0,1).equals("/"))
					tmpDir = tmpDir.substring(1,tmpDir.length());
			
				outputFile = ini.get("system", "defaultOutputFile") ==null ? "tmp/labels.pdf" : ini.get("system", "defaultOutputFile"); 
				//remove leading slash in path
				if(outputFile.substring(0,1).equals("/"))
					outputFile = outputFile.substring(1, outputFile.length());
				
				templateFile = ini.get("labels", "defaultTemplate") ==null ? "defaultTemplate.twig" : ini.get("labels", "defaultTemplate"); 
				templateFile = templateDir+"/"+templateFile;
				pageWidth =  ini.get("labels", "pageWidth") ==null ? 210 : Integer.parseInt(ini.get("labels", "pageWidth")); 
				pageHeight = ini.get("labels", "pageHeight") ==null ? 297 : Float.parseFloat(ini.get("labels", "pageHeight")); 
				codeWidth =  ini.get("labels", "codeWidth") ==null ? 10 : Integer.parseInt(ini.get("labels", "codeWidth")); 
				codeHeight = ini.get("labels", "codeHeight") ==null ? 10 : Integer.parseInt(ini.get("labels", "codeHeight")); 
				// unit defaults to millimeters as the default page size is specified in millimeters
				sizeUnit = ini.get("labels", "sizeUnit") ==null ? "mm" : ini.get("labels", "sizeUnit"); 
				
				if (sizeUnit == "inches")
					pageUnit = PageSizeUnits.INCHES;
				else
					pageUnit = PageSizeUnits.MM;
				
			} catch (InvalidFileFormatException e) {
				System.err.println("Invalid Config File Format!!!");
				System.exit(1);
			} catch (IOException e) {
				
				System.err.println("Could not open config.ini file.");
				System.exit(1);
			}
		}else
		{
			System.err.println("config.ini doesn't exist!!! "+ config.getAbsoluteFile());
			System.exit(1);
		}
		
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
		System.out.println(tmpDir);
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