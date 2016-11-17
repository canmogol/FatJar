package fatjar.implementations.xml;

import fatjar.Log;
import fatjar.XML;

public class CurrentXML {

    private CurrentXML() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("never call constructor, use create method");
    }

    @SuppressWarnings("unchecked")
    public static XML create(XML.Type type) {
        XML xml = null;
        String packageName = CurrentXML.class.getPackage().getName();
        try {
            Class<? extends XML> xmlClass = Class.forName(packageName + "." + type.name()).asSubclass(XML.class);
            xml = xmlClass.newInstance();
        } catch (Exception e) {
            Log.error("could not create xml of type: " + type + " error: " + e);
        }
        return xml;
    }

}
