package MainPieces;

import cryptography.Message;
import cryptography.SessionKey;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    public Client(){
        try {
            Socket socket = new Socket("localhost", 12345);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            //H_Logic.shakeClientsHand(ois, oos);

            SessionKey keys = Handshake.shakeClientsHand(ois, oos);

            for (int i = 0; i < 2; i++){
                byte[] ciphertext = (byte[]) ois.readObject();
                byte[] plaintext = Message.decryptMsg(
                        ciphertext, keys.serverMAC, keys.serverEncryption, keys.serverIV);
                System.out.println("Server: " + new String(plaintext));
            }
            String reply = "The client got your message! :)";
            byte [] encryptedStuff = Message.encryptMsg( reply.getBytes(),
                    keys.clientMAC, keys.clientEncryption, keys.clientIV);
            oos.writeObject(encryptedStuff);

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Client();
    }

}
