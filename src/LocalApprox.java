import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LocalApprox {

	static Map<Integer, List<Integer>> globalbackward;
	static Map<Integer, Integer> globalforward;
	static Map<Integer, List<Integer>> localbackward;

	static int totalnumofpages = 14041179;
	static float pagerank[] = new float[50000000];

	static int radius = 2;
	static int levelid = 0;
	static int lastidvalue = 0;

	public static void initializePageRank(String folder, String pagerankfile) {
		try {
			File file = new File(folder + "/" + pagerankfile);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				String tok[] = line.split(":");
				int val1 = Integer.parseInt(tok[0]);
				float val2 = Float.parseFloat(tok[1]);
				pagerank[val1] = val2;

			}
			br.close();
			fr.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initializeBackwardIndex(String folder,
			String backwardIndex) {
		try {
			File file = new File(folder + "/" + backwardIndex);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			globalbackward = new HashMap<Integer, List<Integer>>();
			String line;
			while ((line = br.readLine()) != null) {
				String tok[] = line.split(":");
				String temp[] = tok[1].split(",");

				List<Integer> param = new ArrayList<Integer>();

				for (String s : temp) {
					param.add(Integer.parseInt(s));
				}

				globalbackward.put(Integer.parseInt(tok[0]), param);
			}

			br.close();
			fr.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void initializeForwardIndex(String folder, String forwardIndex) {
		try {
			File file = new File(folder + "/" + forwardIndex);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			globalforward = new HashMap<Integer, Integer>();
			String line;
			while ((line = br.readLine()) != null) {
				String tok[] = line.split(":");
				globalforward.put(Integer.parseInt(tok[0]),
						Integer.parseInt(tok[1]));
			}

			br.close();
			fr.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			String folder = "/media/shashank/Windows/Wikipedia/fandbnew/";
			String backwardIndex = "backward-index";
			String forwardIndex = "forward-index";
			String pagerankFile = "pagerank";

			System.out.println("Loading Backward Index");
			initializeBackwardIndex(folder, backwardIndex);
			System.out.println("...Loaded!!");

			System.out.println("Loading Forward Index");
			initializeForwardIndex(folder, forwardIndex);
			System.out.println("...Loaded!!");

			System.out.println("Loading Pagerank");
			initializePageRank(folder, pagerankFile);
			System.out.println("...Loaded!!");

			Scanner in = new Scanner(System.in);
			while (true) {
				levelid = 0;
				lastidvalue = 0;
				String queryPid = in.nextLine();

				long time1 = System.currentTimeMillis();
				long time2= 0;
				float queryPagerank = 0f;
				// File dump = new File("dump");
				// FileWriter fr = new FileWriter(dump);
				// BufferedWriter br = new BufferedWriter(fr);
				List<Integer> localqueue = new LinkedList<Integer>();

				localqueue = globalbackward.get(Integer.parseInt(queryPid));

				if (localqueue != null) {
					lastidvalue = localqueue.get((localqueue.size() - 1));
					// br.write(localqueue + "\n");

					levelid++;

					List<HashMap<Integer, Float>> levelInfluenceMaplist = new ArrayList<HashMap<Integer, Float>>();
					HashMap<Integer, Float> tempLevelInfluenceMap = new HashMap<Integer, Float>();
					for (Integer val : localqueue) {
						float inf = 1.0f / globalforward.get(val);
						tempLevelInfluenceMap.put(val, inf);
						// br.write("Influnce:" + val + "--" + inf + "\n");
					}
					for (Map.Entry<Integer, Float> entry : tempLevelInfluenceMap
							.entrySet()) {
						queryPagerank += 0.15 * Math.pow(0.85, levelid)
								* entry.getValue();
					}
					levelInfluenceMaplist.add(tempLevelInfluenceMap);

					while (localqueue.size() != 0) {
						if (levelid == radius) {
							break;
						}
						time2 = System.currentTimeMillis() - time1;
						if((time2/1000.0) > 12.0){
							System.out.println("System is taking longer than usual: In degree of this node is high!");
							break;
						}
						int temp = localqueue.get(0);
						List<Integer> templist = globalbackward.get(temp);
						if (templist != null)
							localqueue.addAll(globalbackward.get(temp));
						localqueue.remove(0);
						if (temp == lastidvalue) {
							levelid++;
							lastidvalue = localqueue
									.get((localqueue.size() - 1));

							HashMap<Integer, Float> tempMap = levelInfluenceMaplist
									.get(levelInfluenceMaplist.size() - 1);
							tempLevelInfluenceMap = new HashMap<Integer, Float>();
							for (Map.Entry<Integer, Float> entry : tempMap
									.entrySet()) {
								List<Integer> tlist = globalbackward.get(entry
										.getKey());
								if (tlist == null)
									continue;
								for (int val : tlist) {
									float newinf = (1.0f / globalforward
											.get(val)) * entry.getValue();
									if (tempLevelInfluenceMap.get(val) == null)
										tempLevelInfluenceMap.put(val, newinf);
									else {
										float tempinf = tempLevelInfluenceMap
												.get(val);
										newinf += tempinf;
										tempLevelInfluenceMap.put(val, newinf);
									}
									// br.write("Influnce:" + val + "--" +
									// newinf+ "\n");
								}
								tlist.clear();
							}
							tempMap.clear();
							levelInfluenceMaplist.add(tempLevelInfluenceMap);
							float tempqueryPagerank = 0f;
							for (Map.Entry<Integer, Float> entry : tempLevelInfluenceMap
									.entrySet()) {
								tempqueryPagerank += 0.15f
										* Math.pow(0.85, levelid)
										* entry.getValue();
							}
							queryPagerank += tempqueryPagerank;
							tempLevelInfluenceMap.clear();
						}
					}
					localqueue.clear();
					levelInfluenceMaplist.clear();
				}
				System.out.println("NewPageRank:" + queryPid + "-->"
						+ queryPagerank);
				System.out.println("www");
				System.out.println("OldPageRank:" + queryPid + "-->"
						+ pagerank[Integer.parseInt(queryPid)]);
				System.out.println("Time:"+time2/1000.0);
				// br.close();
				// fr.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
