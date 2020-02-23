package dina.LabelCreator.Helper;

import org.jtwig.environment.EnvironmentConfiguration;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.functions.FunctionRequest;
import org.jtwig.functions.SimpleJtwigFunction;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

import dina.BarCoder.BarCoder;
import dina.LabelCreator.Options.Options;

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
           if (request.getNumberOfArguments() == 3 /* Define number of arguments */ ) {
               if (request.get(0) instanceof String && request.get(1) instanceof String && request.get(2) instanceof String) {
               	
                	// Define the action here
                   String data = request.getEnvironment().getValueEnvironment().getStringConverter().convert(request.get(0));
                   String filename = request.getEnvironment().getValueEnvironment().getStringConverter().convert(request.get(1));
                   String codeFormat = request.getEnvironment().getValueEnvironment().getStringConverter().convert(request.get(2));
                   if(codeFormat.equalsIgnoreCase("QR-Code") || codeFormat.equalsIgnoreCase("QR"))
                	   re = BarCoder.createCode(new String[] { data, filename}, op.tmpDir, BarCoder.codeFormats.QR_CODE);
                   if(codeFormat.equalsIgnoreCase("Barcode"))
                	   re = BarCoder.createCode(new String[] { data, filename}, op.tmpDir, BarCoder.codeFormats.CODE_128);
                   if(codeFormat.equalsIgnoreCase("DataMatrix"))
                	   re = BarCoder.createCode(new String[] { data, filename}, op.tmpDir, BarCoder.codeFormats.DATA_MATRIX);
                   
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
	      .and()
      .build();

}
