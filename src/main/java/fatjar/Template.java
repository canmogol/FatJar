package fatjar;


import fatjar.implementations.template.CurrentTemplate;

import java.util.HashMap;
import java.util.Map;

public interface Template {

    static Template create() {
        return Template.create(Type.FreemarkerTemplate);
    }

    static Template create(Type type) {
        return CurrentTemplate.create(type);
    }

    String fromTemplate(String content, Map<String, Object> values);

    default String fromTemplate(String content, Object... keyValuePairs) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < keyValuePairs.length; i = i + 2) {
            map.put(String.valueOf(keyValuePairs[i]), keyValuePairs[i + 1]);
        }
        return fromTemplate(content, map);
    }

    enum Type {
        FreemarkerTemplate, MustacheTemplate
    }

}
