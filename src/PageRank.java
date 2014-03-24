import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PageRank 

{

	static LinkedHashMap<Integer, Integer> forwardmap = new LinkedHashMap<Integer, Integer>();
	static LinkedHashMap<Integer, String> backwardmap = new LinkedHashMap<Integer, String>();

	public static void initialize(String folder, String forward, String backward) {
		try {
			File file = new File(folder + "/" + forward);
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				String tok[] = line.split(":");
				forwardmap.put(Integer.parseInt(tok[0]),
						Integer.parseInt(tok[1]));
			}

			br.close();
			fr.close();
			file = new File(folder + "/" + backward);
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			while ((line = br.readLine()) != null) {
				String tok[] = line.split(":");
				backwardmap.put(Integer.parseInt(tok[0]), tok[1]);
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			String folder = "/media/shashank/Windows/Wikipedia/";
			String forward = "forward-index";
			String backward = "backward-index";

			System.out.println("Loading...");
			initialize(folder, forward, backward);
			System.out.println("...Loaded!!");

			int totalnumofpages = 14041179;

			float damping = 0.50f;

			float pagerank[] = new float[50000000];
			float const1 = (1 - damping);
			float const2 = damping;
			int flag = 1;
			int i=1,count=0,precount=0;
			while (flag == 1) {
				System.out.println("Iteration:"+i);
				precount=count;
				count=0;
				for (Map.Entry<Integer, Integer> entry : forwardmap.entrySet()) {
					float backlinkfactor = 0.0f;
					//System.out.println(entry.getKey());
					String backlinks = backwardmap.get(entry.getKey());
					if (backlinks == null){
						count++;
						continue;
					}
					String tok[] = backlinks.split(",");
					for (String s : tok) {
						int val = Integer.parseInt(s);
						backlinkfactor += ((pagerank[val]) / forwardmap.get(val));
					}
					float newpagerank=const1 + const2 * (backlinkfactor);
					//System.out.println(newpagerank+"--"+pagerank[entry.getKey()]);
					if(Math.abs(newpagerank-pagerank[entry.getKey()])<0.000005f){
						count++;
					}
					pagerank[entry.getKey()] = newpagerank;
				}

				System.out.println("Settled so far:"+count);
				if((count==totalnumofpages) | Math.abs(precount-count)<100000)
					break;
				i++;
			}
			File file=new File(folder+"/pagerank");
			FileWriter fw=new FileWriter(file);
			BufferedWriter bw=new BufferedWriter(fw);
			
			for(i=0;i<totalnumofpages;i++){
				bw.write(i+":"+pagerank[i]);
				bw.newLine();
			}
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
