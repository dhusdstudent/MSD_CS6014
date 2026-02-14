package Question1;

public class Packet {
    int seqNum;
    String data;
    boolean isCorrupted;

    public Packet(int seqNum, String data){ //What the sender actually 'sends'
        this.seqNum = seqNum;
        this.data = data;
        isCorrupted = false;
    }

}
