package MSDNS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class DNSServer {

    private static final int PORT = 8053;
    private static final String googleDNS = "8.8.8.8";
    private final DNSCache cache = new DNSCache();

    public void start() throws IOException {
        DatagramSocket socket = new DatagramSocket(PORT);
        byte[] buf = new byte[512];

        System.out.println("Starting DNS server on port " + PORT);

        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            byte[] data = Arrays.copyOf(packet.getData(), packet.getLength());
            DNSMessage message = DNSMessage.decodeMessage(data);

            DNSRecord[] answers = new DNSRecord[message.questions.length];

            for (int i = 0; i < message.questions.length; i++) {
                DNSQuestion quest = message.questions[i];
                DNSRecord myCache = cache.lookup(quest);

                if (myCache != null) {
                    answers[i] = myCache;
                } else {
                    DNSMessage googleResp = passToGoogle(data);
                        if (googleResp.answers.length == 0) {
                            answers[i] = null;
                            continue;
                        }
                    DNSRecord record = googleResp.answers[0];
                    cache.insert(quest, record);
                    answers[i] = record;
                }
            }

            DNSMessage response = DNSMessage.buildResponse(message, answers);
            byte[] outboundBytes = response.toBytes();

            DatagramPacket respPacket = new DatagramPacket(outboundBytes, outboundBytes.length, packet.getAddress(), packet.getPort());
            socket.send(respPacket);
        }
    }

    private DNSMessage passToGoogle(byte[] data) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress googleAdd = InetAddress.getByName(DNSServer.googleDNS);

        DatagramPacket packet = new DatagramPacket(data, data.length, googleAdd, 53);
        socket.send(packet);

        byte[] buffer = new byte[512];
        DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(responsePacket);

        socket.close();
        return DNSMessage.decodeMessage(Arrays.copyOf(responsePacket.getData(), responsePacket.getLength()));
    }
}