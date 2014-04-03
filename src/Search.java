import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

import com.ire.engine.Stemmer;

public class Search {

	private static String stopwords[] = { "a", "b", "c", "d", "e", "f", "g",
			"h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
			"v", "w", "x", "y", "z", "able", "about", "across", "after", "all",
			"almost", "also", "am", "among", "another", "an", "and", "any",
			"are", "as", "at", "be", "because", "been", "but", "by", "can",
			"cannot", "could", "dear", "did", "do", "does", "either", "else",
			"ever", "every", "for", "from", "get", "got", "had", "has", "have",
			"he", "her", "hers", "him", "his", "how", "however", "i", "if",
			"in", "into", "is", "it", "its", "just", "least", "let", "like",
			"likely", "may", "me", "might", "most", "must", "my", "neither",
			"no", "nor", "not", "now", "of", "off", "often", "on", "only",
			"or", "other", "our", "own", "put", "rather", "said", "say",
			"says", "she", "should", "since", "so", "some", "take", "than",
			"that", "the", "their", "them", "then", "there", "these", "they",
			"this", "tis", "to", "too", "twas", "us", "wants", "was", "we",
			"were", "what", "when", "where", "which", "while", "who", "whom",
			"why", "will", "with", "would", "yet", "you", "your", "http",
			"cite", "www", "com" };

	private static HashSet<String> stop = new HashSet<String>();

	static {
		for (String str : stopwords) {
			stop.add(str);
		}

	}

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
			br.close();
			fr.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static String to = "/media/shashank/Windows/Wikipedia/earlier/";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			// String to = args[0];
			// String search = args[1];

			String folder = "/media/shashank/Windows/Wikipedia/fandbnew";
			String pagerankfile = "pagerank";

			//System.out.println("Loading Pagerank");
			initialize(folder, pagerankfile);
			//System.out.println("...Loaded!!");
			String query = "";
			Scanner reader = new Scanner(System.in);
			query = reader.nextLine();
			
			// 1-Title 2-Body 3-Infobox 4-Link 5-Category

			String searchvalues[] = query.split(" ");
			String[] s = new String[100];
			int wcount = 0;
			long time1 = System.currentTimeMillis();
			for (String sea : searchvalues) {
				
				int type = 0;
				String search;
				String cat[] = sea.split(":");
				if (cat.length == 2) {
					if (cat[0].equalsIgnoreCase("t"))
						type = 1;
					else if (cat[0].equalsIgnoreCase("b"))
						type = 2;
					else if (cat[0].equalsIgnoreCase("i"))
						type = 3;
					else if (cat[0].equalsIgnoreCase("l"))
						type = 4;
					else if (cat[0].equalsIgnoreCase("c"))
						type = 5;
					search = cat[1];
				} else {
					search = cat[0];
				}

				StringBuilder sb = new StringBuilder();
				sb.append(search);
				if (stop.contains(search)) {
					continue;
				}
				StringBuilder sb2 = new StringBuilder();
				for (int i = 0; i < search.length(); i++) {
					char c = sb.charAt(i);
					if (c < 0x30 || (c >= 0x3a && c <= 0x40)
							|| (c > 0x5a && c <= 0x60) || c > 0x7a)
						sb.setCharAt(i, ' ');
					else
						sb2.append(c);
				}
				search = sb2.toString();
				search = search.trim();
				Stemmer stemmer = new Stemmer();
				search = search.toLowerCase();
				char[] cArray = search.toCharArray();
				stemmer.add(cArray, search.length());
				stemmer.stem();
				search = stemmer.toString();

				// System.out.println(search);

				char ch = search.charAt(0);
				//File toread = new File(to + "/" + ch + "old/");
				// System.out.println(to + "/" + search.charAt(0) + ".txt");
				//FileReader fr = new FileReader(toread);
				//BufferedReader br = new BufferedReader(fr);
				//System.out.println(search);
				s[wcount] = SearchIndex(search, ch, type,to);
				//System.out.println(s[wcount]);
				wcount++;
				//br.close();
				//fr.close();
			}
			
			long time2 = System.currentTimeMillis() - time1;
			System.out.println("\n\nTotal time taken:" + time2 / 1000.0);

			LinkedHashSet<String> lhs = new LinkedHashSet<String>();
			// System.out.println(wcount);
			String set1[] = s[0].split(",");
			TreeMap<Integer, Integer> tmap = new TreeMap<Integer, Integer>();
			// System.out.println(set1.length);
			tmap.put(set1.length, 0);
			// System.out.println(set1.length);
			for (String s2 : set1) {
				lhs.add(s2);
			}
			for (int i = 1; i < wcount; i++) {
				LinkedHashSet<String> lhs2 = new LinkedHashSet<String>();
				set1 = s[i].split(",");
				tmap.put(set1.length, i);

				for (String s1 : set1) {
					lhs2.add(s1);
				}
				lhs.retainAll(lhs2);
			}

			int count = 0;
			TreeSet<String> answerset = new TreeSet<String>();
			for (String ans : lhs) {

				if(ans.equals(""))
					continue;
				String result = searchtitle(ans);
				
				if (!(result.equalsIgnoreCase(""))
						&& !(result.equalsIgnoreCase("Category"))
						&& !(result.equalsIgnoreCase("Template"))
						&& !(result.equalsIgnoreCase("Portal"))) {
					String vals[]=ans.split(",");
					int ind=Integer.parseInt(vals[0]);
					System.out.println("PageID:"+vals[0]+"-----Page Title:"+result+"-----PageRank: "+pagerank[ind]);
					count++;
				}
				answerset.add(ans);
				if (count == 10)
					break;
			}
			// System.out.println("Here"+wcount);
			if (count < 10) {
				for (Map.Entry<Integer, Integer> entry : tmap.entrySet()) {

					int index = entry.getValue();
					String arr[] = s[index].split(",");
					for (String rem : arr) {
						if (!answerset.contains(rem)) {
							String result = searchtitle(rem);
							if (!(result.equalsIgnoreCase(""))
									&& !(result.equalsIgnoreCase("Category"))
									&& !(result.equalsIgnoreCase("Template"))
									&& !(result.equalsIgnoreCase("Portal"))) {
								String vals[]=rem.split(",");
								int ind=Integer.parseInt(vals[0]);
								System.out.println("PageID:"+vals[0]+"-----Page Title:"+result+"-----PageRank: "+pagerank[ind]);
								count++;
							}
							answerset.add(rem);
						}
						if (count == 10)
							break;
					}
					if (count == 10)
						break;
				}
			}
			reader.close();
		} catch (Exception e) {
			 e.printStackTrace();
		}

	}

	private static String SearchIndex(String search, char ch, int type, String folder) {
		try {
			if(Character.isDigit(ch))
				return "";
			//System.out.println(folder+"/"+ch+"old"+"/secold_"+ch);
			File file = new File(folder+"/"+ch+"old"+"/secold_"+ch);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			int i = 0;
			String val=search;
			while ((line = br.readLine()) != null) {
				String tok[] = line.split(":");
				if (val.compareTo(tok[0]) < 0) {
					break;
				}
				i++;
			}
			//System.out.println(folder+"/"+ch+"old"+"/"+ch+"oldfinal"+i);
			file = new File(folder+"/"+ch+"old"+"/"+ch+"oldfinal"+i);
			br.close();
			fr.close();
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			StringBuilder res = new StringBuilder();
			int flag=0;
			while ((line = br.readLine()) != null) {
				String toks[] = line.split(":");
				if (val.equals(toks[0])) {
					String vals[] = toks[1].split("#");
					for (i = 0; i < vals.length; i++) {
						String rankdocspair[] = vals[i].split("/");
						String docs[] = rankdocspair[1].split(",");
						for (String doc : docs) {
							String docid[] = doc.split("-");
							boolean ret = false;
							if (type != 0) {
								ret = checkField(docid[1], type);
								if (ret)
									res.append(docid[0] + ",");
							} else
								res.append(docid[0] + ",");
						}
					}
					flag = 1;
					break;
				}
			}

			br.close();
			fr.close();
			if (flag == 0)
				return "";

			return res.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ""; 
	}

	private static String searchtitle(String ans) {
		try {
			if(ans.equals(""))
				return "";
			File f = new File(to + "/title.txt");
			RandomAccessFile raf = new RandomAccessFile(f, "r");

			long start = 0;
			long end = f.length();
			long middle;
			// System.out.println("To Search:"+ans);
			while (start <= end) {
				middle = (start + end) / 2;
				raf.seek(middle);
				raf.readLine();
				String line = raf.readLine();
				String tok[];
				if (line != null) {
					tok = line.split(":");
				} else
					continue;
				// System.out.println(tok[0]+" Start:"+start+" Middle:"+middle+" End:"+end);
				if (tok[0].equalsIgnoreCase(ans)) {
					raf.close();
					return tok[1];
				} else {
					long ansl = Long.parseLong(ans);
					long tokl = Long.parseLong(tok[0]);
					if (tokl < ansl) {
						start = middle + 1;
					} else {
						end = middle - 1;
					}
				}
			}
			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public static boolean checkField(String val, int type) {
		int field = Integer.parseInt(val);
		int newval = 0;
		int flag = 0;
		switch (type) {
		case 1:
			newval = 1 << 4;
			newval = newval & field;
			if (newval == 16)
				flag = 1;
			break;
		case 2:
			newval = 1;
			newval = newval & field;
			if (newval == 1)
				flag = 1;
			break;
		case 3:
			newval = 1 << 3;
			newval = newval & field;
			if (newval == 8)
				flag = 1;
			break;
		case 4:
			newval = 1 << 1;
			newval = newval & field;
			if (newval == 2)
				flag = 1;
			break;
		case 5:
			newval = 1 << 2;
			newval = newval & field;
			if (newval == 4)
				flag = 1;
			break;
		}

		if (flag == 0)
			return false;
		else
			return true;
	}

	public static String binarySearch(String s, File to, int type) {
		try {
			RandomAccessFile raf = new RandomAccessFile(to, "r");
			String searchValue = s;
			//System.out.println(s);
			StringBuilder res = new StringBuilder();
			long bottom = 0;
			long top = to.length();
			long middle;
			int flag = 0;
			while (bottom <= top) {
				middle = (bottom + top) / 2;
				raf.seek(middle);

				// middle += (str.length()/2);
				// System.out.println(str);
				String nline = raf.readLine();
				nline = raf.readLine();
				String toks[];
				if (nline != null)
					toks = nline.split(":");
				else
					continue;
				int comparison = toks[0].compareTo(searchValue);
				// if(toks[0].equalsIgnoreCase("actric")){
				// break;
				// }
				//System.out.println("Low:"+bottom+" Mid:"+middle+" High:"+top+" Val:"+toks[0]);

				if (comparison == 0) {
					System.out.println("Here");
					String vals[] = toks[1].split("#");
					for (int i = 0; i < vals.length; i++) {
						String rankdocspair[] = vals[i].split("/");
						String docs[] = rankdocspair[1].split(",");
						for (String doc : docs) {
							String docid[] = doc.split("-");
							boolean ret = false;
							if (type != 0) {
								ret = checkField(docid[1], type);
								if (ret)
									res.append(docid[0] + ",");
							} else
								res.append(docid[0] + ",");
						}
					}
					flag = 1;
					break;
				} else if (comparison < 0) {
					// line comes before searchValue
					bottom = middle + 1;
				} else {
					top = middle - 1;
				}
			}

			raf.close();
			if (flag == 0)
				return "";
			return res.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

}
