package fatjar.implementations.template;


import fatjar.Log;
import fatjar.Template;

public class CurrentTemplate {

    private CurrentTemplate() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("never call constructor, use create method");
    }

    @SuppressWarnings("unchecked")
    public static Template create(Template.Type type) {
        Template template = null;
        String packageName = CurrentTemplate.class.getPackage().getName();
        try {
            Class<? extends Template> templateClass = Class.forName(packageName + "." + type.name()).asSubclass(Template.class);
            template = templateClass.newInstance();
        } catch (Exception e) {
            Log.error("could not create template of type: " + type + " error: " + e, e);
        }
        return template;
    }
}
