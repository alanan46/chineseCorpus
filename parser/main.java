package jsoup_parser;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;





import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class main {
    public static void main(String[] args) throws IOException {
         //File input = new File("test.html"); 
    	 //Document doc = Jsoup.parse(input, "UTF-8", ""); 
         Document doc = Jsoup.connect("http://tieba.baidu.com/p/4960646551").get(); 
    	 
    	 FileWriter output = new FileWriter("result.txt");
    	 output.write("<dialog>\n");

    	 
    	 
    	 Elements post_userID_divs = doc.select("div[class=d_author]"); 
    	 
    	 List<String> post_userIDs = new LinkedList<String>();  
    	 List<String> post_datas = new LinkedList<String>();  
    	 List<List<String>> reply_datas = new LinkedList<List<String>>();  
    	 List<List<String>> comment_post_names = new LinkedList<List<String>>();  
    	 
    	 post_datas.add("");
    	 
    	 //the comments for the first flow
    	 reply_datas.add(new LinkedList<String>());
    	 comment_post_names.add(new LinkedList<String>());
    	 
    	 for(Element ele : post_userID_divs)
    	 {
    		 Element li = ele.select("li[class=d_name]").first();
    		 Element a = li.select("a").first();
    		 String name = a.text();
    		 if(name =="")
    			 continue;
    		 System.out.println(name);
    		 post_userIDs.add(name);
    	 }
    	 
    	 Elements blocks = doc.select("div[class=d_post_content_main]"); 
    	 
    	 System.out.println("name length:"+post_userIDs.size()+" block length:"+blocks.size());

    	 int i = 0;
    	 for (Element block : blocks) {
    		 
    		 //This is to see the code of a block which include several comments.
    		 //System.out.println(block);
    		 
    		 Element primary_reply = block.getElementsByClass("d_post_content").first();
        	 Element primary_user_name = block.select("a[alog-group=p_author]").first();


        	 Elements sub_comments = block.select("span[class=lzl_content_main]");
        	 Elements user_names = block.select("a[alog-group=p_author]");
        	 System.out.println("lengths:"+sub_comments.size()+" "+user_names.size());
        	 System.out.println("Dialogue "+(i+1));
        	 post_datas.add(primary_reply.text());
        	 System.out.println(primary_reply.text());
        	 List<String> temp_comments = new LinkedList<String>();  
        	 List<String> temp_comment_post_names = new LinkedList<String>();  
        	 int length = user_names.size();
        	 for(int j = 0; j < length; j++)
        	 {
        		 System.out.println(user_names.get(j).text());
        		 temp_comment_post_names.add(user_names.get(j).text());
        		 temp_comments.add(sub_comments.get(j).text());
        	 }
        	 reply_datas.add(temp_comments);
        	 comment_post_names.add(temp_comment_post_names);
    		 i += 1;
    		 }
    	 System.out.println(post_userIDs.size()+" "+post_datas.size()+" "+ reply_datas.size()+" "+comment_post_names.size());
    	 
    	 for(i=1;i<post_userIDs.size();i++)
    	 {
    		 Map name_map = new HashMap();
    		 
    		 name_map.put(post_userIDs.get(i), 1);
    		 int k = 2;
    		 for(String name : comment_post_names.get(i))
    		 {
    			 if(!name_map.containsKey(name))
    			 {
    				 name_map.put(name, k);
    				 k++;
    			 }
    		 }
    		 
    		 if(reply_datas.get(i).size()>0)
    		 {
    			 output.write("<s>");
    			 output.write("<utt uid=\""+name_map.get(post_userIDs.get(i))+"\">"+post_datas.get(i)+"</utt>");
    			 int n_comments = reply_datas.get(i).size();
    			 for(int j = 0;j < n_comments;j++)
    			 {
        			 output.write("<utt uid=\""+name_map.get(comment_post_names.get(i).get(j))+"\">"+reply_datas.get(i).get(j)+"</utt>");
    			 }
    			 output.write("</s>\n");
    		 }
    	 }
    	 output.write("</dialog>");
    	 output.close();
    }
}
