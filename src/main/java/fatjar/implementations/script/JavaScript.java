package fatjar.implementations.script;

import fatjar.Log;
import fatjar.Script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

public class JavaScript implements Script {

    private final ScriptEngine javascriptInterpreter;

    public JavaScript() {
        javascriptInterpreter = new ScriptEngineManager().getEngineByName("nashorn");
    }

    @Override
    public Object evaluate(String content, Map<String, Object> map) {
        Object result = null;
        try {
            for (String name : map.keySet()) {
                javascriptInterpreter.put(name, map.get(name));
            }
            result = javascriptInterpreter.eval(content);
        } catch (ScriptException e) {
            Log.error("could not evaluate the content, error: " + e, e);
        }
        return result;
    }

    @Override
    public Object evaluate(String content) {
        return evaluate(content, new HashMap<>());
    }

}
