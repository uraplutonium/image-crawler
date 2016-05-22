package imagecrawler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.TreeSet;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class ImageCrawler {

	private static Set<String> keyword = new TreeSet<String>();
	private static Set<Integer> size = new TreeSet<Integer>();
	private static Set<String> processed = new TreeSet<String>();
	private static String path = new String();
	private static int depth = 0;
	private static byte[] bs = new byte[1048576];
	
	/**
	 * crawl the images from a certain url
	 * @param url The seed url
	 * @param keyword The keyword that must be contained in urls
	 * @param depth The maxmium depth the crawler crawl
	 * @param path The save path
	 */
	public static long crawl(String url, Set<String> key, int dep, String ph, long startnum) {
		keyword  = key;
		size.clear();
		processed.clear();
		depth = dep;
		path = ph;
		startnum = parserHtml(url, startnum);
		return startnum;
	}
	
	/**
	 * 词法分析网页
	 * @param url
	 */
	private static long parserHtml(String url, long num) {
		boolean contain = true;
		for(String key : keyword) {
			if(!(url.contains(key))) {
				contain = false;
			}
		}
		
		if(contain && !(processed.contains(url))) {
			processed.add(url);
			Parser parser = new Parser();
			try {
				System.out.println("Crawling within " + depth + " layer(s) for " + url);
				parser.setURL(url);
				parser.setEncoding("UTF-8");
				
				URLConnection uc = parser.getConnection();
				uc.connect();
				System.out.println("URL connected.");
				
				NodeIterator nit = parser.elements();
				while(nit.hasMoreNodes()) {
					Node node = nit.nextNode();
					num = parserNode(node, num);
				}			
			}
			catch(ParserException exc) {
				System.out.println("ParserException.");
			}
			catch(Exception exc) {
				System.out.println("IOException.");
			}
		}
		return num;
	}
	
	/**
	 * 词法分析结点
	 * @param node
	 */
	private static long parserNode(Node node, long num) {
		if(node instanceof TagNode) {
			if(node instanceof ImageTag){
				ImageTag n = (ImageTag)node;
				System.out.print("i_" + num + "\t");
				if(num%10 == 0)
					System.out.println();
				num++;
				try {
					download(n.getImageURL(), (path + num + ".jpg"));
				}
				catch(IOException exc) {
					System.out.println("IOException.");
				}
			}
			else if(node instanceof LinkTag) {
				LinkTag tag = (LinkTag)node;
				num = addLink(tag.getLink(), num);
			}
			num = dealtag(node, num);
		}
		return num;
	}
	
	/**
	 * 处理标签
	 * @param tag
	 */
	private static long dealtag(Node tag, long num) {
		NodeList list = tag.getChildren();
		if(list != null) {
			NodeIterator nit = list.elements();
			try {
				while(nit.hasMoreNodes()) {
					Node node = nit.nextNode();
					num = parserNode(node, num);
				}
			}
			catch(ParserException exc) {
				System.out.println("ParserException");
			}
		}
		return num;
	}
	
	/**
	 * 修饰并添加url
	 * @param link
	 * @param queue
	 */
	private static long addLink(String link, long num) {
		if (link != null && !link.equals("") && link.indexOf("#") == -1) {
			if (link.startsWith("www.")) {
				link = "http://" + link;
			}
			
			if(depth > 0) {
				depth--;
				System.out.println("\nURL:\t" + link);
				num = parserHtml(link, num);	// recursion
				depth++;
			}
		}
		return num;
	}
	  
	private static void download(String urlString, String filename) throws IOException{
		URL url = new URL(urlString);
		URLConnection con = url.openConnection();
		
		Integer length = con.getContentLength();
		if(length > 131072 && !(size.contains(length))) {
			System.out.println("######## Image downloaded ########");
			size.add(length);
			
			InputStream is = con.getInputStream();
			int len;
			OutputStream os = new FileOutputStream(filename);
			
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}				

			os.close();
			is.close();
		}
	}

}
