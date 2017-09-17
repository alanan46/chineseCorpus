package jsoup_parser;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {
         File input = new File("test.html"); 
    	 Document doc = Jsoup.parse(input, "UTF-8", ""); 
    	 
    	 Elements comments = doc.select("div[class=d_post_content_main]"); 

    	 int i = 1;
    	 for (Element comment : comments) { 
        	 System.out.println("Dialogue "+i);
    		 i += 1;
        	 Element first_floor = comment.select("div[class=d_post_content j_d_post_content  clearfix]").first();
        	 Elements sub_comments = comment.select("span[class=lzl_content_main]");

        	 for (Element com : sub_comments) { 
    		  System.out.println(com.text());
    		 }
        	 
    		 }
    }
}
