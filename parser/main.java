package jsoup_parser;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;






import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

public class main {
	
	

    public static void main(String[] args) throws IOException {
    	
    	
    	String url_file_name_prefix = "final_url2";
    	int save_interval = 500;
    	
    	String url_file_name  = url_file_name_prefix;
    	
    	List<String> urls = readFile01(url_file_name + ".txt");
    	
    	
    	int i = 0;
    	int total_dialogue = 0;
    	int tmp_dialogue = 0;

 	    FileWriter output = new FileWriter(url_file_name+i+"result.txt");
   	    output.write("<dialog>\n");
   	    


 	    FileWriter log = new FileWriter("log.txt");
    	for(String url : urls)
    	{
    		System.out.println("url #"+i);
    		tmp_dialogue = parse(url,output, log);
    		total_dialogue += tmp_dialogue;
    		System.out.println("extracted "+tmp_dialogue+" dialogues from this page.");
    		System.out.println("extracted "+total_dialogue+" dialogues altogether.");
    		log.write("extracted "+tmp_dialogue+" dialogues from this page.");
    		log.write("extracted "+total_dialogue+" dialogues altogether.");
       	    i++;
       	    if(i%save_interval == 0)
       	    {
           	 output.write("</dialog>");
        	 output.close();
        	 System.out.println("save file to "+url_file_name+(i-50)+"result.txt");
        	 log.write("save file to "+url_file_name+(i-50)+"result.txt");
        	 output = new FileWriter(url_file_name+i+"result.txt");
        	 output.write("<dialog>\n");
       	    }
    	}

        output.write("</dialog>");
   	    output.close();
   	    log.close();

    }
	
    public static int parse(String url,FileWriter output, FileWriter log) throws IOException {
    	 
    	
     	 Pattern p = Pattern.compile("https*://tieba.baidu.com/p/(\\d+)\\D*"); 
    	 Matcher m = p.matcher(url);
         m.find();
         String tid = m.group(1);
         
         System.out.println("url: "+ url);

      //  File input = new File("test.html"); 
     	 Document doc = Jsoup.connect(url).get(); 
    	 
    	 Elements post_userID_divs = doc.select("div[class=d_author]"); 
    	 
    	 List<String> post_userIDs = new LinkedList<String>();  
    	 List<String> post_datas = new LinkedList<String>();  
    	 List<List<String>> reply_datas = new LinkedList<List<String>>();  
    	 List<List<String>> reply_userIDs = new LinkedList<List<String>>();  
    	 
    	 post_datas.add("");
    	 
    	 //the comments for the first flow
    	 reply_datas.add(new LinkedList<String>());
    	 reply_userIDs.add(new LinkedList<String>());
    	 
    	 for(Element ele : post_userID_divs)
    	 {
    		 Element li = ele.select("li[class=d_name]").first();
    		 Element a = li.select("a").first();
    		 String name = a.text();
    		 if(name =="")
    			 continue;
    		 post_userIDs.add(name);
    	 }
    	 
    	 Elements blocks = doc.select("div[class=d_post_content_main]"); 
    	 

    	 int i = 0;
    	 int n_dia = 0;
    	 for (Element block : blocks) {
    		 
    		 //This is to see the code of a block which include several comments.
    		 //System.out.println(block);
    		 //System.out.println("-------------------------------------------------");
    		 Element ele_with_post_data = block.getElementsByClass("d_post_content").first();


        	 
        	 //System.out.println("Dialogue "+(i+1));
        	 
        	 post_datas.add(ele_with_post_data.text());
        	 
        	 List<String> temp_replys = new LinkedList<String>();  
        	 List<String> temp_reply_userIDs = new LinkedList<String>();  
        	 
        	 String pid = block.select("a[class=l_post_anchor]").attr("name");
        	 if(pid!="")
        	 {
        		 n_dia++;
            	 System.out.println("Dialogue "+n_dia);
        		 int k = 1;
        		 while(true)
        		 {
            		 Document reply_doc = getHiddenData(tid,pid,Integer.toString(k), log);
            		 //System.out.println(reply_doc);
            		 k++;
            		 
            		 Elements replys = reply_doc.getElementsByClass("lzl_single_post");
            		 if(replys.size()==0)
            		 {
            			 break;
            		 }
            		 for(Element reply:replys)
            		 {
            			 temp_reply_userIDs.add(reply.select("a[class=at j_user_card ]").first().text());
            			 //System.out.println("reply user id:"+reply.select("a[class=at j_user_card ]").first().text());
            			 temp_replys.add(reply.select("span[class=lzl_content_main]").first().text());
            			 //System.out.println("reply content:"+reply.select("span[class=lzl_content_main]").first().text());
            		 }
        		 }
        	 }
        	 
        	 
        	 reply_datas.add(temp_replys);
        	 reply_userIDs.add(temp_reply_userIDs);
    		 i += 1;
    		 }
    	 
    	 // Those four lists should have the same size which is the number of posts in a page.
    	 System.out.println("post_userIDs.size:"+post_userIDs.size()+" post_datas.size:"+post_datas.size()+" reply_datas.size:"+ reply_datas.size()+" reply_userIDs.size:"+reply_userIDs.size());
    	 log.write("post_userIDs.size:"+post_userIDs.size()+" post_datas.size:"+post_datas.size()+" reply_datas.size:"+ reply_datas.size()+" reply_userIDs.size:"+reply_userIDs.size());
    	 
    	 
    	 int n_post = post_userIDs.size();
    	 
    	 //Dump the extracted data into the result file in the required format.
    	 // i begins from 1 since the first post doesn't have a reply. In fact, other posts are all reply to the first one but we don't take any post together with the first post as a dialogue yet.
    	 for(i=1;i<n_post;i++)
    	 {
    		 
    		 if(reply_datas.get(i).size()>0)
    		 {

        		 // maps names to numbers
        		 Map name_map = new HashMap();
        		 
        		 name_map.put(post_userIDs.get(i), 1);
        		 int k = 2;
        		 for(String name : reply_userIDs.get(i))
        		 {
        			 if(!name_map.containsKey(name))
        			 {
        				 name_map.put(name, k);
        				 k++;
        			 }
        		 }
    			 
    			 output.write("<s>");
    			 output.write("<utt uid=\""+name_map.get(post_userIDs.get(i)).toString().replaceAll("[<>]","")+"\" "+" uname=\""+post_userIDs.get(i).toString().replaceAll("[<>]","")+"\" >"+post_datas.get(i).toString().replaceAll("[<>]","")+"</utt>");
    			 int n_comments = reply_datas.get(i).size();
    			 for(int j = 0;j < n_comments;j++)
    			 {
        			 output.write("<utt uid=\""+name_map.get(reply_userIDs.get(i).get(j)).toString().replaceAll("[<>]","")+"\" "+" uname=\""+reply_userIDs.get(i).get(j).toString().replaceAll("[<>]","")+"\" >"+reply_datas.get(i).get(j).toString().replaceAll("[<>]","")+"</utt>");
    			 }
    			 output.write("</s>\n");
    		 }
    	 }
    	 return n_dia;
    }
    
    public static List<String> readFile01(String file_name) throws IOException {
        FileReader fr=new FileReader(file_name);
        BufferedReader br=new BufferedReader(fr);
        String line="";
        String[] arrs=null;
        List<String> result = new LinkedList<String>();
        
        //For the first line
        line=br.readLine();
        while ((line=br.readLine())!=null) {
        	result.add(line);
        }
        br.close();
        fr.close();
        return result;
    }
    
	public static Document getHiddenData(String tid,String pid,String pn, FileWriter log) throws IOException {
		//build url
		String url="http://tieba.baidu.com/p/comment?tid="+tid+"&pid="+pid+"&pn="+pn+"&t=1505875331044";
		System.out.println("reply url:"+url);
		log.write("reply url:"+url);
		//make connection
		Document doc = Jsoup.connect(url).get();
//		Document doc = Jsoup.connect("http://tieba.baidu.com/p/comment?tid=3077857561&pid=51478739271&pn=2&t=1505875331044").get();
		// output to file and return a File object, here can be changed to return any desired type
		return doc;
	}
}
