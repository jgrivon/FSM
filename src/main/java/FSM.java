import org.jdom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by user on 22/03/17.
 */
public class FSM {
    private AstParser ast;
    public static List<State> states;
    public static List<Event> events;
    public final String className = "GeneratedFSM";
    public final String METHOD_SUFFIX = "Event";

    public FSM(AstParser ast){
        this.ast = ast;
        initStates();
    }
    private void initStates(){
        this.states = new ArrayList();
        for(Element child : (List<Element>)ast.getRoot().getChildren()) {
            if(child.getName().equals("state")){
                states.add(new State(child));
            }

        }
        for(State s : states) {
            s.fillData();
        }
        getAllEvents();
        flattening();
    }

    public void flattening(){
        boolean isFinish = false;
        while(!isFinish){
            isFinish = true;
            for(int i = 0; i < states.size();i++){
                if(!states.get(i).gotParent() && !states.get(i).getChildren().isEmpty()) {
                    modifyTransition(states.get(i),states.get(i).getChildren());
                    for (State child : states.get(i).getChildren()) {
                        if(child.isInitial()) {
                            child.getTransitions().addAll(states.get(i).getTransitions());
                            child.getActions().addAll(states.get(i).getActions());
                        }
                        child.noMoreParent();
                    }
                    states.remove(i);
                    isFinish = false;
                }
            }

        }
    }
    private boolean contains(List<Event> l, Event e){

        for(Event c : l){
            if(c.getId().equals(e.getId())) {
                if((c.getType() == Event.Type.TRIGGER && e.getType() == Event.Type.RAISE) || (e.getType() == Event.Type.TRIGGER && c.getType() == Event.Type.RAISE))
                    return true;
                else if(c.getType() == e.getType())
                    return true;
            }
        }
        return false;
    }
    private void getAllEvents() {
        events = new ArrayList();
        for (State state : states) {
            for (Action action : state.getActions())
                for(Event event : action.getEvents())
                    if(!contains(events,event))
                        events.add(event);

            for (Transition transition : state.getTransitions()) {
                for (Event event : transition.getEvents())
                    if (!contains(events,event))
                        events.add(event);
                if (!contains(events,transition.getTrigger()))
                    events.add(transition.getTrigger());
            }
        }
    }



    private void modifyTransition(State parent, List<State> targets){
        for(State state: states){
            List<Integer> deleteSafe = new ArrayList();
            for(int i = 0; i < state.getTransitions().size();i++){
                if(parent == state.getTransitions().get(i).getTarget()) {
                    for(State s : targets){
                        state.getTransitions().add(new Transition(s,state.getTransitions().get(i)));
                    }
                    deleteSafe.add(i);
                }
            }
            for(int i : deleteSafe){
                state.getTransitions().remove(i);
            }
        }
    }

    private String initState(){
        return "currentState = State."+ast.getRoot().getAttribute("initial").getValue().toUpperCase()+";";
    }

    private String outputSwitch(){
        StringBuilder output = new StringBuilder();
        output.append("while(!eventQueue.isEmpty()){")
                .append("Event event = eventQueue.poll();")
                .append("System.out.println(\"Event -> \"+event.name());")
                .append("switch(currentState){");

        for(State state : states){
            output.append("case "+state.getId()+":\n");
            boolean firstIf = true;
            for(Transition t : state.getTransitions()){
                String event = t.getTrigger().toString();
                if(firstIf){
                    output.append("if");
                    firstIf = false;
                }
                else output.append("else if");
                output.append("(event == "+event+"){\n");

                for(Event e : t.getEvents()){
                    if(e.getType() == Event.Type.SEND)
                        output.append(e.getId().toLowerCase() + METHOD_SUFFIX + "();");

                    else
                        output.append("submitEvent(" + e.toString() + ");");
                }

                for(Action a : state.getActions()){
                    if(a.getType() == Action.Type.ONEXIT)
                       for(Event e : a.getEvents()){
                           output.append(e.getId().toLowerCase()+METHOD_SUFFIX+"();");
                       }
                }
                for(Action a : t.getTarget().getActions()){
                    if( a.getType() == Action.Type.ONENTRY)
                        for(Event e : a.getEvents())
                            output.append(e.getId().toLowerCase()+METHOD_SUFFIX+"();");
                }

                output.append("currentState = "+t.getTarget().toString()+";")
                        .append("}\n");
            }

            output.append("break;");
        }
        output.append("}}\n");
        return output.toString();

    }

    private String outputEvents() {
        StringBuilder output = new StringBuilder("enum Event{");
        for(Event event : events){
            if(event.getType() != Event.Type.SEND)
                output.append(event.getId()+",");
        }
        output = new StringBuilder(output.substring(0,output.length()-1));
        output.append("};\n");
        return output.toString();

    }

    private String outputStates(){
        StringBuilder output = new StringBuilder("enum State{");
        for(State state : states){
            output.append(state.getId()+",");
        }
        output = new StringBuilder(output.substring(0,output.length()-1));
        output.append("};\n");
        return output.toString();
    }

    private String correctIndent(StringBuilder output){
        int cpt = 0;
        StringBuilder finalclass = new StringBuilder();
        String word ="";
        for(char c : output.toString().toCharArray()){
            word += c;
            if(word.equals("break"))cpt--;
            switch(c){
                case ':':
                case '{':cpt++;finalclass.append(c+"\n"+indent(cpt));word ="";break;
                case '}':cpt--;finalclass.append("\n"+indent(cpt)+c);word = "";break;
                case '\n':finalclass.append(c+indent(cpt));word = "";break;
                case ',':
                case ';':finalclass.append(c+"\n"+indent(cpt));word = "";break;
                case ' ': word = "";
                default:finalclass.append(c);break;
            }
        }
        return finalclass.toString();
    }
    private String indent(int in){
        StringBuilder tmp = new StringBuilder();
        for(int i = 0; i < in; i++){
            tmp.append("\t");
        }
        return tmp.toString();
    }

    public String writingClass(){
        StringBuilder output= new StringBuilder();
        String imports = "import java.util.LinkedList;";

        String header = "public class "+className+"{";
        String initStateVar = "State currentState;";
        String constructor ="public "+className+"(){"+initState()+"}\n";
        String activate = "public void activate(){";
        String eventList = "LinkedList<Event> eventQueue = new LinkedList<Event>();";
        StringBuilder methods = new StringBuilder();
        for(Event event : events){
            if(event.getType() == Event.Type.SEND)
                methods.append("public void "+event.getId().toLowerCase()+METHOD_SUFFIX+"(){System.out.println(\"executing "+event.getId()+"\");}\n");
        }
        activate+=  outputSwitch() +"}\n";
        String submit ="public void submitEvent(Event e){eventQueue.add(e);}\n";
        output.append(imports)
                .append(header)
                .append(outputEvents())
                .append(outputStates())
                .append(eventList)
                .append(initStateVar)
                .append(constructor)
                .append(submit)
                .append(activate)
                .append(methods+"}");
        return correctIndent(output);
    }

}
