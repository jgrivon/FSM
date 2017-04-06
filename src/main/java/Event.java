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
        this.id = e.getAttribute("event").getValue().toUpperCase();
    }
    public Event(String s,Type type){
        this.type = type;
        this.id = s.toUpperCase();
    }

    public boolean equals(Event e){
        if(e.id == this.id) return true;
        else return false;
    }


    public String getId() { return this.id;}
    public String toString(){
        return PREFIX+"."+id;
    }
    public Type getType() { return this.type;}
}
