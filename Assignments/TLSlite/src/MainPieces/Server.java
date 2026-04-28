package MainPieces;

import cryptography.Message;
import cryptography.SessionKey;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public Server(){
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            Socket socket = serverSocket.accept();

            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            //H_Logic.shakeServersHand(in, out);

            SessionKey sKeys = Handshake.shakeServersHand(in, out);
            String firstMessage = "The server talked to you!";
            String secondMessage = "The server talked to you AGAIN!";

            byte[] firstEnc = Message.encryptMsg( firstMessage.getBytes(), sKeys.serverMAC, sKeys.serverEncryption, sKeys.serverIV);
            byte [] secondEnc = Message.encryptMsg(secondMessage.getBytes(), sKeys.serverMAC, sKeys.serverEncryption, sKeys.serverIV);
            out.writeObject(firstEnc);
            out.writeObject(secondEnc);

            byte[] response = (byte[])in.readObject();
            byte [] decryptedResponse = Message.decryptMsg(response, sKeys.clientMAC, sKeys.clientEncryption, sKeys.clientIV);

           System.out.println("And the client says..." + new String(decryptedResponse));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Server();
    }


}
