package fatjar.implementations.script;

import fatjar.Log;
import fatjar.Script;

import java.util.HashMap;
import java.util.Map;

public class CurrentScript {

    private static final Map<String, Script> scriptMap = new HashMap<>();

    private CurrentScript() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("never call constructor, use create method");
    }

    @SuppressWarnings("unchecked")
    public static Script create(Script.Type type) {
        if (!scriptMap.containsKey(type.name())) {
            String packageName = CurrentScript.class.getPackage().getName();
            try {
                Class<? extends Script> scriptClass = Class.forName(packageName + "." + type.name()).asSubclass(Script.class);
                Script instance = scriptClass.newInstance();
                scriptMap.put(type.name(), instance);
            } catch (Exception e) {
                Log.error("could not create script of type: " + type + " error: " + e);
            }
        }
        return scriptMap.get(type.name());
    }
}
