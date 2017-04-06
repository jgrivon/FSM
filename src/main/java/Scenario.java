import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;


/**
 * Created by user on 05/04/17.
 */
public class Scenario {
    private Queue<GeneratedFSM.Event> events;
    public Scenario(String ... events){
        this.events = new LinkedList<>();
        for(String e : events){
            this.events.add(GeneratedFSM.Event.valueOf(e.toUpperCase()));
        }
    }
    public GeneratedFSM.Event next(){
        if(!this.events.isEmpty())return this.events.remove();
        else return null;
    }

    public Queue<GeneratedFSM.Event> getEvents(){
        return events;
    }
}
