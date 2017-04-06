import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by user on 03/03/17.
 */
public class Main {

    public static void main(String[] args){
        String path = "src/main/java/";
        AstParser ast = new AstParser("text2.xml");
        FSM fsm = new FSM(ast);
        try{
            PrintWriter writer = new PrintWriter(path+fsm.className+".java", "UTF-8");
            writer.print(fsm.writingClass());
            writer.close();
        } catch (IOException e) {
        }
        Scenario scenario = new Scenario("b3","b1","b2","b1","b2","b2","b2","b1","b1");
        GeneratedFSM gfsm = new GeneratedFSM();
        GeneratedFSM.Event next = scenario.next();
        System.out.println("StartingState : "+gfsm.currentState);
        while(next != null){
            gfsm.submitEvent(next);
            gfsm.activate();
            next = scenario.next();
            System.out.println("CurrentState : "+gfsm.currentState);
        }

    }
}
