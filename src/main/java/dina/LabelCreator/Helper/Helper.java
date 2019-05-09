/**
 * 
 */
package dina.LabelCreator.Helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;


import org.apache.commons.validator.routines.UrlValidator;
//import org.w3c.tidy.Tidy;
import org.w3c.tidy.Tidy;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;

/**
 * @author Falko Gloeckler
 *
 */
public class Helper {
	
	static public String parseArgs(String[] args, String param)
	{
		String value="";
		if(args!=null && param!=null)
			for(int i=0; i<args.length; i++)
			{
				// compare case-insensitive by converting to lower case
				if(args[i].toLowerCase().startsWith(param.toLowerCase()+"="))
					value=args[i].split("=")[1];
			}
		return value;
	}
	
	
	public static ArrayList<Object> jsonStringToArray(String jsonString) throws JSONException {
		
	    ArrayList<Object> objectArray = new ArrayList<Object>();

	    JSONArray jsonArray = JSONArray.fromObject( jsonString );

	    for (int i = 0; i < jsonArray.size(); i++) {
	    	
	        objectArray.add(jsonArray.get(i));
	    }

	    return objectArray;
	}
	
	static public boolean validatePath(String url)
	{
		return validatePath(url, false);
	}
	
	static public boolean validatePath(String url, boolean debug)
	{
		UrlValidator urlValidator = new UrlValidator();
		return urlValidator.isValid(url);
	}
	
	static public boolean checkURL(String url)
	{
		return checkURL(url, false);
	}
	
	static public boolean checkURL(String url, boolean debug)
	{
		URL u;
		HttpURLConnection con;

		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("GET");
			int code = con.getResponseCode();
			if(code==200)
				return true;
			else
			{
				if(debug)
					System.out.println("URL response code: "+code+ " at "+url);
				return false;
			}
			
		} catch (MalformedURLException e) {
			if(debug)
				System.out.println("Malformed URL: "+url);
			return false;
		} catch (IOException e) {
			if(debug)
				System.out.println("No Connection to URL: "+url);
			return false;
		}
	}
	
	static private boolean isValidHTML(String htmlData){
		return isValidHTML(htmlData, false);
	}
	static private boolean isValidHTML(String htmlData, boolean debug){
		   Tidy tidy = new Tidy();
		   InputStream stream = new ByteArrayInputStream(htmlData.getBytes());
		   
		   tidy.parse(stream, System.out);
		   
		   return (tidy.getParseErrors() == 0);
		   //return true;
		}
}
