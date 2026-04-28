package cryptography;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AES {
    public static byte[] encrypt(byte[] text, SecretKey key, IvParameterSpec iv) throws Exception{
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            return cipher.doFinal(text);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static byte[] decrypt(byte[] text, SecretKey key, IvParameterSpec iv) throws Exception{
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return cipher.doFinal(text);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
