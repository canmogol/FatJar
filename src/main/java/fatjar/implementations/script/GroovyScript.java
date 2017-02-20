package fatjar.implementations.script;

import fatjar.Log;
import fatjar.Script;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilationFailedException;

import java.util.HashMap;
import java.util.Map;

public class GroovyScript implements Script {

    private final GroovyShell shell;

    public GroovyScript() {
        shell = new GroovyShell();
    }

    @Override
    public Object evaluate(String content, Map<String, Object> map) {
        Object result = null;
        try {
            for (String name : map.keySet()) {
                shell.setVariable(name, map.get(name));
            }
            result = shell.evaluate(content);
        } catch (CompilationFailedException e) {
            Log.error("could not evaluate the content, error: " + e, e);
        }
        return result;
    }

    @Override
    public Object evaluate(String content) {
        return evaluate(content, new HashMap<>());
    }

}
