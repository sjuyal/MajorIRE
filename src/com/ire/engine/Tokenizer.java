package com.ire.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import com.ire.basic.Page;

public class Tokenizer {

	static HashMap<String, String> globalhmap=new HashMap<String, String>();
	
	static{
		try {
			System.out.println("Started! :O");
			File file=new File("tlpid-index.txt");
			FileReader fr=new FileReader(file);
			BufferedReader br=new BufferedReader(fr);
			String line;
			while((line=br.readLine())!=null){
				String tok[]=line.split(":");
				if(tok.length==2){
					char ch[] = tok[1].toCharArray();
					int flag=0;
					for (char c : ch) {
						if (!Character.isDigit(c) && c != ','){
							flag=1;
							break;
						}
					}
					if(flag==0)
						globalhmap.put(tok[0], tok[1]);
				}
			}
			br.close();
			fr.close();
			System.out.println("Done! :)");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void forward(String to,Page page){
		try {

			String text = page.getRevision().getText();

			char[] txtArray = text.toCharArray();
			int i = 0, j = 0;

			int txlen = txtArray.length;
			Vector<String> links = new Vector<String>();
			for (j = 0; j < txlen - 2; j++) {
				String chk = new String(txtArray, j, 2);
				if (chk.equalsIgnoreCase("[[")) {
					// stack.push("[[");
					for (i = j + 2; i < txlen; i++) {
						if (txtArray[i] == '|' | txtArray[i] == ']') {
							String val = new String(txtArray, j + 2, i - j - 2);
							String split[] = val.split(":");
							if (split[0].length() != 2) {
								//String ind = fetchIndex(val);
								
								String ind=globalhmap.get(val);
								if (ind == null){
									//System.out.println("Pageid:" + page.getId()
									//		+ " Not Found for:" + val);
								}else {
									//System.out.println(val+" found id:" + ind);
									links.add(ind);
								}
							}
							j = i + 1;
							break;
						}
					}
				}
			}

			File file = new File(to+"/"+"forward");
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(page.getId() + ":");
			 
			// System.out.println(page.getId());
			// System.out.println(links);
			
			for (String s : links) {
				String tok[]=s.split(",");
				for(String str:tok){
					bw.write(str+",");
				}
			}
			bw.newLine();
			bw.close();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static HashMap<String, String> backward(String folder,Page page) {
		try {

			String text = page.getRevision().getText();

			char[] txtArray = text.toCharArray();
			int i = 0, j = 0;

			int txlen = txtArray.length;
			Vector<String> links = new Vector<String>();
			for (j = 0; j < txlen - 2; j++) {
				String chk = new String(txtArray, j, 2);
				if (chk.equalsIgnoreCase("[[")) {
					// stack.push("[[");
					for (i = j + 2; i < txlen; i++) {
						if (txtArray[i] == '|' | txtArray[i] == ']') {
							String val = new String(txtArray, j + 2, i - j - 2);
							String split[] = val.split(":");
							if (split.length != 2) {
								//String ind = fetchIndex(val);
								
								String ind=globalhmap.get(val);
								if (ind == null){
									//System.out.println("Pageid:" + page.getId()
									//		+ " Not Found for:" + val);
								}else {
									//System.out.println(val+" found id:" + ind);
									links.add(ind);
								}
							}
							j = i + 1;
							break;
						}
					}
				}
			}

			File file = new File(folder+"/forward");
			FileWriter fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(page.getId() + ":"+links.size());
			bw.newLine();
			bw.close();
			fw.close();
			HashMap<String, String> hmap = new HashMap<String, String>();
			// System.out.println(page.getId());
			// System.out.println(links);
			for (String s : links) {
				String tok[]=s.split(",");
				for(String str:tok){
					hmap.put(str, page.getId().toString());
					//System.out.println(str+"---"+page.getId().toString());
				}
			}
			
			
			return hmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String fetchIndex(String val) {
		try {
			// System.out.println("Searching for:" + val);
			File file = new File("./searchid/tersearch");
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
			file = new File("./searchid/secsearchid" + i);
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
		}finally{
			
		}
		return null;
	}
}
