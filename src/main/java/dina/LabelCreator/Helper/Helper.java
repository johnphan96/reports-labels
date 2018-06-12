/**
 * 
 */
package dina.LabelCreator.Helper;

import org.apache.commons.validator.routines.UrlValidator;

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
	
	static public boolean validatePath(String url)
	{
		UrlValidator urlValidator = new UrlValidator();
		return urlValidator.isValid(url);
	}
}
