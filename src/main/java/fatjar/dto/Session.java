package fatjar.dto;

import fatjar.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import java.util.TreeMap;

public class Session extends TreeMap<String, Serializable> {

    private final String rawContent;
    private String secretKey;
    private final String applicationCookieName;

    public Session(String rawContent, String secretKey, String applicationCookieName) {
        // SampleApplication=a2V5MT12YWx1ZTE7a2V5Mj12YWx1ZTI7;SampleApplication_fr_ck_sn_ky=YWRhOTI0M2QyZDA5ZmQwYmQ1ZTM4MGE5ODc4Y2M3YTlhZDA2M2E0MA;
        this.rawContent = rawContent;
        this.secretKey = secretKey;
        this.applicationCookieName = applicationCookieName;
        this.fromCookie();
    }

    private void fromCookie() {
        String applicationCookie = null;
        String applicationCookieValue = null;
        String[] appAndSignCookies = rawContent.split(";");
        /*
         SampleApplication=a2V5MT12YWx1ZTE7a2V5Mj12YWx1ZTI7
         SampleApplication_fr_ck_sn_ky=YWRhOTI0M2QyZDA5ZmQwYmQ1ZTM4MGE5ODc4Y2M3YTlhZDA2M2E0MA
          */
        for (String cookie : appAndSignCookies) {
            if (cookie != null) {
                // SampleApplication=a2V5MT12YWx1ZTE7a2V5Mj12YWx1ZTI7
                String[] cookieKeyValuePair = cookie.trim().split("=");
                if (cookieKeyValuePair.length == 2 &&
                        !"".trim().equals(cookieKeyValuePair[0]) &&
                        !"".trim().equals(cookieKeyValuePair[1]) &&
                        applicationCookieName.equalsIgnoreCase(cookieKeyValuePair[0])) {
                    /*
                       SampleApplication
                       a2V5MT12YWx1ZTE7a2V5Mj12YWx1ZTI7
                     */
                    applicationCookie = cookieKeyValuePair[0].trim();
                    applicationCookieValue = cookieKeyValuePair[1].trim();
                }
            }
        }

        // 1. check if there the application cookie exists
        if (applicationCookie != null) {
            try {
                /*
                a2V5MT12YWx1ZTE7a2V5Mj12YWx1ZTI7
                SampleApplication=RW5jb2RlZEtleT1SVzVqY;SampleApplication_COOKIE_SIGN_KEY=76bb12dc884
                 */
                applicationCookieValue = decode(applicationCookieValue);
                /*
                 SampleApplication=RW5jb2RlZEtleT1SVzVqY
                 SampleApplication_COOKIE_SIGN_KEY=76bb12dc884
                 */
                String appCookieContent = null;
                String signCookieContent = null;
                String[] appAndSignPairs = applicationCookieValue.split(";");
                for (String pair : appAndSignPairs) {
                    String[] keyValue = pair.split("=");
                    if (keyValue.length == 2) {
                        if (keyValue[0].endsWith(SessionKeys.COOKIE_SIGN_KEY.getValue())) {
                            signCookieContent = keyValue[1];
                        } else if (applicationCookieName.equals(keyValue[0])) {
                            appCookieContent = keyValue[1];
                        }
                    }
                }
                if (appCookieContent != null && signCookieContent != null &&
                        signCookieContent.equals(sign(appCookieContent, secretKey))) {
                    /*
                    EncodedKey=RW5jb2RlZFZhbHVlMTExMQ;EncryptedKey=NzcrOTc3KzlMeUowNzc
                     */
                    appCookieContent = decode(appCookieContent);
                    String[] keyValues = appCookieContent.split(";");
                    /*
                    EncodedKey=RW5jb2RlZFZhbHVlMTExMQ
                    EncryptedKey=NzcrOTc3KzlMeUowNzc
                     */
                    for (String keyValue : keyValues) {
                        String[] pair = keyValue.split("=");
                        /*
                        EncodedKey
                        RW5jb2RlZFZhbHVlMTExMQ
                         */
                        if (pair.length == 2) {
                            /*
                            EncodedKey
                            EncodedValue
                             */
                            if (pair[0].endsWith(SessionKeys.COOKIE_ENCRYPTED.toString())) {
                                this.put(pair[0], decrypt(secretKey, encrypt(secretKey, "123").get()));
                            } else {
                                this.put(pair[0], decode(pair[1]));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.error("could not read cookie, rawContent: " + rawContent + " error: " + e);
            }
        }

    }

    public String toCookie() {
        // cookies to set
        StringBuilder cookies = new StringBuilder();
        // it there are key/value pairs, set them to cookie
        try {
            // Set-Cookie: SampleApplication=a2V5MT12YWx1ZTE7a2V5Mj12YWx1ZTI7;\n
            // Set-Cookie: SampleApplication_fr_ck_sn_ky=YWRhOTI0M2QyZDA5ZmQwYmQ1ZTM4MGE5ODc4Y2M3YTlhZDA2M2E0MA;\n

            String applicationCookieKeyValueString = "";
            for (String key : this.keySet()) {
                applicationCookieKeyValueString += key + "=" + this.get(key) + ";";
            }
            String applicationCookieContent = encode(applicationCookieKeyValueString).replace("\n", "").replace("=", "");
            String applicationCookieAsString = applicationCookieName + "=" + applicationCookieContent;
            cookies.append(applicationCookieAsString);
            cookies.append(";");

            String applicationCookieSignatureAsString = applicationCookieName + "_" + SessionKeys.COOKIE_SIGN_KEY.getValue() + "=";

            applicationCookieSignatureAsString += sign(applicationCookieContent, secretKey);
            cookies.append(applicationCookieSignatureAsString);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return applicationCookieName + "=" + encode(cookies.toString());
    }

    public String decode(String value) {
        return new String(Base64.getDecoder().decode(value));
    }

    public String encode(String value) {
        String encoded = new String(Base64.getEncoder().encode(value.getBytes()));
        while (encoded.endsWith("=")) {
            encoded = encoded.substring(0, encoded.length() - 1);
        }
        if (encoded.lastIndexOf('=') != -1) {
            encoded = encoded.substring(0, encoded.lastIndexOf('='));
        }
        return encoded;
    }

    public String decrypt(String secretKey, String value) throws Exception {
        Cipher c = Cipher.getInstance("AES");
        SecretKeySpec k = new SecretKeySpec(secretKey.getBytes(), "AES");
        c.init(Cipher.DECRYPT_MODE, k);
        return new String(c.doFinal(Base64.getDecoder().decode(value)));
    }

    public Optional<String> encrypt(String secretKey, String value) {
        try {
            Cipher c = Cipher.getInstance("AES");
            SecretKeySpec k = new SecretKeySpec(secretKey.getBytes(), "AES");
            c.init(Cipher.ENCRYPT_MODE, k);
            byte[] encrypted = c.doFinal(value.getBytes());
            return Optional.of(Base64.getEncoder().encodeToString(encrypted));
        } catch (Exception e) {
            Log.error("could not encrypt value: " + value + " for key: " + secretKey);
            return Optional.empty();
        }
    }

    private String sign(String value, String secretKey) throws Exception {
        String signValue = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (byte b : md.digest((value + secretKey).getBytes())) {
                signValue += Integer.toString((b & 0xff) + 0x100, 16).substring(1);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw e;
        }
        return signValue;
    }

    public void putEncoded(String key, String value) {
        put(key, encode(value));
    }

    public void putEncrypt(String key, String value) {
        encrypt(secretKey, value).ifPresent(
                encryptedValue -> putEncoded(key + "_" + SessionKeys.COOKIE_ENCRYPTED.toString(), encryptedValue)
        );
    }

    public String getRawContent() {
        return rawContent;
    }
}

