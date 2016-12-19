package fatjar.implementations.template;

import fatjar.Log;
import fatjar.Template;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateExceptionHandler;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class FreemarkerTemplate implements Template {

    private final Configuration configuration;

    public FreemarkerTemplate() {
	configuration = new Configuration(Configuration.VERSION_2_3_23);
	configuration.setDefaultEncoding("UTF-8");
	configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
	configuration.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_23).build());
    }

    @Override
    public String fromTemplate(String content, Map<String, Object> map) {
	String generatedContent = null;
	try {
	    Writer out = new StringWriter();
	    freemarker.template.Template t = new freemarker.template.Template("templateName", new StringReader(content), configuration);
	    t.process(map, out);
	    generatedContent = out.toString();
	} catch (Exception e) {
	    Log.error("could not generate content from template, error: " + e, e);
	}
	return generatedContent;
    }

}
