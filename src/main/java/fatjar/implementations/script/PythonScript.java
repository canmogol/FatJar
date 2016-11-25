package fatjar.implementations.script;

import java.util.HashMap;
import java.util.Map;

import fatjar.Script;
import org.python.util.PythonInterpreter;

public class PythonScript implements Script {

    private final PythonInterpreter pythonInterpreter;

    public PythonScript() {
        pythonInterpreter = new PythonInterpreter();
    }

    @Override
    public Object evaluate(String content, Map<String, Object> map) {
        for (String name : map.keySet()) {
            pythonInterpreter.set(name, map.get(name));
        }
        pythonInterpreter.exec(content);
        return null;
    }

    @Override
    public Object evaluate(String content) {
        return evaluate(content, new HashMap<>());
    }

}
