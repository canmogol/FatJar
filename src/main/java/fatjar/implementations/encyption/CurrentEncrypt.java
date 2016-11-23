package fatjar.implementations.encyption;

import fatjar.Encrypt;
import fatjar.Log;

public class CurrentEncrypt {

    private CurrentEncrypt() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("never call constructor, use create method");
    }

    @SuppressWarnings("unchecked")
    public static Encrypt create(Encrypt.Type type) {
        Encrypt instance = null;
        String packageName = CurrentEncrypt.class.getPackage().getName();
        try {
            Class<? extends Encrypt> encryptClass = Class.forName(packageName + "." + type.name()).asSubclass(Encrypt.class);
            instance = encryptClass.newInstance();
        } catch (Exception e) {
            Log.error("could not create encryption of type: " + type + " error: " + e);
        }
        return instance;
    }
}
