/**
 * 
 */
package dina.LabelCreator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder.PageSizeUnits;

import dina.BarCoder.BarCoder;
import dina.LabelCreator.Options.Options;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Falko Gloeckler
 *
 */
public class LabelCreator {
	
	protected String templateFile;
	protected String outputFile;
	protected String tmpDir;
	protected String sizeUnit;
	protected PageSizeUnits pageUnit;
	protected float pageWidth, pageHeight; 
	protected int codeWidth, codeHeight;
	
	public String jsonData;
	public String baseURL; 
	
	ArrayList<String> systemPlaceholder = new ArrayList<String>(); 
	ArrayList<String> customPlaceholder = new ArrayList<String>();
	
	
	//ArrayList<ArrayList<String>> data =  new ArrayList<ArrayList<String>>();
	
	public LabelCreator(Options op) {
		templateFile = op.templateFile;
		outputFile = op.outputFile;
		tmpDir = op.tmpDir;
		sizeUnit = op.sizeUnit;
		pageUnit = op.pageUnit;
		pageWidth = op.pageWidth;
		pageHeight = op.pageHeight;
		codeWidth = op.codeWidth;
		codeHeight = op.codeHeight;		
		
		registerPlaceholders();
	}
	
	public LabelCreator() {
		registerPlaceholders();
	}
	
	protected void registerPlaceholders() {
		// In the template the system placeholders need be wrapped in triple brackets {{{ }}}, but here without brackets
		systemPlaceholder.add("START REPEAT");
		systemPlaceholder.add("END REPEAT");
		systemPlaceholder.add("START LABEL");
		systemPlaceholder.add("START LABEL");
		
		// In the template the custom placeholders need be wrapped in double brackets {{ }}, but here without brackets
		customPlaceholder.add("QR-Code");
		customPlaceholder.add("Barcode");
		customPlaceholder.add("DataMatrix");
		//customPlaceholder.add("inventoryNumber");
	}
	
	public void createPDF()
	{
          
          /*	
            	String[] test = new String[2];
	            test[0] = "http://coll.mfn-berlin.de/u/ZMB_Phasm_D0001"; 
	            test[1] = "ZMB_Phasm_D0001";
	            
	            BarCoder.createCode(test, "C:\\Temp\\", BarCoder.codeFormats.CODE_128);
          */ 
            String template = new String();
            
            OutputStream os = null;
              try {
               os = new FileOutputStream(outputFile);
       
               try {
                     PdfRendererBuilder builder = new PdfRendererBuilder();
                    /* if(Helper.validatePath(templateFile))
                    	 builder.withUri(templateFile);
                     else
                     {
                    	File file = new File(templateFile);
                    	try {
                    		builder.withFile(file);
                    	} catch (Exception f) {
                    		System.out.println("Template not found: "+templateFile);
                    		//f.printStackTrace();
                    	}
                     }*/
                     template = parseTemplate();
                     builder.withHtmlContent(template, "/");
                     
                     builder.useDefaultPageSize(pageWidth, pageHeight, pageUnit);
                     builder.toStream(os);
                     builder.run();
                     
               } catch (Exception e) {
                     e.printStackTrace();
                     // LOG exception
               } finally {
                     try {
                            os.close();
                     } catch (IOException e) {
                            // swallow
                     }
               }
              }
              catch (IOException e) {
                     e.printStackTrace();
                     // LOG exception.
              }
	}
	
	
	public String parseTemplate(){
		String html = "";
		String repeat = "";
		
		//TODO:  Load template, find and replace placeholders
		
		Path path = Paths.get(templateFile);
		Charset charset = StandardCharsets.UTF_8;

		try {
			html = new String(Files.readAllBytes(path), charset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// find the system-defined placeholders for repeating the template patterns
		Pattern repeater = Pattern.compile("\\{\\{\\{START REPEAT\\}\\}\\}(.*?)\\{\\{\\{END REPEAT\\}\\}\\}", Pattern.DOTALL);
		Matcher matcher = repeater.matcher(html);
		if (matcher.find())
		{
		   repeat = matcher.group(1);
		}
		
		// Determine the amount of items in the template's repeatable area
        int numRepeatedItems = repeat.split("\\{\\{\\{START LABEL\\}\\}\\}",-1).length - 1;
        
/*		
		//TODO in the future the JSON object will be provided via the API instead of the file		
		
		String json = new String();
        Path jsonFile = Paths.get("templates/data2.json");
		try {
			json = new String(Files.readAllBytes(jsonFile), charset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
		System.out.println("jsonData received: " +jsonData) ; 
        JSONArray data = JSONArray.fromObject( jsonData );
       // JSONArray data = JSONArray.fromObject( jsonData ).getJSONObject(0).getJSONArray("data");

        // register all keys for the templates
        for (int i = 0; i < data.size(); i++) {
            JSONObject object = data.optJSONObject(i);
            Iterator<String> iterator = object.keys();
            while(iterator.hasNext()) {
              String currentKey = iterator.next();
              if(!customPlaceholder.contains(currentKey))
            	  customPlaceholder.add(currentKey);
            }
       }
       
        int numRepeatedInserts = numRepeatedItems;
        
        // insert the repeated pattern until there are enough custom placeholders
        while(data.size()>numRepeatedInserts && numRepeatedInserts>0) {
        	html = html.replaceFirst("\\{\\{\\{END REPEAT\\}\\}\\}", "{{{END REPEAT}}}" + repeat );
        	numRepeatedInserts += numRepeatedItems;
        }
        	
      //  int i = 0;
        int v = 0;
        List<String> htmlParts = Arrays.asList(html.split("\\{\\{\\{START LABEL\\}\\}\\}",-1));
        System.out.println(htmlParts.size());
    	/*for(String label : htmlParts) {*/
        for(int i=0; i<data.size(); i++) {
    		for (String pattern : customPlaceholder) {
    			int l = i+1;
    			String label = htmlParts.get(l);
        	//while(htmlPart.get(i).contains("{{"+ pattern + "}}") && v<data.size() ) {
        		//System.out.println(v);
        		if(data.getJSONObject(v).get(pattern) != null) {
		        	if(pattern.equals("QR-Code")) {
		        	        
		        		String[] codeArray = new String[] { escapeHtml(data.getJSONObject(v).get("QR-Code").toString()), escapeHtml(data.getJSONObject(v).get("inventoryNumber").toString()) };
		        		String codePath = baseURL +"/" + tmpDir+ "?f="+ BarCoder.createCode(codeArray, tmpDir, BarCoder.codeFormats.QR_CODE);
		        		//System.out.println(baseURL);
		        		htmlParts.set(l, label.replaceAll("\\{\\{"+ pattern + "\\}\\}", codePath));
		        		
		        	}else if(pattern.equals("Barcode")) {
	        	        
		        		String[] codeArray = new String[] { escapeHtml(data.getJSONObject(v).get("Barcode").toString()), escapeHtml(data.getJSONObject(v).get("inventoryNumber").toString()) };
		        		String codePath =  baseURL +"/" + tmpDir+ "?f="+ BarCoder.createCode(codeArray, tmpDir, BarCoder.codeFormats.CODE_128);
		        		
		        		htmlParts.set(l, label.replaceAll("\\{\\{"+ pattern + "\\}\\}", codePath));
		        		
		        	}else if(pattern.equals("DataMatrix")) {
	        	        
		        		String[] codeArray = new String[] { escapeHtml(data.getJSONObject(v).get("DataMatrix").toString()), escapeHtml(data.getJSONObject(v).get("inventoryNumber").toString()) };
		        		String codePath =  baseURL +"/" + tmpDir+ "?f="+ BarCoder.createCode(codeArray, tmpDir, BarCoder.codeFormats.DATA_MATRIX);
		        		
		        		htmlParts.set(l, label.replaceAll("\\{\\{"+ pattern + "\\}\\}", codePath));
		        		
		        	}else  
		        		htmlParts.set(l, label.replaceAll("\\{\\{"+ pattern + "\\}\\}", escapeHtml(data.getJSONObject(v).get(pattern).toString())));
        		}
	        	//System.out.println("pattern "+v+ "\""+pattern+"\": "+escapeHtml(data.getJSONObject(v).get(pattern).toString()));
        		//System.out.println(htmlParts.get(i));
	        	
			}
    	//	System.out.println("----------------------------------");
        //	i++;
    		
    		// remove all remaining custom placeholders in the last label section
    	   htmlParts.set(v, htmlParts.get(v).replaceAll("\\{\\{.*\\}\\}", " "));
    	    
        	v++;
        }
    	html = String.join("{{{START LABEL}}}", htmlParts);
      
        // remove all system placeholders
        for(String rm : systemPlaceholder) {
        	html = html.replaceAll("\\{\\{\\{"+ rm + "\\}\\}\\}", "");
        }
	        
	    // remove all remaining custom placeholders
	    html = html.replaceAll("\\{\\{.*\\}\\}", "");
        
		  
		return(html);
	}

	public void setData(String data) {
		
		//TODO: Do some validation before passing the data to the jsonData variable!
		
		jsonData = data;
	}
}
