import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 03/03/17.
 */
public class State {
    private List<Transition> transitions;
    private List<Action> actions;
    private String id;
    private Element jdom;
    private static final String PREFIX = "State";

    public State(Element element) {
        jdom = element;
        transitions = new ArrayList<Transition>();
        actions = new ArrayList<Action>();
        id = element.getAttribute("id").getValue();
    }

    public void fillData(){
        for (Element child : (List<Element>) jdom.getChildren()) {
            if (child.getName().equals("transition")){
                Transition tmp = new Transition(child);
                this.transitions.add(tmp);
            }
            else if (child.getName().equals("onentry") || child.getName().equals("onexit") ){
                String delay ;
                try {
                    delay = child.getAttribute("delay").getValue();
                }catch(NullPointerException e){
                    delay = "Os";
                }
                Action tmp = new Action(child,Action.Type.valueOf(child.getName().toUpperCase()),delay);
                this.actions.add(tmp);
            }
        }
    }


    public String getId(){ return this.id;}
    public List<Action> getActions(){ return this.actions;}
    public List<Transition> getTransitions() { return this.transitions;}
    public String toString(){
        return PREFIX+"."+id;
    }
}
