package msd.benjones;

//Implement the onInit and onDistanceMessage methods in the Router class of the following code:
//
//Implement the method so that the routers use the Bellman-Ford algorithm to compute routing information. Use the static
//methods in the Network class to help you with this. These methods shouldn't be more than 20 lines of code or so!
//
//Once you have a working implementation, test your algorithm on a variety of network sizes. Plot the number of messages
//required to converge as a function of network size. Since the networks are probablisitically generated, you might want
//to try several networks of each size to get a sense of the distribution.

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static msd.benjones.Network.*;

public class Router {

    private HashMap<Router, Integer> distances;
    private String name;
    public Router(String name) {
        this.distances = new HashMap<>();
        this.name = name;
    }

    //TODO: IMPLEMENT ME
    public void onInit() throws InterruptedException {
        Set <Router> routers = getRouters();
        for (Router router : routers) {
            distances.put(router, Integer.MAX_VALUE);
        }

        distances.put(this, 0);

        HashSet <Neighbor> neighbors = getNeighbors(this);

        for (Neighbor n : neighbors ){
            distances.put(n.router, n.cost);
        }

        HashMap allDistance = new HashMap<>(distances);

        for (Neighbor n : neighbors) {
            sendDistanceMessage(new Message(this, n.router, allDistance));
        }
		//As soon as the network is online,
		//fill in your initial distance table and broadcast it to your neighbors
    }

    //TODO: IMPLEMENT ME
    public void onDistanceMessage(Message message) throws InterruptedException {

        boolean didChange = false;
        int currentCost = distances.get(message.sender);

        Set <Router> routerKeys = message.distances.keySet();
        for (Router router : routerKeys) {
            int newCost = currentCost + message.distances.get(router);

            if (newCost < distances.get(router)) {
                distances.put(router, newCost);
                didChange = true;
            }
        }

        if (didChange) {
            HashSet <Neighbor> neighbors = getNeighbors(this);
            HashMap allDistance = new HashMap<>(distances);
            for (Neighbor n : neighbors){
                sendDistanceMessage(new Message(this, n.router, allDistance));
            }
        }
		//update your distance table and broadcast it to your neighbors if it changed
    }


    public void dumpDistanceTable() {
        System.out.println("router: " + this);
        for(Router r : distances.keySet()){
            System.out.println("\t" + r + "\t" + distances.get(r));
        }
    }

    @Override
    public String toString(){
        return "Router: " + name;
    }
}
