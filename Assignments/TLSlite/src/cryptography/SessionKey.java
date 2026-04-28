package cryptography;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class SessionKey {

    public SecretKey serverEncryption;
    public SecretKey clientEncryption;

    public SecretKey serverMAC;
    public SecretKey clientMAC;

    public IvParameterSpec serverIV;
    public IvParameterSpec clientIV;

}
