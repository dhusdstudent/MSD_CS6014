package Question1;

public class Sender{
//
    enum SendState { WAIT_CALL_0, WAIT_ACK_0, WAIT_CALL_1, WAIT_ACK_1}

    private SendState sendState =  SendState.WAIT_CALL_0;
    private boolean ackR1;
    private boolean ackR2;

    private Receiver firstGuy;
    private Receiver secondGuy;

    private Packet recentPacket;

    public Sender(){
    }

    public Sender(Receiver firstGuy, Receiver secondGuy){
        this.firstGuy = firstGuy;
        this.secondGuy = secondGuy;
    }

    public void sendData(String data) {
        if (sendState == SendState.WAIT_CALL_0) {
           recentPacket = new Packet(0, data);
            udt_send(recentPacket);
            sendState = SendState.WAIT_ACK_0;
        } else if (sendState == SendState.WAIT_CALL_1) {
            recentPacket = new Packet(1, data);
            udt_send(recentPacket);
            sendState = SendState.WAIT_ACK_1;
        }
    }

    public void getAck(int ackNum, String name){

        if (ackNum == 0 && sendState == SendState.WAIT_ACK_0) {
            getAckFrom(name);

            if (bothAcks()){
                makeAcksFalse();
                sendState = SendState.WAIT_CALL_1;
            }
        } else if (ackNum == 1 && sendState == SendState.WAIT_ACK_1) {
            getAckFrom(name);
            if (bothAcks()){
                makeAcksFalse();
                sendState = SendState.WAIT_CALL_0;
            }
        }
    }

    public void udt_send(Packet recentPacket){
        System.out.println("udt_send: " + this.recentPacket.seqNum);
    }

    public void timeout(){
        if (sendState == SendState.WAIT_ACK_1 || sendState == SendState.WAIT_ACK_0) {
            System.out.println("Resending " + recentPacket.seqNum + " due to timeout");
            udt_send(recentPacket);
        }
    }

    private void makeAcksFalse(){
        ackR1 = false;
        ackR2 = false;
    }

    private void getAckFrom(String name){
        if (name.equals("R1")) ackR1 = true;
        else if (name.equals("R2")) ackR2 = true;
    }

    private boolean bothAcks(){
        return ackR1 && ackR2;
    }
}