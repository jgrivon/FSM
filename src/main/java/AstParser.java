/**
 * @author Grivon Justin
 * @email grivon.justin@gmail.com
 */
import java.io.*;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.filter.*;
import java.util.List;
import java.util.Iterator;

public class AstParser {

    private Document document;
    private Element root;

    public AstParser(String file){
        SAXBuilder sxb = new SAXBuilder();
        try
        {
            document = sxb.build(new File(file));
        }
        catch(Exception e){
            e.printStackTrace();
        }

        root = document.getRootElement();


    }

    public Element getRoot() {
        return root;
    }

    public String toString(){

        String tostring = indentToString("",root);
        return tostring;
    }

    private String indentToString(String indent,Element element){
        String tostring=indent
                        +element.getName()
                        + getAttributes(element)
                        +":";
        if(element.getChildren().size() == 0){
            tostring+= element.getValue();
        }
        tostring+="\n";
        Iterator i = element.getChildren().iterator();
        while(i.hasNext())
        {
            Element courant = (Element)i.next();
            tostring += indentToString(indent+"  ",courant);
        }
        return tostring;

    }

    private String getAttributes(Element element){
        String tostring="";
        if(element.getAttributes().size()>0){
            tostring+=" (";
            for (Attribute a:(List<Attribute>)element.getAttributes()) {
                tostring+=a.getName()+":"+a.getValue()+",";
            }
            tostring = tostring.substring(0, tostring.length()-1);
            tostring+=") ";
        }
        return tostring;
    }
}
