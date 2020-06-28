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
		return getTemplates("");
	}
	
	public JSONObject getTemplates(String subDir) {
		
		JSONObject ret = new JSONObject();
		final File folder = new File(options.templateDir+"/"+subDir);	
		
		if(folder.isHidden())
			return ret;
		
		ret.put("groups", new JSONArray());
		JSONArray out = new JSONArray();

        // directories need to be listed as groups before files
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory() && !fileEntry.isHidden()) {
	        	JSONObject tmpl = new JSONObject();
	        	JSONArray groups = ret.getJSONArray("groups");
	        	groups.add(groups.size(), subDir+"/"+fileEntry.getName());
	        	ret.put("groups", groups);
	        	tmpl.put(subDir+"/"+fileEntry.getName(), getTemplates(subDir+"/"+fileEntry.getName()));
	        	out.add(out.size(), tmpl);
	        } else {
	        	// skip in order to do the files later
	        }
		};

		// now list the files
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory() || fileEntry.isHidden()) {
	        	// skip in as the directories have already been treated
	        } else {
	        	
	        	JSONObject tmpl = new JSONObject();
	        	tmpl.put("name", FilenameUtils.removeExtension(fileEntry.getName()));
	        	tmpl.put("file", subDir+"/"+fileEntry.getName());
	        	out.add(out.size(), tmpl);
	        }
		}
		
		if(debug)
			System.out.println(out);
		
		if(out.size()>0)
			ret.put("templates", out);
		return ret;
	}
}
	