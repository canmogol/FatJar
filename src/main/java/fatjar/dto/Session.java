package fatjar.dto;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

public class Session extends TreeMap<String, Serializable> {

    private BASE64Encoder base64Encoder = new BASE64Encoder();
    private BASE64Decoder base64Decoder = new BASE64Decoder();
    private String cookieSignSecretKey = "CHANGE_THIS_KEY";
    private String applicationCookieName = "CHANGE_APPLICATION_NAME";
    private String rawContent = "";


    public Session(String rawContent) {
        // SampleApplication=a2V5MT12YWx1ZTE7a2V5Mj12YWx1ZTI7;SampleApplication_fr_ck_sn_ky=YWRhOTI0M2QyZDA5ZmQwYmQ1ZTM4MGE5ODc4Y2M3YTlhZDA2M2E0MA;
        this.rawContent = rawContent;
    }

    public void fromCookie(String applicationCookieName, String cookieSignSecretKey) {
        this.applicationCookieName = applicationCookieName;
        this.cookieSignSecretKey = cookieSignSecretKey;
        String applicationCookieSignedName = applicationCookieName + "_" + SessionKeys.COOKIE_SIGN_KEY.getValue();
        String applicationCookie = null;
        String applicationCookieValue = null;
        String applicationCookieSigned = null;
        String applicationCookieSignedValue = null;
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
                        !"".trim().equals(cookieKeyValuePair[1])) {
                    /*
                       SampleApplication
                       a2V5MT12YWx1ZTE7a2V5Mj12YWx1ZTI7

                       OR

                       SampleApplication_fr_ck_sn_ky
                       YWRhOTI0M2QyZDA5ZmQwYmQ1ZTM4MGE5ODc4Y2M3YTlhZDA2M2E0MA
                     */

                    // this is the SampleApplication
                    if (applicationCookieName.equalsIgnoreCase(cookieKeyValuePair[0])) {
                        applicationCookie = cookieKeyValuePair[0].trim();
                        applicationCookieValue = cookieKeyValuePair[1].trim();

                    } else if (applicationCookieSignedName.equalsIgnoreCase(cookieKeyValuePair[0])) {
                        applicationCookieSigned = cookieKeyValuePair[0].trim();
                        applicationCookieSignedValue = cookieKeyValuePair[1].trim();
                    }

                }
            }
        }

        // 1. check if there are application cookie and its signed form
        // if values found, check if they are correct
        if (applicationCookie != null && applicationCookieSigned != null) {
            // 2.   requestSigned = SHA1(cookieSignSecretKey + applicationCookie)
            try {
                String requestSigned = sign(applicationCookieValue, cookieSignSecretKey);
                // 3.   check(applicationCookieSigned == requestSigned)
                if (applicationCookieSignedValue.equals(requestSigned)) { // the request cookie is authentic
                    // 4.   String decodedApplicationCookie = decode(applicationCookie)
                    String decodedApplicationCookie = decode(applicationCookieValue);
                    // 5.   String[] keyValuePairs = decodedApplicationCookie.split(";")
                    String[] keyValuePairs = decodedApplicationCookie.split(";");
                    // 5.   keyValuePairs.foreach(pair){String[] keyAndValuePair = pair.split("=")}
                    for (String pair : keyValuePairs) {
                        String[] keyAndValuePair = pair.trim().split("=");
                        if (keyAndValuePair.length == 2) {
                            // 6.   {...  put(keyAndValuePair[0],keyAndValuePair[1])   ...}
                            this.put(keyAndValuePair[0].trim(), keyAndValuePair[1].trim()); //   here we add the cookie key / value pairs to Map
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public String toCookie() {
        // if there is nothing to set to cookie, return empty string
        if (this.size() == 0) {
            return "";
        }
        // it there are key/value pairs, set them to cookie
        try {
            String cookieAsString = "";
            // Set-Cookie: SampleApplication=a2V5MT12YWx1ZTE7a2V5Mj12YWx1ZTI7;\n
            // Set-Cookie: SampleApplication_fr_ck_sn_ky=YWRhOTI0M2QyZDA5ZmQwYmQ1ZTM4MGE5ODc4Y2M3YTlhZDA2M2E0MA;\n

            String applicationCookieKeyValueString = "";
            for (String key : this.keySet()) {
                applicationCookieKeyValueString += key + "=" + this.get(key) + ";";
            }
            String applicationCookieContent = encode(applicationCookieKeyValueString).replace("\n", "").replace("=", "");
            String applicationCookieAsString = applicationCookieName + "=" + applicationCookieContent;
            cookieAsString += "Set-Cookie: ";
            cookieAsString += applicationCookieAsString;
            cookieAsString += ";\n";


            String applicationCookieSignatureAsString = applicationCookieName + "_" + SessionKeys.COOKIE_SIGN_KEY.getValue() + "=";

            applicationCookieSignatureAsString += sign(applicationCookieContent, cookieSignSecretKey);
            cookieAsString += "Set-Cookie: ";
            cookieAsString += applicationCookieSignatureAsString;
            cookieAsString += ";\n";

            return cookieAsString;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String decode(String value) throws IOException {
        return new String(base64Decoder.decodeBuffer(value));
    }

    public String encode(String value) {
        String encoded = base64Encoder.encode(value.getBytes());
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
        return new String(c.doFinal(value.getBytes()));
    }

    public String encrypt(String secretKey, String value) throws Exception {
        try {
            Cipher c = Cipher.getInstance("AES");
            SecretKeySpec k = new SecretKeySpec(secretKey.getBytes(), "AES");
            c.init(Cipher.ENCRYPT_MODE, k);
            return new String(c.doFinal(value.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new Exception("could not encrypt value!");
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

    public void putEncrypt(String secretKey, String key, String value) throws Exception {
        putEncoded(key, encode(encrypt(secretKey, value)));
    }

    /*
    setters & getters
     */

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public String getRawContent() {
        return rawContent;
    }
}
