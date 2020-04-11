package hu.kleatech.networksim;

import java.util.HashSet;
import java.util.Set;

public class Router {
    public static final Router ROUTER = new Router();
    
    private Router() {}
    
    private final Set<Node> nodes = new HashSet();
    
    public void Connect(Node node) {
        nodes.add(node);
    }
    
    public void Disconnect(Node node) {
        nodes.remove(node);
    }
    
    public void Send(Message message) {
        message.getTarget().recieveMessage(message);
    }
    
    public void Broadcast(Message message) {
        nodes.stream().filter(n -> n != message.getSender()).forEach(n -> n.recieveMessage(message));
    }
}
