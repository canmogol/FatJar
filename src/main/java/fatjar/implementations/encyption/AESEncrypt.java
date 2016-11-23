package fatjar.implementations.encyption;

import fatjar.Encrypt;
import fatjar.Log;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AESEncrypt implements Encrypt {

    @Override
    public byte[] encrypt(byte[] key, byte[] clear) {
        byte[] encrypted = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            SecretKeySpec k = new SecretKeySpec(key, "AES");
            c.init(Cipher.ENCRYPT_MODE, k);
            encrypted = c.doFinal(clear);
        } catch (Exception e) {
            Log.error("could not (>)ENCRYPT, check key and setup, key length: " + key.length + " encrypted length: " + clear.length);
        }
        return encrypted;
    }

    @Override
    public byte[] decrypt(byte[] key, byte[] encrypted) {
        byte[] decrypted = null;
        try {
            Cipher c = Cipher.getInstance("AES");
            SecretKeySpec k = new SecretKeySpec(key, "AES");
            c.init(Cipher.DECRYPT_MODE, k);
            decrypted = c.doFinal(encrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            Log.error("could not DECRYPT(<), check key and setup, key length: " + key.length + " encrypted length: " + encrypted.length);
        }
        return decrypted;
    }

}
