/**
 * 
 */
/**
 * @author Falko Glöckler
 *
 */
package dina.TemplateCreator;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import dina.LabelCreator.Options.Options;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import spark.Request;



public class TemplateCreator {
	
	protected static boolean debug;
	protected Options options;
	public String baseURL;
	public String target;
	public Request origReq;
	
	public TemplateCreator(Options op) {
		options = op;
		baseURL = op.baseURL;
	}
	
	public String chooseTemplateForm() {
		
		String out = "";
		out = "<form action=\""+baseURL+"/"+origReq.pathInfo()+"\" method=\"post\">"
				+ "Please choose a template: "
				+ "<select name=\"template\">";
		
		JSONArray tmpl = getTemplates().getJSONArray("templates");
		for(int i=0; i<tmpl.size(); i++) {
	        
        	out += "<option value=\""+tmpl.getJSONObject(i).getString("file")+"\">"+tmpl.getJSONObject(i).getString("name")+"</option>\n";
	        
		
		}
		out += "</select>";
		
		out += "<textarea name=\"data\" style=\"display:none;\">"+origReq.queryParams("data")+"</textarea>\n";

		out += "<input type=\"hidden\" name=\"format\" value=\""+origReq.queryParams("format")+"\"/>\n";
				
		out += "<input type=\"submit\" value=\"Go\"/>"
				+ "</form>";
		
		return out;
	}
	
	public JSONObject getTemplates() {
		
		final File folder = new File(options.templateDir);
	
		JSONArray out = new JSONArray();
		int i = 0;
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            // skip
	        } else {
	        	
	        	JSONObject tmpl = new JSONObject();
	        	tmpl.put("name", FilenameUtils.removeExtension(fileEntry.getName()));
	        	tmpl.put("file", fileEntry.getName());
	        	out.add(i, tmpl);
	        }
	        i++;
		}
		if(debug)
			System.out.println(out);
		JSONObject ret = new JSONObject();
		ret.put("templates", out);
		return ret;
	}
}
	