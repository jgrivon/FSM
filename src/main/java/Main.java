import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by user on 03/03/17.
 */
public class Main {

    public static void main(String[] args){
        String path = "src/main/java/";
        AstParser ast = new AstParser("text.xml");
        long startTime = System.currentTimeMillis();
        FSM fsm = new FSM(ast);
        try{
            PrintWriter writer = new PrintWriter(path+fsm.className+".java", "UTF-8");
            writer.print(fsm.writingClass());
            writer.close();
        } catch (IOException e) {
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);

    }
}
