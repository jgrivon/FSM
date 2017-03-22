import org.jdom.Element;

/**
 * Created by user on 14/03/17.
 */
public class Event {
    public enum Type{
        SEND,
        RAISE,
        TRIGGER
    }

    private String id;
    private Type type;
    private static final String PREFIX = "Event";
    public Event(Element e,Type type){
        this.type = type;
        this.id = e.getAttribute("event").getValue();
    }
    public Event(String s,Type type){
        this.type = type;
        this.id = s;
    }


    public String getId() { return this.id;}
    public String toString(){
        return PREFIX+"."+id;
    }
    public Type getType() { return this.type;}
}
