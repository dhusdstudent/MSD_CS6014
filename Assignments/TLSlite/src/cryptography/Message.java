package cryptography;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import java.util.Arrays;

import static cryptography.AES.decrypt;
import static cryptography.HMAC.computeHMAC;
import static cryptography.AES.encrypt;

public class Message {

    public static byte [] encryptMsg(byte[] msg, SecretKey mKey, SecretKey eKey, IvParameterSpec iv) throws Exception{
        try{
            byte [] mac = computeHMAC(mKey.getEncoded(), msg);
            byte[] combo = new byte[msg.length + mac.length];
            System.arraycopy(msg, 0, combo, 0, msg.length);
            System.arraycopy(mac, 0, combo, msg.length, mac.length);

            return encrypt(combo, eKey, iv);
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static byte[] decryptMsg(byte[] ciphertext, SecretKey mKey, SecretKey eKey, IvParameterSpec iv) throws Exception{
        try{
            byte[] combo = decrypt(ciphertext, eKey, iv);

            byte [] msg = Arrays.copyOfRange(combo, 0, combo.length-32);
            byte[] newMac = Arrays.copyOfRange(combo, combo.length-32, combo.length);
            byte[] realMac = computeHMAC(mKey.getEncoded(), msg);

            if(!Arrays.equals(newMac, realMac)){
                throw new RuntimeException("Wrong MAC!");
            }
            return msg;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
