import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 22/03/17.
 */
public class Action {
    public enum Type{
        ONENTRY,
        ONEXIT
    }

    private Type type;
    private List<Event> events;
    private String delay;

    public Action(Element e, Type type, String delay){
        this.type = type;
        this.events = new ArrayList<Event>();
        this.delay = delay;
        for(Object o: e.getChildren()){
            Element event = (Element) o;
            Event.Type tmpType= Event.Type.valueOf(event.getName().toUpperCase());
            if(tmpType == Event.Type.SEND || tmpType == Event.Type.RAISE ){
                this.events.add(new Event(event,tmpType));
            }
        }
    }

    public List<Event> getEvents(){ return this.events;}
    public Type getType(){ return this.type;}
}
