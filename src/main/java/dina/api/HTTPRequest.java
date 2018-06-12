package dina.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPRequest {

	public static String method 			= "GET";
	public static String contentType		= "text/plain";
	public static String targetURL 			= "http://localhost";
	public static String urlParameters		= "";
	public static String contentLanguage 	= "en-US";
	public static String userAgent			= "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:56.0) Gecko/20100101 Firefox/56.0";
	
// Example 
	
/*	HTTPRequest r = new HTTPRequest();
	r.method = "GET";
	return r.exec("http://www.indobiosys.org/sites/default/scripts/createHexdec.php", "reason=id.indobiosys.org&amount=3"); //"spec="+swaggerJson.toString());
	
*/
	
	public static String exec() {
		return exec(targetURL, urlParameters);
	}
	public static String exec(String targetURL, String urlParameters) {
		  HttpURLConnection connection = null;
	
		  if(!urlParameters.isEmpty() && method.equalsIgnoreCase("GET")) {
			  targetURL = targetURL + "?" + urlParameters;
			  urlParameters = "";
		  }
		  
		  try {
		    //Create connection
		    URL url = new URL(targetURL);
		    connection = (HttpURLConnection) url.openConnection();
		    connection.setRequestMethod(method);
		    connection.setRequestProperty("User-Agent", userAgent);
		    connection.setRequestProperty("Content-Type",contentType);
		    connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
		    connection.setRequestProperty("Content-Language", contentLanguage);  
	
		    System.out.println(targetURL);
		    System.out.println(urlParameters);
		    System.out.println(contentType);
		    System.out.println(contentLanguage);
		    System.out.println(Integer.toString(urlParameters.getBytes().length));
		    System.out.println(method);
		    
		    
		    connection.setUseCaches(false);
		    
		    //Send post request
		    if(!urlParameters.isEmpty() && method.equalsIgnoreCase("POST")) {
					    
				connection.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();
				wr.close();
		    }
	
		    int responseCode = connection.getResponseCode();
		    
		    //Get Response  
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
		    String line;
		    while ((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    return response.toString();
		  } catch (Exception e) {
		    e.printStackTrace();
		    return null;
		  } finally {
		    if (connection != null) {
		      connection.disconnect();
		    }
		  }
		}
}