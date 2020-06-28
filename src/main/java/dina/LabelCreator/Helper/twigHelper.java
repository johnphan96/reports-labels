package dina.LabelCreator.Helper;

import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import dina.BarCoder.BarCoder;
import dina.LabelCreator.Options.Options;
import net.sf.json.JSONObject;

public class twigHelper {
	
	public Options op;
	
	public twigHelper(Options options)
	{
		this.op = options;
	}
	
	
	
	// Define your Twig functions here
	
	
	/*
	 * Description:		replace a (sub-)string by another (sub-)string
	 * @params:			str = Original string
	 * 					find = the string to be replaced
	 * 					replacement = the string that replaces the (sub-)string in the original string
	 * return			(String) the modified string or the original string if the (sub-)string <find> was not found
	 */
	final SimpleJtwigFunction myReplaceFunction = new SimpleJtwigFunction() {
        @Override
         public String name() {
        	  // Define function name here
              return "replace";
           }

       @Override
       public   Object execute(FunctionRequest request) {
           String re = new String();
            if (request.getNumberOfArguments() == 3 /* Define number of arguments */ ) {
                if (request.get(0) instanceof String && request.get(1) instanceof String && request.get(2) instanceof String) {
                	
                 	// Define the action here
                    String str = request.getEnvironment().getValueEnvironment().getStringConverter().convert(request.get(0));
                    String find = request.getEnvironment().getValueEnvironment().getStringConverter().convert(request.get(1));
                    String replacement = request.getEnvironment().getValueEnvironment().getStringConverter().convert(request.get(2));
                    re = str.replace(find, replacement);
                }
            }

           return (re);
       }
   };
   
   
	/*
	 * Description:		replace a (sub-)string by another (sub-)string, but case insensitive
	 * @params:			str = Original string
	 * 					find = the string to be replaced
	 * 					replacement = the string that replaces the (sub-)string in the original string
	 * return			(String) the modified string or the original string if the (sub-)string <find> was not found
	 */
	final SimpleJtwigFunction myCaseInsensitiveReplaceFunction = new SimpleJtwigFunction() {
        @Override
         public String name() {
        	  // Define function name here
              return "ireplace";
           }

       @Override
       public   Object execute(FunctionRequest request) {
           String re = new String();
            if (request.getNumberOfArguments() == 3 /* Define number of arguments */ ) {
                if (request.get(0) instanceof String && request.get(1) instanceof String && request.get(2) instanceof String) {
                	
                 	// Define the action here
                    String str = request.getEnvironment().getValueEnvironment().getStringConverter().convert(request.get(0)).toLowerCase();
                    String find = request.getEnvironment().getValueEnvironment().getStringConverter().convert(request.get(1)).toLowerCase();
                    String replacement = request.getEnvironment().getValueEnvironment().getStringConverter().convert(request.get(2));
                    re = str.replace(find, replacement);
                }
            }

           return (re);
       }
   };
   
   /*
	 * Description:		replace null by empty string
	 * @params:			str = Original string
	 * 					
	 * return			(String) if null empty string otherwise original string
	 */
	final SimpleJtwigFunction nullToEmptyStringFunction = new SimpleJtwigFunction() {
       @Override
        public String name() {
       	  // Define function name here
             return "null2empty";
          }

      @Override
      public   Object execute(FunctionRequest request) {
    	  String str = new String();
          if (request.getNumberOfArguments() >= 1 /* Define number of arguments */ ) 
          {
        	   
                	// Define the action here
                   str = request.getEnvironment().getValueEnvironment().getStringConverter().convert(request.get(0));
	               if (str == null || str.equals("null") ) {
	               		str = "";
	               }
	               System.out.println("value: "+str);
          }

          return (str);
      }
  };
  
  /*
	 * Description:		format a date string
	 * @params:			date = Original date string
	 * 					sourceFormat = format of the date (if null or empty then try auto-detect format)
	 * 					targetFormat = desired format
	 * return			(String) the modified date string or the original string if the <format> is empty or parding fails
	 */
	final SimpleJtwigFunction dateFormatterFunction = new SimpleJtwigFunction() {
      @Override
       public String name() {
      	  // Define function name here
            return "dateFormat";
         }

     @Override
     public   Object execute(FunctionRequest request) {
         String re = new String();
          if (request.getNumberOfArguments() == 3 /* Define number of arguments */ ) {
              if (request.get(0) instanceof String && request.get(1) instanceof String && request.get(2) instanceof String) {
              	
               	// Define the action here
            	  String sourceFormat = (String) request.get(1);
            	  String targetFormat = (String) request.get(2);
            	  
            	  if(sourceFormat == null || sourceFormat.equals("null") || sourceFormat.trim().isEmpty())
            		  sourceFormat = detectDateFormat(new String((String) request.get(0)).trim());
            	  
					try {
						if(sourceFormat != null)
							re = new SimpleDateFormat(targetFormat).format(new SimpleDateFormat(sourceFormat).parse((String) request.get(0)));
						else
							re = (String) request.get(0);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						re = (String) request.get(0);
					}
              }else
              {
            	  re = null;
              }
          }else
          {
        	  return request.get(0);
          }

         return (re);
     }
     
     protected String detectDateFormat(String inputDate) {
    	 inputDate = inputDate.trim();
         String tempDate = inputDate.replace("/", "*").replace("-", "*").replace(" ", "*").replace(".", "*");
         String sep = null;
         String dateFormat = null;

         if(inputDate.contains("/")) {
        	 sep = "/";
         }else if(inputDate.contains("-")) {
        	 sep = "-";
         }else if(inputDate.contains(" ")) {
        	 sep = " ";
         }else if(inputDate.contains(".")) {
        	 sep = ".";
         }else {
        	 System.out.println("\n\nNo seperator: "+ inputDate + "\n\n");
        	 return null;
         }
         
         // define regex pattern
         String year  =	"([0-9]{4})";
         String month =	"(0[1-9]|1[0-2])";
         String monthString = "([a-z]{3})";
         String day   =	"(0[0-9]|1[0-9]|2[0-9]|3[0-1])";
         
         if (tempDate.matches(year)) {
             dateFormat = "yyyy";
         } else if (tempDate.matches(month+"\\*"+year)) {
             dateFormat = "MM*yyyy";
         } else if (tempDate.matches(day+"\\*"+month+"\\*"+year)) {
             dateFormat = "dd*MM*yyyy";
         } else if (tempDate.matches(month+"\\*"+day+"\\*"+year)) {
             dateFormat = "MM*dd*yyyy";
         } else if (tempDate.matches(year+"\\*"+month+"\\*"+day)) {
             dateFormat = "yyyy*MM*dd";
         } else if (tempDate.matches(year+"\\*"+day+"\\*"+month)) {
             dateFormat = "yyyy*dd*MM";
         } else if (tempDate.matches(day+"\\*"+monthString+"\\*"+year)) {
             dateFormat = "dd*MMM*yyyy";
         } else if (tempDate.matches(monthString+"\\*"+day+"\\*"+year)) {
             dateFormat = "MMM*dd*yyyy";
         } else if (tempDate.matches(year+"\\*"+monthString+"\\*"+day)) {
             dateFormat = "yyyy*MMM*dd";
         } else if (tempDate.matches(year+"\\*"+day+"\\*"+monthString)) {
             dateFormat = "yyyy*dd*MMM";
         } else {
        	 //add your required regex
        	 System.out.println("\n\nNo date match: "+ tempDate + "\n\n");
             return null;
         }
         
         System.out.println(inputDate + "__" + sep + "__" + dateFormat);
         
         return dateFormat.replace("*", sep);

     }
 }; 
  
  
	/*
	 * Description:		Generates a QR-Code, Barcode or Data Matrix image
	 * @params:			data = encoded string
	 * 					filename = filename of the genered image
	 * 					codeFormat = format of the code (QR_CODE, CODE_128, DATA_MATRIX)
	 * return			(String) filename of the generated image file
	 * 
	 */
	final SimpleJtwigFunction generateCodeFunction = new SimpleJtwigFunction() {
       @Override
        public String name() {
       	  // Define function name here
             return "generateCode";
          }

      @Override
      public   Object execute(FunctionRequest request) {
          String re = new String();
          int width = 600;
          int height = 600;
          int margin = 0;
          String para = null;
          
          JSONObject paraJSON = new JSONObject();
           if (request.getNumberOfArguments() == 4 /* Define number of arguments */ ) {
        	   	para = request.getEnvironment().getValueEnvironment().getStringConverter().convert(request.get(3));
        	   	paraJSON = JSONObject.fromObject(para);
           };

	         //check at least the parameters width and height as they are mendatory
	   	   	if(!paraJSON.has("width")) 
	   	   		paraJSON.put("width", width);
	   	   	if(!paraJSON.has("height")) 
	   	   		paraJSON.put("height", height);
	   	   	
	   	   	if(!paraJSON.has("margin")) 
	   	   		paraJSON.put("margin", margin);
           
		   if (request.getNumberOfArguments() >= 3/* Define number of arguments */ ) {           	   
               if (request.get(0) instanceof String && request.get(1) instanceof String && request.get(2) instanceof String) {
               	
                	// Define the action here
                   String data = request.getEnvironment().getValueEnvironment().getStringConverter().convert(request.get(0));
                   String filename = request.getEnvironment().getValueEnvironment().getStringConverter().convert(request.get(1));
                   String codeFormat = request.getEnvironment().getValueEnvironment().getStringConverter().convert(request.get(2));
                   if(codeFormat.equalsIgnoreCase("QR-Code") || codeFormat.equalsIgnoreCase("QR"))
                	   re = BarCoder.createCode(new String[] { data, filename}, op.tmpDir, BarCoder.codeFormats.QR_CODE, paraJSON);
                   if(codeFormat.equalsIgnoreCase("Barcode"))
                	   re = BarCoder.createCode(new String[] { data, filename}, op.tmpDir, BarCoder.codeFormats.CODE_128,  paraJSON);
                   if(codeFormat.equalsIgnoreCase("DataMatrix"))
                	   re = BarCoder.createCode(new String[] { data, filename}, op.tmpDir, BarCoder.codeFormats.DATA_MATRIX, paraJSON);
                   
                   if(!re.isEmpty())
                	   re = op.baseURL +"/" + op.tmpPath + "?f="+ re;
                   
                   System.out.println(re);
                   
                   if(!Helper.checkURL(re, op.debug) && !op.debug)
                	   re = "_";
               }
           }

          return (re);
      }
  };

   public final EnvironmentConfiguration configuration = EnvironmentConfigurationBuilder
	       .configuration()
	       .functions()
	       		// Register your functions here
	           	.add(myReplaceFunction)
	           	.add(myCaseInsensitiveReplaceFunction)
	           	.add(nullToEmptyStringFunction)
	           	.add(generateCodeFunction)
	           	.add(dateFormatterFunction)
	      .and()
      .build();

}
