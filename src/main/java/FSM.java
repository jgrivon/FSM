import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 22/03/17.
 */
public class FSM {
    private AstParser ast;
    public static List<State> states;
    public static List<Event> events;
    public final String className = "GeneratedFSM";

    public FSM(AstParser ast){
        this.ast = ast;
        initStates();
    }
    private void initStates(){
        this.states = new ArrayList<State>();
        for(Element child : (List<Element>)ast.getRoot().getChildren()) {
            if(child.getName().equals("state")){
                states.add(new State(child));
            }

        }
        for(State s : states)
            s.fillData();
        getAllEvents();
    }
    private boolean contains(List<Event> l, Event e){

        for(Event c : l){
            if(c.getId().equals(e.getId())) {
                if(c.getType() == Event.Type.TRIGGER && e.getType() == Event.Type.RAISE)
                    return true;
                else if(c.getType() == e.getType())
                    return true;
            }
        }
        return false;
    }
    private void getAllEvents() {
        events = new ArrayList<Event>();
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

    private String initState(){
        return "currentState = State."+ast.getRoot().getAttribute("initial").getValue()+";";
    }

    private String outputSwitch(){
        String output="while(!eventQueue.isEmpty()){";
        output+="Event event = eventQueue.poll();";
        output+="switch(currentState){";
        for(State state : states){
            output+="case "+state.getId()+":\n";
            boolean firstIf = true;
            for(Transition t : state.getTransitions()){
                String event = t.getTrigger().toString();
                if(firstIf){
                    output+="if";
                    firstIf = false;
                }
                else output+="else if";
                output+="(event == "+event+"){\n";

                for(Event e : t.getEvents()){
                    if(e.getType() == Event.Type.SEND)
                        output+=e.getId()+"();";
                    else
                        output+="submitEvent("+e.toString()+");";
                }

                for(Action a : state.getActions()){
                    if(a.getType() == Action.Type.ONEXIT)
                       for(Event e : a.getEvents()){
                           output+=e.getId()+"();";
                       }
                }
                for(Action a : t.getTarget().getActions()){
                    if( a.getType() == Action.Type.ONENTRY)
                        for(Event e : a.getEvents())
                            output+=e.getId()+"();";
                }

                output+="currentState = "+t.getTarget().toString()+";";
                output+="}\n";
            }

            output+="break;";
        }
        output+="}}\n";
        return output;

    }

    private String outputEvents() {
        String output = "enum Event{";
        for(Event event : events){
            if(event.getType() != Event.Type.SEND)
                output+=event.getId()+",";
        }
        output = output.substring(0,output.length()-1);
        output+="};\n";
        return output;

    }

    private String outputStates(){
        String output = "enum State{";
        for(State state : states){
            output +=state.getId()+",";
        }
        output = output.substring(0,output.length()-1);
        output+="};\n";
        return output;
    }

    private String correctIndent(String output){
        int cpt = 0;
        String finalclass = "";
        String word ="";
        for(char c : output.toCharArray()){
            word += c;
            if(word.equals("break"))cpt--;
            switch(c){
                case ':':
                case '{':cpt++;finalclass+=c+"\n"+indent(cpt);word ="";break;
                case '}':cpt--;finalclass+="\n"+indent(cpt)+c;word = "";break;
                case '\n':finalclass+=c+indent(cpt);word = "";break;
                case ',':
                case ';':finalclass+=c+"\n"+indent(cpt);word = "";break;
                case ' ': word = "";
                default:finalclass+=c;break;
            }
        }
        return finalclass;
    }
    private String indent(int in){
        String tmp = "";
        for(int i = 0; i < in; i++){
            tmp+="\t";
        }
        return tmp;
    }

    public String writingClass(){
        String output="";
        String imports = "import java.util.LinkedList;";

        String header = "public class "+className+"{";
        String initStateVar = "State currentState;";
        String constructor ="public "+className+"(){"+initState()+"}\n";
        String activate = "public void activate(){";
        String eventList = "LinkedList<Event> eventQueue = new LinkedList<Event>();";
        String methods ="";
        for(Event event : events){
            if(event.getType() == Event.Type.SEND)
                methods+="public void "+event.getId()+"(){System.out.println(\"executing "+event.getId()+"\");}\n";
        }
        activate+=  outputSwitch() +"}\n";
        String submit ="public void submitEvent(Event e){eventQueue.add(e);}\n";
        output += imports+header+outputEvents()+outputStates()+eventList+initStateVar+constructor+submit+activate+methods+"}";
        return correctIndent(output);
    }

}
