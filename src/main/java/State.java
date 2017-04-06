import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by user on 03/03/17.
 */
public class State {
    private List<Transition> transitions;
    private List<Action> actions;
    private List<State> children;
    private Optional<State> parent;
    private String id;
    private boolean isInitial;
    private Element jdom;
    private static final String PREFIX = "State";

    public State(Element element) {
        jdom = element;
        transitions = new ArrayList();
        actions = new ArrayList();
        id = element.getAttribute("id").getValue().toUpperCase();
        parent = Optional.empty();
        children = new ArrayList();
        boolean gotInitial = false;

        for(Element e: (List<Element>)element.getChildren()) {
            Optional<State> child = Optional.empty();
            if (e.getName().equals("state")) {
                child = Optional.of(new State(e));
                if(e.getAttributes().contains("initial")){
                    child.get().isInitial = true;
                    gotInitial = true;
                }

            } else if (e.getName().equals("initial")) {
                child = Optional.of(new State((Element) e.getChildren().get(0)));
                child.get().isInitial = true;
                gotInitial = true;
            }
            if(child.isPresent()){
                child.get().parent = Optional.of(this);
                this.children.add(child.get());
                FSM.states.add(child.get());
            }
        }
        if(!gotInitial && !this.children.isEmpty()){
            this.children.get(0).isInitial = true;
        }
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

    public boolean contains(State child){
        for(State c : children){
            if(c.id == child.id)
                return true;
        }
        return false;
    }


    public String getId(){ return this.id;}
    public List<Action> getActions(){ return this.actions;}
    public List<Transition> getTransitions() { return this.transitions;}
    public String toString(){
        return PREFIX+"."+id;
    }
    public State getParent(){ return this.parent.get();}
    public boolean gotParent(){ return this.parent.isPresent();}
    public List<State> getChildren(){ return this.children;}
    public boolean isInitial(){ return this.isInitial;}
    public void noMoreParent(){ parent = Optional.empty();}
}
