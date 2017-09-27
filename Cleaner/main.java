// u can remove the line below
package cleaner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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

import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Miles,Alan,Akash based on 
 * @ original author Alexia: http://www.cnblogs.com/lanxuezaipiao/archive/2013/05/17/3082949.html
 *        
 */
public class main {
	private Document document;
	// put the list of regex of what we wish to filter in side the regexs.dat
	// file
	// then we filter it
	public static ArrayList<String> regexs;
	public static Map<String, Integer> stats = new HashMap<String, Integer>();
	public static ArrayList<ArrayList<Integer>> Stats2d = new ArrayList<ArrayList<Integer>>();

	public static ArrayList<String> loadRegexs() {
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader("./regexs.dat"));

			String str;

			ArrayList<String> list = new ArrayList<String>();
			while ((str = in.readLine()) != null) {
				list.add(str);
			}

			return list;
			// return list.toArray(new String[1]);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return null;
		}
	}

	public static void main(String[] args) throws IOException {

		regexs = loadRegexs();
		purgeMergeDataFile();
		removeSpecialCharacters();
		purifyXml("merged.txt", "final_corpus.txt");
		removeAttribute();
    	StatsXml("final_corpus.txt","final_stats.txt");
    	UIDStatsXml("final_corpus.txt","pp_stats.txt");
		return;
	}

	// get rid of all txt files with only <dialogue></dialogue> tag and no data
	// and merge them to a single file
	// then clean the <dialogue>tag so that the result file is a pure xml

	public static void merge() {

		try {
			Process p = Runtime.getRuntime().exec(
					"./merge.sh");
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void removeSpecialCharacters() {

		try {
			Process p = Runtime.getRuntime().exec(
					"./remove_special_characters.sh");
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void removeAttribute() {

		try {
			Process p = Runtime.getRuntime().exec("./removeatrribute.sh");
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void purgeMergeDataFile() {

		try {
			Process p = Runtime.getRuntime().exec("./merge.sh");
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public static void purifyXml(String inputFileName, String outputFileName) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(inputFileName);
			NodeList users = document.getChildNodes();

			FileWriter deletelog = new FileWriter("delete.log");
			Map remove_map = new HashMap();
			for (int i = 0; i < users.getLength(); i++) {
				Node user = users.item(i);
				NodeList userInfo = user.getChildNodes();
				// counter to count <s> that has content
				// not all j has conversation in it
				int counter = 1;
				for (int j = 0; j < userInfo.getLength(); j++) {

					Node node = userInfo.item(j);
					NodeList userMeta = node.getChildNodes();
					int k = 0;
					for (; k < userMeta.getLength(); k++) {
						String content = userMeta.item(k).getTextContent();
						Pattern p11 = Pattern.compile("回复\\s*\\S+\\s*:(.*)");
						Pattern p12 = Pattern.compile("回复\\s*\\S+\\s*：(.*)");
						
						//System.out.println("\""+content+"\"");

						Matcher m11 = p11.matcher(content);
						Matcher m12 = p12.matcher(content);

						if (m11.find()) {
							userMeta.item(k).setTextContent(m11.group(1));
						}
						if (m12.find()) {
							userMeta.item(k).setTextContent(m12.group(1));
						}
						

						Pattern p13 = Pattern.compile("\\s+");
						Matcher m13 = p13.matcher(userMeta.item(k).getTextContent());
						if(userMeta.item(k).getTextContent().length()==0||m13.find()){
							userMeta.item(k).getParentNode()
							.removeChild(userMeta.item(k));
							k--;
							continue;
						}
							
						
						// filter swear words
						Boolean removed = false;
						int number = 1;

						for (String reg : regexs) {
							String content1 = userMeta.item(k).getTextContent();
							// Pattern ptn = Pattern.compile(reg);
							// Matcher m1 = ptn.matcher(content1);
							// if(m1.find()) {
							if (content1.contains(reg)) {
								if (!remove_map.containsKey(reg)) {
									remove_map.put(reg, 1);
								}
								else
								{
									remove_map.put(reg, (int)remove_map.get(reg)+1);
								}
								// delete it
								//System.out.println("number: " + number
								//		+ " reg: " + reg);
								//System.out.println("Delete '" + content1
								//		+ "' because of '" + reg + "'\n");
								deletelog.write("Delete '" + content1
										+ "' because of '" + reg + "'"
										+ "number: " + number + "\n");
								userMeta.item(k).getParentNode()
										.removeChild(userMeta.item(k));
								k--;
								removed = true;
								break;
							}
							number++;
						}
						

					}
					// if there is actually utterance then record the # of
					// utterance in a conversation
					if (k > 1) {
						stats.put("conv" + counter, k);
						counter++;
					}

					// System.out.println();
				}

				
				FileWriter remove_map_file = new FileWriter("remove_map.txt");
				printMap(remove_map, remove_map_file);
				remove_map_file.close();
                deletelog.close();
				System.out.println("stats: " + stats);
			}

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer;
			try {
				transformer = tf.newTransformer();
				DOMSource source = new DOMSource(document);
				transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				PrintWriter pw = new PrintWriter(new FileOutputStream(
						outputFileName));
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

    public static void StatsXml(String inputFileName,String outputFileName) {
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
                    int k = 0;
                    int wdLength=0;
                	
                    ArrayList<Integer> tempAL=new ArrayList<Integer>();
                    int turnCounter=1;
                    for (; k < userMeta.getLength(); k++) {
                    	
                        String content = userMeta.item(k).getTextContent();
                        if(content.equals("\n"))continue;
                        wdLength=content.length();
                        if(wdLength>0) {
                        	turnCounter++;

                        	tempAL.add(wdLength);
                        }
                        
                    }
                    //if there is actually utterance then record the # of utterance in a conversation
                    if(k>1) {
                    	stats.put("conv"+counter, (turnCounter-1));
                    	 Stats2d.add(tempAL);
                    	counter++;
                    }
                    System.out.println();
                }
                System.out.println("stats: "+stats);
                FileWriter output = new FileWriter(outputFileName);
                for (int ii=0;ii< Stats2d.size();ii++) {
                	
                	System.out.println("conv"+(ii+1)+" :");
                	for (int jj=0;jj<Stats2d.get(ii).size();jj++) {
                		System.out.println("turn"+(jj+1)+" :"+Stats2d.get(ii).get(jj)+" words");
                		output.write(Stats2d.get(ii).get(jj)+" ");
                	}
                	output.write("\n");
                	
                }
                output.close();
          
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
    public static void UIDStatsXml(String inputFileName,String outputFileName) {
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
                ArrayList<Integer> ppCounter=new ArrayList<Integer>();
                for (int j = 0; j < userInfo.getLength(); j++) {
                	
                    Node node = userInfo.item(j);
                    NodeList userMeta = node.getChildNodes();
                    int k = 0;
                   
                    int max=0;
                    for (; k < userMeta.getLength(); k++) {
                    	
                        String content = userMeta.item(k).getTextContent();
                        if(content.equals("\n"))continue;
                        Node value = userMeta.item(k).getAttributes().getNamedItem("uid");
                        max=Math.max(max, Integer.parseInt(value.getNodeValue()));

                    }
                    ppCounter.add(max);
                                }
                FileWriter output = new FileWriter(outputFileName);
                for (int tmpnum:ppCounter) {
                		if(tmpnum==0) continue;
                		output.write(Integer.toString(tmpnum));
                	
                	output.write("\n");
                	
                }
                output.close();
                
          
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




	public static void printMap(Map mp, FileWriter file) {
	    Iterator it = mp.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        try {
				file.write(pair.getKey() + " = " + pair.getValue()+"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}
}
