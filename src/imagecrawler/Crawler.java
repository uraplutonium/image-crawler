package imagecrawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Crawler {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String confpath = args[0];
		String savepath = args[1];
		
		BufferedReader reader = null;

		try {
			String line;
			String url;
			int depth;
			long num = 0;
			reader = new BufferedReader(new FileReader(confpath));
			while((line = reader.readLine()) != null){
				StringTokenizer token = new StringTokenizer(line);
				url = token.nextToken();
				depth = Integer.valueOf(token.nextToken());
				
				Set<String> keyword = new TreeSet<String>();
				while(token.hasMoreTokens()) {
					keyword.add(token.nextToken());
				}
				
				num = ImageCrawler.crawl(url, keyword, depth, savepath, num);	// Run ImageCrawler
			}
		}
		catch(IOException exc) {
			System.out.println("IOException.");
		}
		finally {
			if(reader!=null) {
				try{
					reader.close();
				}
				catch(IOException exc){
					System.out.println("IOException.");
				}
			}
		}		
	}
	
}
