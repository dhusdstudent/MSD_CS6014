package cryptography;

import java.math.BigInteger;
import java.security.SecureRandom;

public class DiffieHellman {
    private static BigInteger g = BigInteger.valueOf(2);
    private static BigInteger N = new BigInteger(
            "FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD1"
            + "29024E088A67CC74020BBEA63B139B22514A08798E3404DD"
            + "EF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245"
            + "E485B576625E7EC6F44C42E9A63A3620FFFFFFFFFFFFFFFF",
            + 16);

    public static BigInteger makePrivateVal(){
        SecureRandom sr = new SecureRandom();
        return new BigInteger(2048,sr);
    }

    public static BigInteger analyzePubVal(BigInteger val){
        return g.modPow(val, N);
    }

    public static BigInteger shareSecret(BigInteger yourVal, BigInteger myVal){
        return yourVal.modPow(myVal, N);
    }
}
