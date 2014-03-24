import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class SearchPageRank {

	/**
	 * @param args
	 */

	static int totalnumofpages = 14041179;
	static float pagerank[] = new float[50000000];

	public static void initialize(String folder, String pagerankfile) {
		try {
			File file = new File(folder + "/" + pagerankfile);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String line;
			float max=0;
			int index=0;
			while ((line = br.readLine()) != null) {
				String tok[] = line.split(":");
				int val1 = Integer.parseInt(tok[0]);
				float val2 = Float.parseFloat(tok[1]);
				pagerank[val1] = val2;
				if(val2>max){
					max=val2;
					index=val1;
				}
			}
			System.out.println(index+":"+max);
			br.close();
			fr.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String folder = "/media/shashank/Windows/Wikipedia/";
		String pagerankfile = "pagerank";

		System.out.println("Loading Pagerank");
		initialize(folder, pagerankfile);
		System.out.println("...Loaded!!");
		Scanner in=new Scanner(System.in);
		while (true) {
			String searchString=in.nextLine();
			if(searchString.equalsIgnoreCase("stop"))
				break;
			String pageids=fetchIndex(folder,searchString);
			if(pageids==null){
				System.out.println("NA");
				continue;
			}
			String tok[] = pageids.split(",");
			for (String s : tok) {
				int val = Integer.parseInt(s);
				System.out.println(pagerank[val]);
			}
		}
		in.close();
	}

	public static String fetchIndex(String folder,String val) {
		try {
			// System.out.println("Searching for:" + val);
			File file = new File(folder+"/searchid/tersearch");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				String tok[] = line.split(":");
				if (val.compareTo(tok[0]) < 0) {
					break;
				}
				i++;
			}
			file = new File(folder+"/searchid/secsearchid" + i);
			// System.out.println("Searching file:" + "./searchid/secsearchid"
			// +i);
			br.close();
			fr.close();
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			int store = i;
			i = 0;
			while ((line = br.readLine()) != null) {
				String tok[] = line.split(":");
				if (val.compareTo(tok[0]) < 0) {
					break;
				}
				i++;
			}
			if (store == 0)
				store = i + (store * 100);
			else
				store = i - 1 + (store * 100);
			file = new File("./searchid/searchid" + store);
			// System.out.println("Searching file:" + "./searchid/searchid"
			// +store);
			br.close();
			fr.close();
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			while ((line = br.readLine()) != null) {
				String tok[] = line.split(":");
				if (val.equals(tok[0])) {
					br.close();
					fr.close();
					char ch[] = tok[1].toCharArray();

					for (char c : ch) {
						if (!Character.isDigit(c) && c != ',')
							return null;
					}
					return tok[1];
				}
			}
			br.close();
			fr.close();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
		return null;
	}

}
