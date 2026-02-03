package MSDNS;

public class Main {
    public static void main(String[] args) {
        DNSServer server = new DNSServer();
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
