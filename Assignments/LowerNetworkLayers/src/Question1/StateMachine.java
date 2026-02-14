package Question1;//We want to send data from one node to two other nodes using over a simple broadcast channel. Specifically, we want to
// design a protocol for reliably sending data from host S to hosts R1 and R2 over this channel. The channel can lose
// or corrupt packets for independently. For example, a packet sent by S might be received by R1 but not R2.
//
//When there are collisions on the broadcast channel, you can assume that the receiving hosts will detect them as
// corrupt packets. If data needs to be resent, you can ignore random backoffs, etc, and assume that eventually the
// colliding hosts will be able to resend their data without interference.
//
//Design the protocol state machines for S and R (both R1 and R2 should use the same protocol).
//
//Use the primitives we discussed in the notes (udt_send and udt_receive, etc). Don't consider pipelining. The RDT
// protocol we developed with sequence numbers 0 or 1 and with timeouts is a good starting point.


public class StateMachine{
    public static void main(String[] args){
    Receiver r1 = new Receiver("R1");
    Receiver r2 = new Receiver("R2");
        Sender sender = new Sender(r1, r2);

    sender.sendData("Howdy");

    int ack1 = r1.receive(new Packet(0, "Howdy"));
    sender.getAck(ack1, r1.getName());

    sender.timeout();

    int ack2 = r2.receive(new Packet(2, "Howdy"));
    sender.getAck(ack2, r2.getName());
    }
}




;

