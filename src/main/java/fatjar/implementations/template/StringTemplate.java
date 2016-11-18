package fatjar.implementations.template;

import fatjar.Template;
import org.stringtemplate.v4.ST;

import java.util.Map;

public class StringTemplate implements Template {

    @Override
    public String fromTemplate(String content, Map<String, Object> map) {
        ST st = new ST(content);
        for (String key : map.keySet()) {
            st.add(key, map.get(key));
        }
        return st.render();
    }

}
