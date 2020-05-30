/**
 * 
 */
package dina.LabelCreator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import org.jtwig.resource.reference.ResourceReference;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.PageSizeUnits;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import dina.BarCoder.BarCoder;
import dina.LabelCreator.Helper.Helper;
import dina.LabelCreator.Helper.twigHelper;
import dina.LabelCreator.Options.Options;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Falko Gloeckler
 *
 */
public class LabelCreator {
	
	protected boolean debug;
	protected String templateFile;
	protected String outputFile;
	protected String tmpDir, tmpPath;
	protected String sizeUnit;
	protected PageSizeUnits pageUnit;
	protected float pageWidth, pageHeight; 
	protected int codeWidth, codeHeight;
	protected String sess_uuid;
	private int dataInteration = 0;
	
	protected Options options;//= new Options();
	
	public String jsonData;
	public String baseURL; 
	
	ArrayList<String> systemPlaceholder = new ArrayList<String>(); 
	ArrayList<String> customPlaceholder = new ArrayList<String>();
	ArrayList<String> funcPlaceholder = new ArrayList<String>();
	Map<String, String> localVariables = new HashMap<String, String>();
	
	
	//ArrayList<ArrayList<String>> data =  new ArrayList<ArrayList<String>>();
	
	public int getDataInteration() {
		return dataInteration;
	}
	
	private void setDataInteration(int i) {
		dataInteration = i;
	}
	
	public LabelCreator(Options op, String data) {
		
		options = op;
		debug = op.debug;
		templateFile = op.templateFile;
		outputFile = op.outputFile;
		tmpDir = op.tmpDir;
		tmpPath = op.tmpPath;
		baseURL = op.baseURL;
		sizeUnit = op.sizeUnit;
		pageUnit = op.pageUnit;
		pageWidth = op.pageWidth;
		pageHeight = op.pageHeight;
		codeWidth = op.codeWidth;
		codeHeight = op.codeHeight;
		
		registerPlaceholders();		
		
		if(!op.sessionIsSet)
		{	
			//session based output file
			sess_uuid = UUID.randomUUID().toString();
			outputFile = outputFile.replaceFirst("\\.", "__"+sess_uuid+".");
			op.outputFile = outputFile;
			op.sessionIsSet = true;
		}
		
		setData(data);
		
		//clean-up old tmp files
		op.cleanUp();
	}
	
	protected void registerPlaceholders() {
		// In the template the system placeholders need be wrapped in triple brackets {{{ }}}, but here without brackets
		systemPlaceholder.add("START REPEAT");
		systemPlaceholder.add("END REPEAT");
		systemPlaceholder.add("START LABEL");
		systemPlaceholder.add("START LABEL");
		systemPlaceholder.add("DATE");
		systemPlaceholder.add("COUNT LABEL");
		systemPlaceholder.add("COUNT REPEAT");
		
		// In the template the functional placeholders need be wrapped in triple brackets {{% %}}, but here without brackets
		funcPlaceholder.add("GET");
		funcPlaceholder.add("SET");
		funcPlaceholder.add("PREV");
		funcPlaceholder.add("NEXT");
		funcPlaceholder.add("EACH");
		funcPlaceholder.add("QR-Code");
		funcPlaceholder.add("Barcode");
		funcPlaceholder.add("DataMatrix");
		funcPlaceholder.add("IF");
		funcPlaceholder.add("EQUALS");
		funcPlaceholder.add("EQUALSI");
		funcPlaceholder.add("IFINARRAY");
		funcPlaceholder.add("REPLACE");
		funcPlaceholder.add("REPLACELAST");
		funcPlaceholder.add("TOLOWER");
		funcPlaceholder.add("TOUPPER");
		funcPlaceholder.add("TRIM");
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
                     //template = parseTemplate();
                     template = parseTwigTemplate("PDF");
                     
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
	
	public String parseTwigTemplate() throws MalformedURLException, IOException{
		return parseTwigTemplate(null);
	}
	
	public String parseTwigTemplate(String format) throws MalformedURLException, IOException{
		
		String twig = null;
	
		ArrayList<Object> data = Helper.jsonStringToArray( jsonData );
		
		java.util.ResourceBundle.clearCache();

		twigHelper twigConf = new twigHelper(options);
		
		JtwigTemplate template = JtwigTemplate.fileTemplate(new File(templateFile).getAbsolutePath(), twigConf.configuration);
		JtwigModel model = JtwigModel.newModel().with("dataArray", data);
		
		model.with("format", format);

		if(options.debug)
			System.out.println(data);	
		
		twig = template.render(model);
		
		return twig;
	}
	
	
	
	public String parseTemplate(){
		String html = "";
		String repeat = "";
		
		Path path = Paths.get(templateFile);
		Charset charset = StandardCharsets.UTF_8;

		try {
			html = new String(Files.readAllBytes(path), charset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// find the system-defined placeholders for metadata
		html = html.replaceAll("\\{\\{\\{DATE\\}\\}\\}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString() );
		
		// find the system-defined placeholders for repeating the template patterns
		Pattern repeater = Pattern.compile("\\{\\{\\{START REPEAT\\}\\}\\}(.*?)\\{\\{\\{END REPEAT\\}\\}\\}", Pattern.DOTALL);
		Matcher matcher = repeater.matcher(html);
		if (matcher.find())
		{
		   repeat = matcher.group(1);
		}
		
		// Determine the amount of items in the template's repeatable area
        int numRepeatedItems = repeat.split("\\{\\{\\{START LABEL\\}\\}\\}",-1).length - 1;
        
        if(debug)
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
        System.out.println(customPlaceholder);
       
        int numRepeatedInserts = numRepeatedItems;

        // insert the repeated pattern until there are enough custom placeholders
        while(data.size()>numRepeatedInserts && numRepeatedInserts>0) {
        	html = html.replaceFirst("\\{\\{\\{END REPEAT\\}\\}\\}", "{{{END REPEAT}}}" + repeat );
        	numRepeatedInserts += numRepeatedItems;
        }
        
        
        int r = 1;
        Pattern pat_rep = Pattern.compile("\\{\\{\\{COUNT REPEAT\\}\\}\\}", Pattern.DOTALL);
		Matcher match_rep = pat_rep.matcher(html);
        while (match_rep.find())
        {
        	html = html.replaceFirst("\\{\\{\\{COUNT REPEAT\\}\\}\\}", Integer.toString(r));
        	r++;
        }
        
        	
      //  int i = 0;
        int v = 0;
        List<String> htmlParts = Arrays.asList(html.split("\\{\\{\\{START LABEL\\}\\}\\}",-1));
      //  System.out.println(htmlParts.size());
    	/*for(String label : htmlParts) {*/
        for(int i=0; i<data.size(); i++) 
        {
        	setDataInteration(i);
    		for (String pattern : customPlaceholder) 
    		{		
    			int l = i+1;
    			String label = htmlParts.get(l);
    			
        	//while(htmlPart.get(i).contains("{{"+ pattern + "}}") && v<data.size() ) {
        		//System.out.println(v);
        		if(data.getJSONObject(v).get(pattern) != null) {
		        	/*if(pattern.equals("QR-Code")) {
		        	        
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
		        		
		        	}else  */
        				
        				// replace system placeholders for label counter  numRepeatedItems
        				label = label.replace("{{{COUNT LABEL}}}", Integer.toString(l));
        				
		        		htmlParts.set(l, label.replaceAll(Pattern.quote("{{"+ pattern + "}}"), data.getJSONObject(v).get(pattern).toString()));
        				//htmlParts.set(l, label.replaceAll("\\{\\{"+ pattern + "\\}\\}", escapeHtml(data.getJSONObject(v).get(pattern).toString())));
        				
        				
        				//System.out.println(escapeHtml(data.getJSONObject(v).get(pattern).toString()));
        		}
	        	//System.out.println("pattern "+v+ "\""+pattern+"\": "+escapeHtml(data.getJSONObject(v).get(pattern).toString()));
        		//System.out.println(htmlParts.get(i));
        		
	        	
			}
    		
    		//execute functional placeholders
    		//for (String func : funcPlaceholder) 
    		//{
    			int l = i+1;
    			String label = htmlParts.get(l);
    			
				//htmlParts.set(l, label.replaceAll("\\{\\{%"+ func + " "+params.replaceAll("\\|",  "\\\\|").replaceAll("\\}",  "\\\\}").replaceAll("\\{",  "\\\\{") +"%\\}\\}", replacement));
    			htmlParts.set(l, nested_func(label));
	        	//replacement = "";
	        	//params = "";
	        	//l++;
    		//}
    		
    		//System.out.println(codesForCleanUp.toString());
    	   // remove all remaining custom placeholders in the last label section
    	   htmlParts.set(v, htmlParts.get(v).replaceAll("\\{\\{.*\\}\\}", " "));
    	    
        	v++;
        }
    	html = String.join("{{{START LABEL}}}", htmlParts);
      
        // remove all system placeholders
        for(String rm : systemPlaceholder) {
        	html = html.replaceAll("\\{\\{\\{"+ rm + "\\}\\}\\}", "");
        }
	    
        // remove all remaining function placeholders
	    html = html.replaceAll("\\{\\{%.*\\%}\\}", "");
	    
	    // remove all remaining custom placeholders
	    html = html.replaceAll("\\{\\{.*\\}\\}", "");
	    
		return(html);
	}

	private String nested_func(String label) {
		
			for (String func : funcPlaceholder) {
				if((label.contains("{{%"+func+" ") || label.contains("\\{\\{%"+func+" ")))
					label = replace_func(label, func);
			}
		return label;
	}
	
	private String replace_func(String label, String func) {
		
		String params = "";
		String replacement ="";
		List<String> paramsArray = new ArrayList<>();
		String[] codeArray = null;
		
		List<String> funcParams = Arrays.asList(label.split("\\{\\{%"+func+" "));
		//System.out.println(funcParams+"\n-------------------");
		
		if(funcParams.size()>1)
		{
			paramsArray = Arrays.asList(funcParams.get(1).split("%"+func+"\\}\\}"));
			if(func.equalsIgnoreCase("EACH"))
			{
				params = paramsArray.get(0);
				paramsArray = Arrays.asList(params.split("\\|", 3));
				//System.out.println(paramsArray.get(2));
			}else
			{	
				params = nested_func(paramsArray.get(0));
				//params = paramsArray.get(0);
				paramsArray = Arrays.asList(params.split("\\|"));
			}
			//System.out.println(label);
		}
		
		if(paramsArray.size()>1)
		{
			// reference to previous data record (params: key, value)
			if(func.equalsIgnoreCase("PREV")) {
				int di = getDataInteration()-1;
				JSONObject d = new JSONObject();
				String dat = "";
				if(di>=0 && paramsArray.get(0)!=null)
				{
					d = JSONArray.fromObject( jsonData ).getJSONObject(di);
					if(d != null && d.get(paramsArray.get(0))!=null)
						dat = d.get(paramsArray.get(0)).toString();
				}
				if(dat != null)
				{
					replacement = dat;
				}
				else
				{
					replacement = "";
				}
			}
			
			// reference to next data record (params: key, value)
			if(func.equalsIgnoreCase("NEXT")) {
				int di = getDataInteration()+1;
				JSONObject d = new JSONObject();
				String dat = "";
				if(di<JSONArray.fromObject( jsonData ).size() && paramsArray.get(0)!=null)
				{
					d = JSONArray.fromObject( jsonData ).getJSONObject(di);
					if(d != null && d.get(paramsArray.get(0))!=null)
						dat = d.get(paramsArray.get(0)).toString();
				}
				if(dat != null)
				{
					replacement = dat;
				}
				else
				{
					replacement = "";
				}
			}
			
			// set local variable (params: key, value)
			if(func.equalsIgnoreCase("SET")) {
        		replacement = "";
        		localVariables.put(paramsArray.get(0), paramsArray.get(1));
        		//System.out.println("SET "+localVariables);
			}
			
			// get local variable (param: key)
			if(func.equalsIgnoreCase("GET")) {
				if(localVariables.keySet().contains(paramsArray.get(0)))
					replacement = localVariables.get(paramsArray.get(0));
				else
					replacement = "";
        		//System.out.println("GET "+paramsArray.get(0)+"="+localVariables.get(paramsArray.get(0)));
			}
						
			// simple boolean conditioning 
			if(func.equalsIgnoreCase("IF")) {
        		replacement = (!paramsArray.get(0).trim().equals("0") && !paramsArray.get(0).trim().isEmpty() ? paramsArray.get(1) : paramsArray.get(2));
			}
			
			// simple case-sensitive string comparison 
			if(func.equalsIgnoreCase("EQUALS")) {
        		replacement = (paramsArray.get(0).equals(paramsArray.get(1)) ? paramsArray.get(2) : paramsArray.get(3));
        		//System.out.println(paramsArray);
			}
			
			// simple case-insensitive string comparison 
			if(func.equalsIgnoreCase("EQUALSI")) {
        		replacement = (paramsArray.get(0).equalsIgnoreCase(paramsArray.get(1)) ? paramsArray.get(2) : paramsArray.get(3));
        		//System.out.println(paramsArray.get(0)+ "====>" +paramsArray.get(1)+ "====>" +paramsArray.get(0).equalsIgnoreCase(paramsArray.get(1)));
			}
					
			// check if string is in character separated values (params: separated string, separator, if action, else action)
			if(func.equalsIgnoreCase("IFINARRAY")) {
				String separator = paramsArray.get(1);
				//System.out.println(separator);
				boolean contains = Arrays.asList(paramsArray.get(0).split(separator)).contains(paramsArray.get(2));
        		replacement = (contains ? paramsArray.get(3) : paramsArray.get(4));
        		//System.out.println(replacement);
			}
			
			// replace string (params: original string, regex, replacement)
			if(func.equalsIgnoreCase("REPLACE")) {
				replacement = paramsArray.get(0).replaceAll(Pattern.quote(paramsArray.get(1)), paramsArray.get(2));
			}
			
			// replace last occurrence of string (params: original string, regex, replacement)
			if(func.equalsIgnoreCase("REPLACELAST")) {
				String str = paramsArray.get(0);
				int ind = str.lastIndexOf(paramsArray.get(1));
				if( ind>=0 )
				    str = new StringBuilder(str).replace(ind, ind+1,paramsArray.get(2)).toString();
				replacement = str;
			}
			
			// replace string to lower case (params: original string)
			if(func.equalsIgnoreCase("TOLOWER")) {
				replacement = paramsArray.get(0).toLowerCase();
			}
			
			// replace string to upper case (params: original string)
			if(func.equalsIgnoreCase("TOUPPER")) {
				replacement = paramsArray.get(0).toLowerCase();
			}
			
			// trim whitespace in string (params: original string)
			if(func.equalsIgnoreCase("TRIM")) {
				replacement = paramsArray.get(0).trim();
			}
		
			
			// generate data codes & replace placeholders by image path
			if(func.equalsIgnoreCase("QR-Code") || func.equalsIgnoreCase("Barcode") || func.equalsIgnoreCase("DataMatrix"))
			{
				String filename = "";
				codeArray = new String[] { escapeHtml(paramsArray.get(0).toString()), escapeHtml(paramsArray.get(1).toString()) };
				
				if(func.equalsIgnoreCase("QR-Code")) {  
        			filename = BarCoder.createCode(codeArray, tmpDir, BarCoder.codeFormats.QR_CODE);
        			
	        	}else if(func.equalsIgnoreCase("Barcode")) { 
	        		filename = BarCoder.createCode(codeArray, tmpDir, BarCoder.codeFormats.CODE_128);
	        		
	        	}else if(func.equalsIgnoreCase("DataMatrix")) {
	        		filename = BarCoder.createCode(codeArray, tmpDir, BarCoder.codeFormats.DATA_MATRIX);
	        	}
        		
				replacement =  baseURL +"/" + tmpPath + "?f="+ filename;
        		if(!Helper.checkURL(replacement.replaceAll("\\{\\{.*\\}\\}", ""), debug) && !debug)
        			replacement = "_";
			}        		
			
			// do something for each element in character separated values (params: separated string, separator, replacement)
			if(func.equalsIgnoreCase("EACH")) {
				String separator = paramsArray.get(1);
				List<String> elements = Arrays.asList(paramsArray.get(0).split(separator));
        		for(int x=0; x<elements.size(); x++) {
        			elements.set(x, paramsArray.get(2).replaceAll(Pattern.quote("{{%s}}"), elements.get(x)));
        			//ystem.out.println(elements.get(x));
        		}
        		replacement = String.join(separator, elements);
        		//System.out.println(paramsArray.get(2));
        		//System.out.println(replacement);
			}
		}
		
		//System.out.println("Before ------  " +label );
		//params = params.replaceAll("\\|",  "\\\\|").replaceAll("\\}",  "\\\\}").replaceAll("\\{",  "\\\\{") ;
		String rep = label.replaceAll(Pattern.quote("{{%"+ func + " "+ params +"%"+ func + "}}"), replacement);
		//System.out.println("After ------  " +label );
		if(rep.contains("{{%"+ func + " "))
		{
			//System.out.println("\\{\\{%"+ func + " "+params.replaceAll("\\|",  "\\\\|").replaceAll("\\}",  "\\\\}").replaceAll("\\{",  "\\\\{") +"%"+ func + "\\}\\}");
			rep = replace_func(rep, func);
		}
		return rep;
	}

	public void setData(String data) {
		
		if(data==null || data.isEmpty())
			data="[{}]";
		
		//TODO: Do some validation before passing the data to the jsonData variable!
		jsonData = data;
	}	
}
