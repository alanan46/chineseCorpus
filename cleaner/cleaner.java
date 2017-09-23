
// u can remove the line below
package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Alexia
 * 
 * 
 */
public class cleaner{
    private Document document;
    
    public static 	 Map<String,Integer> stats= new HashMap<String,Integer>();  

    
    
    public static void main(String[] args) throws IOException {
    	
    	RemoveReplyOfXml("a.txt","testtestresult.txt");
    	return;
    }
    

    public static void parserXml(String inputFileName, String outputFileName) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(inputFileName);
            NodeList users = document.getChildNodes();
            
            for (int i = 0; i < users.getLength(); i++) {
                Node user = users.item(i);
                NodeList userInfo = user.getChildNodes();
                
                for (int j = 0; j < userInfo.getLength(); j++) {
                    Node node = userInfo.item(j);
                    NodeList userMeta = node.getChildNodes();
                    
                    for (int k = 0; k < userMeta.getLength(); k++) {
                        if(userMeta.item(k).getNodeName() != "#text")
                            System.out.println(userMeta.item(k).getNodeName()
                                    + ":" + userMeta.item(k).getTextContent());
                        
                        
                        Node value = userMeta.item(k).getAttributes().getNamedItem("uid");
                        //String val = value.getNodeValue();
                        //value.setNodeValue("2");
                        
                        Element element = (Element) userMeta.item(k);
                        element.removeAttribute("uname");

                    }
                    
                    System.out.println();
                }
            }
            
    		TransformerFactory tf = TransformerFactory.newInstance();
    		Transformer transformer;
			try {
				transformer = tf.newTransformer();
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			PrintWriter pw = new PrintWriter(new FileOutputStream(outputFileName));
			StreamResult result = new StreamResult(pw);
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static void ChangeContentOfXml(String inputFileName, String outputFileName) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(inputFileName);
            NodeList users = document.getChildNodes();
            
            for (int i = 0; i < users.getLength(); i++) {
                Node user = users.item(i);
                NodeList userInfo = user.getChildNodes();
                
                for (int j = 0; j < userInfo.getLength(); j++) {
                    Node node = userInfo.item(j);
                    NodeList userMeta = node.getChildNodes();
                    
                    for (int k = 0; k < userMeta.getLength(); k++) {
                        userMeta.item(k).setTextContent("new");
                        

                    }
                    
                    System.out.println();
                }
            }
            
    		TransformerFactory tf = TransformerFactory.newInstance();
    		Transformer transformer;
			try {
				transformer = tf.newTransformer();
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			PrintWriter pw = new PrintWriter(new FileOutputStream(outputFileName));
			StreamResult result = new StreamResult(pw);
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //adding stats stuff in the function below
    public static void RemoveReplyOfXml(String inputFileName, String outputFileName) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(inputFileName);
            NodeList users = document.getChildNodes();
            
            for (int i = 0; i < users.getLength(); i++) {
                Node user = users.item(i);
                NodeList userInfo = user.getChildNodes();
                //counter to count <s> that has content
                //not all j has conversation in it
                int counter=1;
                for (int j = 0; j < userInfo.getLength(); j++) {
                	
                    Node node = userInfo.item(j);
                    NodeList userMeta = node.getChildNodes();
                    int k = 1;
                    for (; k < userMeta.getLength(); k++) {
                        String content = userMeta.item(k).getTextContent();
                        Pattern p = Pattern.compile("回复 \\S+ :(\\S+)");  
                        Matcher m = p.matcher(content);
                        if(m.find()) {
                        	System.out.println("hh");
                        	userMeta.item(k).setTextContent(m.group(1));
                        }
                            
                    }
                    //if there is actually utterance then record the # of utterance in a conversation
                    if(k>1) {
                    	stats.put("conv"+counter, k);
                    	counter++;
                    }
                    
                    System.out.println();
                }
                System.out.println("stats: "+stats);
            }
            
    		TransformerFactory tf = TransformerFactory.newInstance();
    		Transformer transformer;
			try {
				transformer = tf.newTransformer();
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			PrintWriter pw = new PrintWriter(new FileOutputStream(outputFileName));
			StreamResult result = new StreamResult(pw);
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    
    
    public static void modifyAttribute(String inputFileName, String outputFileName, String att_name) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(inputFileName);
            NodeList users = document.getChildNodes();
            
            for (int i = 0; i < users.getLength(); i++) {
                Node user = users.item(i);
                NodeList userInfo = user.getChildNodes();
                
                for (int j = 0; j < userInfo.getLength(); j++) {
                    Node node = userInfo.item(j);
                    NodeList userMeta = node.getChildNodes();
                    
                    for (int k = 0; k < userMeta.getLength(); k++) {
                        
                        
                        Node value = userMeta.item(k).getAttributes().getNamedItem(att_name);
                        value.setNodeValue("2");

                    }
                    
                }
            }
            
    		TransformerFactory tf = TransformerFactory.newInstance();
    		Transformer transformer;
			try {
				transformer = tf.newTransformer();
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			PrintWriter pw = new PrintWriter(new FileOutputStream(outputFileName));
			StreamResult result = new StreamResult(pw);
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    public static void removeAttribute(String inputFileName, String outputFileName, String att_name) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(inputFileName);
            NodeList users = document.getChildNodes();
            
            for (int i = 0; i < users.getLength(); i++) {
                Node user = users.item(i);
                NodeList userInfo = user.getChildNodes();
                
                for (int j = 0; j < userInfo.getLength(); j++) {
                    Node node = userInfo.item(j);
                    NodeList userMeta = node.getChildNodes();
                    
                    for (int k = 0; k < userMeta.getLength(); k++) {
                        Element element = (Element) userMeta.item(k);
                        element.removeAttribute(att_name);

                    }
                    
                }
            }
            
    		TransformerFactory tf = TransformerFactory.newInstance();
    		Transformer transformer;
			try {
				transformer = tf.newTransformer();
			DOMSource source = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			PrintWriter pw = new PrintWriter(new FileOutputStream(outputFileName));
			StreamResult result = new StreamResult(pw);
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void mergeXML(String input_f1, String input_f2, String output_f){


        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Document doc = null;
        Document doc2 = null;

        try {
                db = dbf.newDocumentBuilder();
                doc = db.parse(new File(input_f1));
                doc2 = db.parse(new File(input_f2));
                NodeList ndListFirstFile = doc.getElementsByTagName("staff");

                Node nodeArea = doc.importNode(doc2.getElementsByTagName("area").item(0), true);
                Node nodeCity = doc.importNode(doc2.getElementsByTagName("city").item(0), true);
                ndListFirstFile.item(0).appendChild(nodeArea);
                ndListFirstFile.item(0).appendChild(nodeCity);

              TransformerFactory tFactory = TransformerFactory.newInstance();
              Transformer transformer = tFactory.newTransformer();
              transformer.setOutputProperty(OutputKeys.INDENT, "yes");  

              DOMSource source = new DOMSource(doc);
              StreamResult result = new StreamResult(new StringWriter());
              transformer.transform(source, result); 

              Writer output = new BufferedWriter(new FileWriter(output_f));
              String xmlOutput = result.getWriter().toString();  
              output.write(xmlOutput);
              output.close();

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
    
    
}
