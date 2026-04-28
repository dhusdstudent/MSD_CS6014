package cryptography;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HMAC {

    public static byte [] computeHMAC(byte[] key, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException {
    Mac mac = Mac.getInstance("HmacSHA256");
    SecretKeySpec keySpec = new SecretKeySpec(key, "HmacSHA256");
    mac.init(keySpec);
    return mac.doFinal(data);
    }
}
