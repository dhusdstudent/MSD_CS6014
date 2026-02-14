package msd.benjones;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        msd.benjones.Network.makeSimpleNetwork(); //use this for testing/debugging
        //Network.makeProbablisticNetwork(5); //use this for the plotting part
        msd.benjones.Network.dump();

        msd.benjones.Network.startup();
        msd.benjones.Network.runBellmanFord();

        System.out.println("done building tables!");
        for(msd.benjones.Router r : msd.benjones.Network.getRouters()){
            r.dumpDistanceTable();
        }
        System.out.println("total messages: " + msd.benjones.Network.getMessageCount());


    }
}
