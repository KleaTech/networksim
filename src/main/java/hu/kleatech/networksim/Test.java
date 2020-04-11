package hu.kleatech.networksim;

import java.util.Scanner;
import static hu.kleatech.networksim.Router.ROUTER;
import java.util.concurrent.ThreadLocalRandom;

public class Test {
    public static void main(String args[]) {
        var node1 = new Node(5, n -> {
            var msg = n.messageQueue.poll();
            if (msg == null && n.master != null){
                if (ThreadLocalRandom.current().nextInt(0, 10) == 0) n.stop();
                ROUTER.Send(new Message(n, n.master, Message.SENSOR_READOUT, Math.sqrt(System.currentTimeMillis())));
            }
            if (msg == null) return;
            switch(msg.getGoal()) {
                case Message.LEADER_ELECTION:
                    leaderElection(n, msg);
                    break;
                case Message.MASTER_STATEMENT:
                    n.master = (Node)msg.getParameters();
                    break;
                case Message.MASTER_LEAVING:
                    leaderElection(n, msg);
                    break;
            }
        }).start();
        node1.master = node1; //Ideiglenes megoldás, hogy legyen egy master
        var node2 = new Node(1, n -> {
            var msg = n.messageQueue.poll();
            if (msg == null) return;
            switch(msg.getGoal()) {
                case Message.LEADER_ELECTION:
                    leaderElection(n, msg);
                    break;
                case Message.MASTER_STATEMENT:
                    n.master = (Node)msg.getParameters();
                    break;
                case Message.MASTER_LEAVING:
                    leaderElection(n, msg);
                    break;
            }
        }).start();
        var node3 = new Node(10, n -> {
            var msg = n.messageQueue.poll();
            if (msg == null) return;
            switch(msg.getGoal()) {
                case Message.LEADER_ELECTION:
                    leaderElection(n, msg);
                    break;
                case Message.MASTER_STATEMENT:
                    n.master = (Node)msg.getParameters();
                    break;
                case Message.MASTER_LEAVING:
                    ROUTER.Broadcast(new Message(n, Node.BROADCAST_TARGET, Message.LEADER_ELECTION, n.priority));
                    break;
                case Message.SENSOR_READOUT:
                    System.out.println("Showing on HID: " + msg.getParameters());
                    break;
            }
        }).start();
        new Scanner(System.in).nextLine();
        node1.stop();
        node2.stop();
        node3.stop();
    }

    private static void leaderElection(Node n, Message msg) {
        //Ha egy új node bekapcsolódott
        if (n.master == n) {        //Ha én vagyok a master
            if (n.priority <= (Integer)msg.getParameters()) { //Ha a prioritásom nem nagyobb, mint az új node-é
                ROUTER.Broadcast(new Message(n, Node.BROADCAST_TARGET, Message.MASTER_STATEMENT, msg.getSender())); //A master legyen az új node
                n.master = msg.getSender();
            }
            else {
                ROUTER.Send(new Message(n, msg.getSender(), Message.MASTER_STATEMENT, n)); //Megmondom az új node-nak, hogy én vagyok a master
            }
        }
    }
}
