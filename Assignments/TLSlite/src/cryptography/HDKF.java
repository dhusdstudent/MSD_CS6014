package cryptography;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

import static cryptography.HMAC.computeHMAC;

public class HDKF {
    public static byte[] hdkfExpand(byte[] in, String tag){
//   okm = HMAC(key = input,  data = tag concatenated with a byte with value 1)
//   return first 16 bytes of okm
        try {
            byte[] tagBytes = tag.getBytes();

            byte[] combo = new byte[tagBytes.length + 1];
            System.arraycopy(tagBytes, 0, combo, 0, tagBytes.length);
            combo[tagBytes.length] = 1;

            byte[] result = computeHMAC(in, combo);
            return Arrays.copyOf(result, 16);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SessionKey makeKey(byte [] none, byte[] secret){
//   prk = HMAC(key = clientNonce, data = sharedSecretFromDiffieHellman)
//   serverEncrypt = hkdfExpand(prk, "server encrypt")
//   clientEncrypt = hkdfExpand(serverEncrypt, "client encrypt")
//   serverMAC = hkdfExpand(clientEncrypt, "server MAC")
//   clientMAC = hkdfExpand(serverMAC, "client MAC")
//   serverIV = hkdfExpand(clientMAC, "server IV")
//   clientIV = hkdfExpand(serverIV, "client IV")
        try{
            SessionKey keys =  new SessionKey();

            byte[] prk = computeHMAC(none, secret);
            //-----
            byte [] serverEncBytes = hdkfExpand(prk, "server encryption");
            byte[] clientEncBytes = hdkfExpand(serverEncBytes, "client encryption");
            //-----
            byte [] serverMAC = hdkfExpand(clientEncBytes, "server mac encryption");
            byte [] clientMAC = hdkfExpand(serverMAC, "client mac encryption");
            //----
            byte[] serverIVBytes = hdkfExpand(clientMAC, "server IV");
            byte[] clientIVBytes = hdkfExpand(serverIVBytes, "client IV");

            keys.serverEncryption = new SecretKeySpec(serverEncBytes, "AES");
            keys.clientEncryption = new SecretKeySpec(clientEncBytes, "AES");

            keys.serverMAC = new SecretKeySpec(serverMAC, "HmacSHA256");
            keys.clientMAC = new SecretKeySpec(clientMAC, "HmacSHA256");

            keys.serverIV = new IvParameterSpec(serverIVBytes);
            keys.clientIV = new IvParameterSpec(clientIVBytes);

            return keys;

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
