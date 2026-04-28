package MainPieces;

import cryptography.DiffieHellman;
import cryptography.HDKF;
import cryptography.RSA;
import cryptography.SessionKey;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;

public class Handshake {

    public static SessionKey shakeClientsHand(ObjectInputStream in, ObjectOutputStream out) throws Exception {

        FileUtils files = new FileUtils();

        Certificate clientCert = files.loadCert("CASignedClientCertificate.pem");
        PrivateKey clientKey = files.loadPrivateKey("clientPrivateKey.key");
        Certificate caCert = files.loadCert("CAcertificate.pem");

        SecureRandom rand = new SecureRandom();
        byte[] nonce = new byte[32];
        rand.nextBytes(nonce);
        out.writeObject(nonce);

        Certificate serverCert = (Certificate) in.readObject();
        BigInteger serverPub = (BigInteger) in.readObject();
        byte[] serverSig = (byte[]) in.readObject();

        serverCert.verify(caCert.getPublicKey());
        if (!RSA.verifySig(serverPub.toByteArray(), serverSig, serverCert.getPublicKey())) {
            throw new RuntimeException("Invalid server signature");
        }

        BigInteger clientPriv = DiffieHellman.makePrivateVal();
        BigInteger clientPub = DiffieHellman.analyzePubVal(clientPriv);

        byte[] clientSig = RSA.signData(clientPub.toByteArray(), clientKey);

        out.writeObject(clientCert);
        out.writeObject(clientPub);
        out.writeObject(clientSig);

        BigInteger shared = DiffieHellman.shareSecret(serverPub, clientPriv);

        return HDKF.makeKey(nonce, shared.toByteArray());
    }

    public static SessionKey shakeServersHand(ObjectInputStream in, ObjectOutputStream out) throws Exception {
        FileUtils files = new FileUtils();

        Certificate serverCert = files.loadCert("CASignedServerCertificate.pem");
        PrivateKey serverKey = files.loadPrivateKey("serverPrivateKey.der");
        Certificate caCert = files.loadCert("CAcertificate.pem");

        byte[] nonce = (byte[]) in.readObject();

        BigInteger serverPriv = DiffieHellman.makePrivateVal();
        BigInteger serverPub = DiffieHellman.analyzePubVal(serverPriv);

        byte[] sig = RSA.signData(serverPub.toByteArray(), serverKey);

        out.writeObject(serverCert);
        out.writeObject(serverPub);
        out.writeObject(sig);

        Certificate clientCert = (Certificate) in.readObject();
        BigInteger clientPub = (BigInteger) in.readObject();
        byte[] clientSig = (byte[]) in.readObject();

        clientCert.verify(caCert.getPublicKey());

        if (!RSA.verifySig(clientPub.toByteArray(), clientSig, clientCert.getPublicKey())) {
            throw new RuntimeException("Invalid client signature");
        }

        BigInteger shared = DiffieHellman.shareSecret(clientPub, serverPriv);
        return HDKF.makeKey(nonce, shared.toByteArray());
    }
}


