package fatjar;

import fatjar.implementations.encyption.CurrentEncrypt;

public interface Encrypt {

    static Encrypt create() {
        return Encrypt.create(Type.TEAEncrypt);
    }

    static Encrypt create(Type type) {
        return CurrentEncrypt.create(type);
    }

    byte[] encrypt(byte[] key, byte[] clear);

    byte[] decrypt(byte[] key, byte[] encrypted);

    enum Type {
        TEAEncrypt, AESEncrypt
    }

}
