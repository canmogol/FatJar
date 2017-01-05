package fatjar.implementations.script;

import fatjar.Script;
import org.jruby.embed.ScriptingContainer;

import java.util.HashMap;
import java.util.Map;

public class RubyScript implements Script {

    private final ScriptingContainer container;

    public RubyScript() {
	container = new ScriptingContainer();
    }

    @Override
    public Object evaluate(String content, Map<String, Object> map) {
	for (String name : map.keySet()) {
	    container.put(name, map.get(name));
	}
	return container.runScriptlet(content);
    }

    @Override
    public Object evaluate(String content) {
	return evaluate(content, new HashMap<>());
    }

}
