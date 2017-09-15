/**
 * Created by student on 9/12/17.
 */

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Main {

    public static void main(String[] args) {

        try {
            File inputFile = new File("input.txt");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("questions");

            String question;
            String a1;
            String a2;
            String a3;
            String a4;
            int correct;


            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode1 = nList.item(temp);
                NodeList nl = nNode1.getChildNodes();
                System.out.println("\nCurrent Element :" + nNode1.getNodeName());
                for (int temp1 = 0; temp1 < nl.getLength(); temp1++) {
                    Node nNode = nl.item(temp1);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    question = eElement
                            .getElementsByTagName("q")
                            .item(0)
                            .getTextContent();
                    a1 = eElement
                            .getElementsByTagName("a1")
                            .item(0)
                            .getTextContent();
                    a2 = eElement
                            .getElementsByTagName("a2")
                            .item(0)
                            .getTextContent();
                    a3 = eElement
                            .getElementsByTagName("a3")
                            .item(0)
                            .getTextContent();
                    a3 = eElement
                            .getElementsByTagName("a4")
                            .item(0)
                            .getTextContent();
                    correct = Integer.parseInt(eElement
                            .getElementsByTagName("correct")
                            .item(0)
                            .getTextContent());

                    System.out.println(question);


//                    System.out.println("Marks : "
//                            + eElement
//                            .getElementsByTagName("marks")
//                            .item(0)
//                            .getTextContent());
                }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
