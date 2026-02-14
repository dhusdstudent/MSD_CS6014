package Question1;

public class Receiver{
    public Receiver() {
    }
    enum ReceiveState {WAIT_0, WAIT_1};

    private ReceiveState state = ReceiveState.WAIT_0;
    private String name;

    public Receiver(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public int receive(Packet packet) {
        System.out.println("I've received the packet: " + packet.seqNum);

        if (packet.seqNum == 0 && state == ReceiveState.WAIT_0) {
            System.out.println(name + "'s delivery data is/was " + packet.data);
            state = ReceiveState.WAIT_1;
            return 0; // B/c ACK was 0
        } else if (packet.seqNum == 1 && state == ReceiveState.WAIT_1) {
            System.out.println(name + "'s delivery data is/was " + packet.data);
            state = ReceiveState.WAIT_0;
            return 1; //B/c ACK was 1
        } else if (packet.isCorrupted) {
            System.out.println("...but " + packet.seqNum + " is corrupted!");
            return -1; //There was no ACK
        } else {
            System.out.println(name + " is a duplicate packet!");
            return packet.seqNum; //If it doesn't fall into an above category, assume dupe
        }
    }

}
