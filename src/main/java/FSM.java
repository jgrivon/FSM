import org.jdom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Grivon Justin
 * @email grivon.justin@gmail.com
 */
public class FSM {
    private AstParser ast;
    private List<Element> states;

    public FSM(AstParser ast){
        this.ast = ast;
        this.states = initStates();
    }

    public List<Element> getTransitions(Element e){
        return e.getChildren("transition");
    }

    private List<Element> initStates(){
       //TODO change to getChildren("state")
        return ast.getRoot().getChildren();
    }

    public List<Element> getStates(){
        return this.states;
    }
}
