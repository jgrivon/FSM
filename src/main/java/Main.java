import org.jdom.Element;

import java.util.ArrayList;

/**
 * Created by user on 03/03/17.
 */
public class Main {

    public static void main(String[] args){
        AstParser ast = new AstParser("text.xml");
        FSM fsm = new FSM(ast);
        ArrayList<Element> states = new ArrayList<Element>(fsm.getStates());
        System.out.println(states.size());
        for(Element state: states){
            System.out.println(fsm.getTransitions(state));
        }
    }
}
