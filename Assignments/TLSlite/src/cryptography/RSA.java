package cryptography;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

public class RSA {

    public static byte[] signData(byte[] data, PrivateKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static boolean verifySig(byte[] data, byte [] sig, PublicKey pubKey) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.DECRYPT_MODE, pubKey);
    byte[] decrypted = cipher.doFinal(sig);
    return Arrays.equals(data, decrypted);
    }
}
