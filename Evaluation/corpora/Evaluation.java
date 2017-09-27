package corpora;

import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Evaluation {
	public static void main(String[] args) throws IOException {
		String filename ="final_corpus.txt";
		BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
		String line;
		String sb = new String();

		while((line=br.readLine())!= null){
		    sb=sb+"\n"+(line.trim());
		}
	    final String str = sb;
	    //System.out.println(str); // test do not run
	}

	private static final Pattern TAG_REGEX = Pattern.compile(" >(.+?)</utt>");

	private static void getTagValues(final String str) {
	    final ArrayList<String> tagValues = new ArrayList<String>();
	    final Matcher matcher = TAG_REGEX.matcher(str);
	    while (matcher.find()) {
	        tagValues.add(matcher.group(1));
	    }
	    isChineseText(tagValues);
	}
	
	public static void isChineseText(ArrayList<String> s1) {
		int count =0;
		for(int j=0;j<s1.size();j++){
			String s= s1.get(j);
		    for (int i = 0; s != null && s.length() > 0 && i < s.length(); i++) {
		        char ch = s.charAt(i);
		        Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
		        if (Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(block)
		                || Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
		                        .equals(block)
		                || Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
		                        .equals(block)) {
		        	continue;
		        }
		        else{
		        	count++;
		        	break;
		        }

		    }
		}

	    System.out.println("toal number of non chinese words = "+ count);
	}
}
