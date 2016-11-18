package fatjar;


import fatjar.implementations.template.CurrentTemplate;

import java.util.Map;

public interface Template {

    static Template create() {
        return Template.create(Type.StringTemplate);
    }

    static Template create(Type type) {
        return CurrentTemplate.create(type);
    }

    String fromTemplate(String content, Map<String, Object> values);

    enum Type {
        StringTemplate, FreemarkerTemplate
    }

}
