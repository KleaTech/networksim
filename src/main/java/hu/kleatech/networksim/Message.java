package hu.kleatech.networksim;

import java.util.UUID;

public class Message {
    private final Node sender;
    private final Node target;
    private final String goal;
    private final Object parameters;
    private final UUID id;
    
    public Message(Node sender, Node target, String goal, Object parameters) {
        this.goal = goal;
        this.parameters = parameters;
        this.sender = sender;
        this.target = target;
        this.id = UUID.randomUUID();
    }
    
    public Node getSender() {
        return sender;
    }
    
    public Node getTarget() {
        return target;
    }
    
    public String getGoal() {
        return goal;
    }
    
    public Object getParameters() {
        return parameters;
    }
    
    @Override
    public String toString() {
        var trg = target == Node.BROADCAST_TARGET ? "BROADCAST" : target;
        return "from " + sender + " to " + trg + ": " + goal + parameters;
    }
    
    public static final String LEADER_ELECTION = "Who's the master? I have prio ";
    public static final String MASTER_STATEMENT = "The master is ";
    public static final String MASTER_LEAVING = "I'm the master and I'm leaving ";
    public static final String SENSOR_READOUT = "The sensor's value is ";
}
