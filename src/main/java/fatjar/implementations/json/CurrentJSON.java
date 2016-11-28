package fatjar.implementations.json;

import fatjar.JSON;
import fatjar.Log;

import java.util.HashMap;
import java.util.Map;

public class CurrentJSON {

    private static final Map<String, JSON> jsonMap = new HashMap<>();

    private CurrentJSON() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("never call constructor, use create method");
    }

    @SuppressWarnings("unchecked")
    public static JSON create(JSON.Type type) {
        if (!jsonMap.containsKey(type.name())) {
            String packageName = CurrentJSON.class.getPackage().getName();
            try {
                Class<? extends JSON> jsonClass = Class.forName(packageName + "." + type.name()).asSubclass(JSON.class);
                JSON instance = jsonClass.newInstance();
                jsonMap.put(type.name(), instance);
            } catch (Exception e) {
                Log.error("could not create json of type: " + type + " error: " + e, e);
            }
        }
        return jsonMap.get(type.name());
    }
}
