package org.xk.crawler.xpath;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

public class XPathUtils {
    public static String getContect(String url,String expression) throws Exception{
        DocumentBuilderFactory domFactory=
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=domFactory.newDocumentBuilder();
        Document document = builder.parse(url);
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr= xpath.compile(expression);
        NodeList nodes = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
        StringBuffer buffer = new StringBuffer();
        buildNodes(nodes,buffer,"");
        return buffer.toString();
    }

    public static boolean isMatch(String url , String expression) throws Exception{
        DocumentBuilderFactory domFactory=
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=domFactory.newDocumentBuilder();
        Document document = builder.parse(url);
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr= xpath.compile(expression);
        NodeList nodes = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
        if(nodes.getLength()==0){
            return false;
        }
        return true;
    }

    private static String buildAttribute(NamedNodeMap nameNodeMap){
        if(nameNodeMap.getLength() != 0){
            StringBuffer buffer = new StringBuffer();
            buffer.append(" ");
            for(int index=0;index<nameNodeMap.getLength();index++){
                buffer.append(nameNodeMap.item(index).getNodeName());
                buffer.append("=");
                buffer.append("\""+nameNodeMap.item(index).getTextContent()+"\" ");
            }
            String result = buffer.toString();
            result = result.substring(0,result.length()-1);
            return result;
        }else{
            return "";
        }
    }

    private static StringBuffer buildNodes(NodeList nodes , StringBuffer buffer ,String prefix){
        for(int i = 0; i<nodes.getLength();i++) {
//            buffer.append(nodes.item(i).getTextContent());
            String nodeName = nodes.item(i).getNodeName().trim();
            //leaf node
            if("#text".equals(nodeName)){
                buffer.append(nodes.item(i).getTextContent());
                continue;
            }
            NamedNodeMap nameNodeMap = nodes.item(i).getAttributes();
            String attributeString = buildAttribute(nameNodeMap);
            //not leaf node
            //append prefix
            buffer.append(getNodePrefix(nodeName,attributeString));
            //append subNodes
            NodeList subList = nodes.item(i).getChildNodes();

            if(subList.getLength() != 0){
                buildNodes(subList,buffer,prefix+"");
            }else{
                buffer.append(getNodeContent(nodes.item(i).getTextContent(),prefix));
            }
            //append suffix
            buffer.append(getNodeSuffix(nodeName));
        }
        return buffer;
    }

    private static String getNodePrefix(String nodeName , String attributes){
        return "<" + nodeName + attributes +">";
    }

    private static String  getNodeContent(String content , String prefix){
        return prefix + content;
    }

    private static String getNodeSuffix(String nodeName){
        return "</" + nodeName + ">";
    }

    public static void main(String[] args) throws Exception{
        String result = getContect("https://crawltest.cis.upenn.edu/nytimes/Africa.xml","/rss/channel/item/title[text()=\"World Briefing: Africa, Americas, Europe and Asia\"]");
        if("".equals(result)){
            System.out.println("can not find");
        }else{
            System.out.println(result);
        }
    }
}
