package org.xk.crawler.xpath;

import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

public class XPathTest {
    public static void test1() throws Exception{
        DocumentBuilderFactory domFactory=
                DocumentBuilderFactory.newInstance( );
        DocumentBuilder builder=domFactory.newDocumentBuilder( );
        Document doc=builder.parse(new File("E:\\dev\\svnspace\\crawler\\pom.xml"));
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath( );
        XPathExpression expr= xpath.compile("//groupId");
//        System.out.println(expr.evaluate(doc));
        NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
        for(int i = 0; i<nodes.getLength();i++) {
            System.out.println(nodes.item(i).getNodeName());
//            NodeList subNodes = nodes.item(i).getChildNodes();
//            for(int j = 0 ; j < subNodes.getLength() ; j++){
////                System.out.println(subNodes.item(j).getNodeName());
////                System.out.println(subNodes.item(j).getTextContent());
//                System.out.println(subNodes.item(j).getTextContent());
//            }
        }
//        List<String> list = (List<String>)expr.evaluate(doc,XPathConstants.STRING);
//        for(String str : list){
//            System.out.println(str);
//        }
    }

    public static void test2() throws Exception {
        SAXReader reader = new SAXReader();
        InputStream in = new FileInputStream(new File("E:\\dev\\svnspace\\crawler\\pom.xml"));
        org.dom4j.Document doc = reader.read(in);
        String xpath = "//dependencies/dependency/text()";
        List<Node> list = doc.selectNodes(xpath);
        for(Node node : list){
            System.out.println(node.getStringValue());
        }
    }


    public static void test3() throws Exception{
        String doc = "";
    }
    public static void main(String[] args) throws Exception {
//        test1();
        System.out.println(XPathUtils.isMatch("https://crawltest.cis.upenn.edu/nytimes/Africa.xml","//item"));
    }
}
