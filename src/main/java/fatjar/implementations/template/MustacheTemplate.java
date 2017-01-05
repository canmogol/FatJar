package fatjar.implementations.template;

import com.samskivert.mustache.Mustache;
import fatjar.Log;
import fatjar.Template;

import java.util.Map;

public class MustacheTemplate implements Template {

    private final Mustache.Compiler compiler;

    public MustacheTemplate() {
        compiler = Mustache.compiler();
    }

    @Override
    public String fromTemplate(String content, Map<String, Object> map) {
        String generatedContent = null;
        try {
            com.samskivert.mustache.Template template = compiler.compile(content);
            generatedContent = template.execute(map);
        } catch (Exception e) {
            Log.error("could not generate content from template, error: " + e, e);
        }
        return generatedContent;
    }

}
