package hu.kleatech.networksim;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static hu.kleatech.networksim.Router.ROUTER;
import static hu.kleatech.networksim.Message.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Node {
    public static final Consumer<Node> EMPTY_JOB = q -> {};
    public static final Node BROADCAST_TARGET = new Node(0, EMPTY_JOB);
    
    static int _id = 0;
    int id = ++_id;
    
    private final Consumer<Node> job;
    private final ExecutorService executor = Executors.newCachedThreadPool();    
    private final AtomicBoolean running = new AtomicBoolean(true);
    
    final Queue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    final Integer priority;
    Node master;
    
    public Node(int priority, Consumer<Node> job) {
        this.job = job;
        this.priority = priority;
    }
    
    public Node start() {
        executor.execute(() -> {
            System.out.println("Started " + this);
            ROUTER.Connect(this);
            ROUTER.Broadcast(new Message(this, BROADCAST_TARGET, LEADER_ELECTION, priority));
            try {
                loop();
            } catch (InterruptedException ex) {
                stop();
            }
        });
        return this;
    }
    
    public void stop() {
        running.set(false);
        if (this == master) ROUTER.Broadcast(new Message(this, Node.BROADCAST_TARGET, Message.MASTER_LEAVING, null));
        ROUTER.Disconnect(this);        
    }
    
    public void recieveMessage(Message message) {
        System.out.println(this + " received message " + message);
        messageQueue.add(message);
    }
    
    private void loop() throws InterruptedException {
        while(running.get()) {
            Thread.sleep(1000);
            job.accept(this);
        }
        System.out.println("Stopped " + this);
    }
    
    @Override
    public String toString() {
        return "Node" + id;
    }
}
