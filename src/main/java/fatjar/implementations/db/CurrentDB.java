package fatjar.implementations.db;

import fatjar.DB;
import fatjar.Log;

import java.util.HashMap;
import java.util.Map;

public class CurrentDB {

    private static final Map<String, DB> dbMap = new HashMap<>();

    private CurrentDB() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("never call constructor, use create method");
    }

    @SuppressWarnings("unchecked")
    public static DB create(DB.Type type, String name) {
        String key = type.name() + "/" + name;
        if (!dbMap.containsKey(key)) {
            String packageName = CurrentDB.class.getPackage().getName();
            try {
                Class<? extends DB> dbClass = Class.forName(packageName + "." + type.name()).asSubclass(DB.class);
                DB instance = dbClass.getConstructor(String.class).newInstance(name);
                dbMap.put(key, instance);
            } catch (Exception e) {
                Log.error("could not create db of type: " + type + " error: " + e, e);
            }
        }
        return dbMap.get(key);
    }
}
