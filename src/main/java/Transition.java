import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 14/03/17.
 */
public class Transition {
    public enum Type{
        EXTERNAL,
        INTERNAL
    }

    private Type type;
    private List<Event> events;
    private State target;
    private Event trigger;

    public Transition(Element e){

        this.type = Type.valueOf(e.getAttribute("type").getValue().toUpperCase());
        this.trigger = new Event(e.getAttribute("event").getValue(), Event.Type.TRIGGER);

        for(State s : FSM.states){
            if(s.getId().equals(e.getAttribute("target").getValue().toUpperCase())){
                this.target = s;
                break;
            }
        }
        this.events = new ArrayList<Event>();
        for(Object o: e.getChildren()){
            Element event = (Element) o;
            Event.Type type = Event.Type.valueOf(event.getName().toUpperCase());
            if(type == Event.Type.SEND || type == Event.Type.RAISE ){
                this.events.add(new Event(event,type));
            }
        }
    }

    public Transition(State s, Transition old){
        this.type = old.type;
        this.trigger = old.trigger;
        this.target = s;
        this.events = old.events;
    }

    public List<Event> getEvents(){ return this.events;}
    public State getTarget(){ return this.target;}
    public Event getTrigger(){ return this.trigger;}
    public void setTarget(State target){ this.target = target;}
    public Type getType(){ return this.type;}
}
