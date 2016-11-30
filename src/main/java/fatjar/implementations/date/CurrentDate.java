package fatjar.implementations.date;


import fatjar.Date;
import fatjar.Log;

import java.util.HashMap;
import java.util.Map;

public class CurrentDate {

    private static final Map<String, Date> dateMap = new HashMap<>();

    private CurrentDate() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("never call constructor, use create method");
    }

    @SuppressWarnings("unchecked")
    public static Date create(Date.Type type, String url) {
        String key = type.name() + "/" + url;
        if (!dateMap.containsKey(key)) {
            String packageName = CurrentDate.class.getPackage().getName();
            try {
                Class<? extends Date> dateClass = Class.forName(packageName + "." + type.name()).asSubclass(Date.class);
                Date instance = dateClass.getConstructor(String.class).newInstance(url);
                dateMap.put(key, instance);
            } catch (Exception e) {
                Log.error("could not create date of type: " + type + " for url: " + url + " error: " + e, e);
            }
        }
        return dateMap.get(key);
    }

}
