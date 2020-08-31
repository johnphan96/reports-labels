/**
 * 
 */
package dina.LabelCreator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.UUID;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.PageSizeUnits;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import dina.LabelCreator.Helper.Helper;
import dina.LabelCreator.Helper.twigHelper;
import dina.LabelCreator.Options.Options;

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
	private int dataIteration = 0;
	
	protected Options options;//= new Options();
	
	public String jsonData;
	public String baseURL; 
	
	public int getDataIteration() {
		return dataIteration;
	}
	
	private void setDataIteration(int i) {
		dataIteration = i;
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

	public void createPDF()
	{
		    String template = new String();
            
            OutputStream os = null;
              try {
               os = new FileOutputStream(outputFile);
       
               try {
                     PdfRendererBuilder builder = new PdfRendererBuilder();
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
		model.with("baseURL", baseURL);
		model.with("staticFiles", baseURL+"/static");

		if(options.debug)
			System.out.println(data);	
		
		twig = template.render(model);
		
		return twig;
	}
	
	
	public void setData(String data) {
		
		if(data==null || data.isEmpty())
			data="[{}]";
		
		//TODO: Do some validation before passing the data to the jsonData variable!
		jsonData = data;
	}	
}
