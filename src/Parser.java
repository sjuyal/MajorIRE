import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Scanner;
import java.util.TreeMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.ire.handler.InitialHandler;
import com.ire.handler.SJHandler;

public class Parser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			long time1 = System.currentTimeMillis();
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

			// String from = args[0];
			// String to = args[1];
			String from ="/media/shashank/Windows/Wikipedia/enwiki-latest-pages-articles.xml";
			String to = "backward";
			String folder="/media/shashank/Windows/Wikipedia/fandbnew/";
			//String from = "sample.xml";
			//String to = "backward";
			//String folder=".";

			SAXParser saxParser = saxParserFactory.newSAXParser();
			
			/*InitialHandler ihandler=new InitialHandler();
			ihandler.to = to;
			saxParser.parse(new File(from), ihandler);
			*/
			
			
			SJHandler handler = new SJHandler();
			handler.to = to;
			handler.folder=folder;
			saxParser.parse(new File(from), handler);
			
			
			/*int pgcount = handler.getPagecount();
			TreeMap<String, ArrayList<String>> globalmap = SJHandler
					.getGlobalmap();
			int totalDocCount = (handler.getFilecount() * SJHandler
					.getSplitsize()) + pgcount;
			System.out.println("Doc Count:" + totalDocCount);

			if (pgcount > 0) {
				String filename = "/info" + handler.getFilecount() + ".txt";
				handler.setFilecount(handler.getFilecount() + 1);
				System.out.println("Last File:" + filename);
				File towrite = new File(to + filename);
				FileWriter fw;
				BufferedWriter bw = null;

				fw = new FileWriter(towrite.getAbsoluteFile(), false);
				bw = new BufferedWriter(fw);
				for (Map.Entry<String, ArrayList<String>> entry : globalmap
						.entrySet()) {
					// System.out.print(entry.getKey() + ":");
					bw.write(entry.getKey() + ":");
					ArrayList<String> alist = entry.getValue();
					StringBuilder sb = new StringBuilder();
					for (String s : alist) {
						sb.append(s + ",");
					}
					// System.out.println(sb.toString());
					bw.write(sb.toString());
					bw.newLine();
				}
				bw.close();
				fw.close();
			}

			if (handler.getFilecount() > 0) {
				int filecount = handler.getFilecount();
				
				int filec = 26;
				File towrite[] = new File[filec];
				char charact = 'a';
				for (int i = 0; i < filec; i++) {
					towrite[i] = new File(to + "/" + charact + ".txt");
					charact++;
				}
				FileWriter fw[] = new FileWriter[filec];
				for (int i = 0; i < filec; i++) {
					fw[i] = new FileWriter(towrite[i], false);
				}

				BufferedWriter bw[] = new BufferedWriter[filec];
				for (int i = 0; i < filec; i++) {
					bw[i] = new BufferedWriter(fw[i]);
				}

				File toread[] = new File[filecount];
				for (int i = 0; i < filecount; i++) {
					toread[i] = new File(to + "/info" + i + ".txt");
				}

				FileReader fr[] = new FileReader[filecount];
				for (int i = 0; i < filecount; i++) {
					fr[i] = new FileReader(toread[i]);
				}

				BufferedReader br[] = new BufferedReader[filecount];
				for (int i = 0; i < filecount; i++) {
					br[i] = new BufferedReader(fr[i]);
				}

				String line[] = new String[filecount];
				int flag = 0;
				String words[] = new String[filecount];
				for (int i = 0; i < filecount; i++) {
					line[i] = br[i].readLine();
					if (line[i] != null) {
						flag = 1;
						String ar[] = line[i].split(":");
						words[i] = ar[0];
					} else {
						words[i] = null;
					}
				}

				long y0, y1, x0, x1;
				x0 = 0;
				x1 = (long) (100000 * Math.log10(totalDocCount));
				y0 = 0;
				y1 = 99999;
				System.out.println("Ranging [" + x0 + "," + x1 + "] to [" + y0
						+ "," + y1 + "]");

				char ch = 'a';
				int chcount = 0;

				while (flag == 1) {
					// System.out.println("here2!");
					String smallest = "|";
					int index = 0;
					for (int i = 0; i < filecount; i++) {
						if (words[i] == null)
							continue;
						if (words[i].compareTo(smallest) < 0) {
							smallest = words[i];
							index = i;
						}
					}

					if (smallest.equalsIgnoreCase("|")) {
						flag = 0;
						continue;
					}

					StringBuilder sbhead = new StringBuilder();
					String rest[] = line[index].split(":");
					sbhead.append(smallest);

					StringBuilder sb = new StringBuilder();
					sb.append(rest[1]);

					for (int i = 0; i < filecount; i++) {
						if (words[i] == null)
							continue;
						if (smallest.equalsIgnoreCase(words[i]) && index != i) {
							String res[] = line[i].split(":");
							sb.append(res[1]);
							line[i] = br[i].readLine();
							if (line[i] != null) {
								res = line[i].split(":");
								words[i] = res[0];
							} else {
								words[i] = null;
							}
						}
					}
					String rec[] = (sb.toString()).split(",");
					int count = rec.length;

					StringBuilder sbuild = new StringBuilder();
					sbuild.append(sbhead.toString() + ":");
					String posting[] = sb.toString().split(",");
					String vals[] = posting[0].split("-");
					sbuild.append(vals[0]);
					sbuild.append("-");

					double idf;
					if (count != 0)
						idf = Math.log10(totalDocCount / count);
					else
						idf = 0;
					// System.out.println("IDF:"+idf);
					int tf = Integer.parseInt(vals[1]);
					double tfidf = tf * idf;
					int scaledtfidf = (int) (y0 + (y1 - y0) * (tfidf - x0)
							/ (x1 - x0));
					// System.out.println("Val1:"+tfidf+": Scaled1:"+scaledtfidf);
					sbuild.append(scaledtfidf + "");

					sbuild.append("-");
					sbuild.append(vals[2]);
					sbuild.append(",");

					long prev = Long.parseLong(vals[0]);
					for (int i = 1; i < posting.length - 1; i++) {
						vals = posting[i].split("-");
						long cur = Long.parseLong(vals[0]);
						long base = cur - prev;
						sbuild.append(cur + "");
						prev = cur;
						sbuild.append("-");

						tf = Integer.parseInt(vals[1]);
						tfidf = tf * idf;

						scaledtfidf = (int) (y0 + (y1 - y0) * (tfidf - x0)
								/ (x1 - x0));
						// System.out.println("Val:"+tfidf+": Scaled:"+scaledtfidf);
						sbuild.append(scaledtfidf + "");
						sbuild.append("-");
						sbuild.append(vals[2]);
						sbuild.append(",");
					}

					if (sbhead.toString().charAt(0) != ch) {
						ch = sbhead.toString().charAt(0);
						chcount++;
						String ret = makeSecondaryIndex(sbuild.toString());
						bw[chcount].write(ret);
						bw[chcount].newLine();
					} else {
						String ret = makeSecondaryIndex(sbuild.toString());
						bw[chcount].write(ret);
						bw[chcount].newLine();
					}
					line[index] = br[index].readLine();
					if (line[index] != null) {
						rest = line[index].split(":");
						words[index] = rest[0];
					} else {
						words[index] = null;
					}
				}
				for (int i = 0; i < filecount; i++) {
					br[i].close();
					fr[i].close();
				}
				for (int i = 0; i < filec; i++) {
					bw[i].close();
					fw[i].close();
				}

			}
		*/
			long time2 = System.currentTimeMillis() - time1;
			System.out.println("Time Taken in Seconds:" + time2 / 1000.0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("Total time taken:" + time2/1000);

	}

	private static String makeSecondaryIndex(String line) {
		try {

			String toks[] = line.split(":");
			String vals[] = toks[1].split(",");

			TreeMap<Integer, String> tmap = new TreeMap<Integer, String>();

			String indiv[] = vals[0].split("-");
			int rank = Integer.parseInt(indiv[1]);
			int base = Integer.parseInt(indiv[0]);
			StringBuilder sb1 = new StringBuilder();
			sb1.append(base);
			sb1.append("-");
			sb1.append(indiv[2]);
			tmap.put(rank, sb1.toString());

			for (int j = 0; j < vals.length - 1; j++) {
				indiv = vals[j].split("-");
				rank = Integer.parseInt(indiv[1]);
				int cur = Integer.parseInt(indiv[0]);
				// cur=base+cur;
				StringBuilder sb = new StringBuilder();
				sb.append(cur);
				sb.append("-");
				sb.append(indiv[2]);
				// base=cur;

				if (tmap.containsKey(rank)) {
					String val = tmap.get(rank);
					StringBuilder sbnew = new StringBuilder();
					sbnew.append(val);
					sbnew.append(",");
					sbnew.append(sb.toString());
					tmap.put(rank, sbnew.toString());
				} else {
					tmap.put(rank, sb.toString());
				}

			}
			NavigableMap<Integer, String> nmap = tmap.descendingMap();
			StringBuilder sb = new StringBuilder();
			sb.append(toks[0]);
			sb.append(":");
			for (Map.Entry<Integer, String> entry : nmap.entrySet()) {
				sb.append(entry.getKey() + "/");
				sb.append(entry.getValue() + "#");
			}
			return sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}

}
