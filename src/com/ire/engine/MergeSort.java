package com.ire.engine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MergeSort {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long time1 = System.currentTimeMillis();
		KWayMergeSortInteger("backward-index", 562, "backward", "/media/shashank/Windows/Wikipedia/fandbnew");
		//KWayMergeSortString("tlpid-index.txt", 141, "title-pageid.txt", "./intermediate files/");
		//char ch='z';
		//for(int i=5;i<=26;i++){
			//MakeSecondary(ch+"oldfinal", "/media/shashank/Windows/Wikipedia/earlier/"+ch+"old", ch+"old.txt", "secold_"+ch);
		//	ch++;
		//}
		
		//MakeSecondary("secsearchid", "./searchidnew", "secsearch", "tersearch");  // Change split to 100
		//MakeSecondary("searchid", "./searchidnew", "tlpid-index.txt", "secsearch");  // Change split to 1000 (First)
		
		//MakeTertiary("./searchid", "secsearch.txt", "tersearch");
		//String ret=Tokenizer.fetchIndex("! (math)");
		//System.out.println(ret);
		long time2 = System.currentTimeMillis() - time1;
		System.out.println("Time Taken in Seconds:" + time2 / 1000.0);
	}
	
	/*private static void MakeTertiary(String folder,String toread,String tertiary) {
		
		try {
			File read=new File(folder+"/"+toread);
			FileReader fr=new FileReader(read);
			BufferedReader br=new BufferedReader(fr);
			File tert= new File(folder+"/"+tertiary);
			FileWriter fwter=new FileWriter(tert);
			BufferedWriter bwter=new BufferedWriter(fwter);
			
			String line;
			int count=0,split=1000;
			while((line=br.readLine())!=null){
				
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/

	public static void MakeSecondary(String finalfileprefix,String folder,String toread,String secondary){
		try {
			File read=new File(folder+"/"+toread);
			FileReader fr=new FileReader(read);
			BufferedReader br=new BufferedReader(fr);
			int fcount=0,i=0;
			int split=1000;
			File towrite= new File(folder+"/"+finalfileprefix + fcount);
			FileWriter fw=new FileWriter(towrite);
			BufferedWriter bw=new BufferedWriter(fw);
			
			File second= new File(folder+"/"+secondary);
			FileWriter fwsec=new FileWriter(second);
			BufferedWriter bwsec=new BufferedWriter(fwsec);
			
			while(true){
				String line=br.readLine();
				if(line==null)
					break;
				i++;
				if(i==split){
					bw.close();
					fw.close();
					fcount++;
					towrite= new File(folder+"/"+finalfileprefix + fcount);
					fw=new FileWriter(towrite);
					bw=new BufferedWriter(fw);
						bw.write(line+"\n");
					bwsec.write(line+"\n");
					i=0;
				}else{
						bw.write(line+"\n");
				}
			}
			br.close();
			fr.close();
			bw.close();
			fw.close();
			bwsec.close();
			fwsec.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void KWayMergeSortString(String finalfile, int filecount,String prefix,String folder){
		
		try {
			File towrite= new File(folder+"/"+finalfile);
			FileWriter fw=new FileWriter(towrite);
			BufferedWriter bw=new BufferedWriter(fw);

			File toread[] = new File[filecount];
			for (int i = 0; i < filecount; i++) {
				toread[i] = new File(folder+"/"+prefix + i);
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
			
			while(flag==1){
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
				
				if (smallest.equals("|")) {
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
					if (smallest.equals(words[i]) && index != i) {
						String res[] = line[i].split(":");
						sb.append(","+res[1]);
						line[i] = br[i].readLine();
						if (line[i] != null) {
							res = line[i].split(":");
							words[i] = res[0];
						} else {
							words[i] = null;
						}
					}
				}
				
				bw.write(sbhead.toString()+":"+sb.toString());
				bw.newLine();
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
			bw.close();
			fw.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void KWayMergeSortInteger(String finalfile, int filecount,String prefix,String folder){
		
		try {
			File towrite= new File(folder+"/"+finalfile);
			FileWriter fw=new FileWriter(towrite);
			BufferedWriter bw=new BufferedWriter(fw);

			File toread[] = new File[filecount];
			for (int i = 0; i < filecount; i++) {
				toread[i] = new File(folder+"/"+prefix + i);
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
			int words[] = new int[filecount];
			for (int i = 0; i < filecount; i++) {
				line[i] = br[i].readLine();
				if (line[i] != null) {
					flag = 1;
					String ar[] = line[i].split(":");
					words[i] = Integer.parseInt(ar[0]);
				} else {
					words[i] = -1;
				}
			}
			while(flag==1){
				int smallest = 99999999;
				int index = 0;
				for (int i = 0; i < filecount; i++) {
					if (words[i] == -1)
						continue;
					if (words[i]<smallest){
						smallest = words[i];
						index = i;
					}
				}
				
				if (smallest==99999999) {
					flag = 0;
					System.out.println("Yikes!!");
					continue;
				}
				
				StringBuilder sbhead = new StringBuilder();
				
				String rest[] = line[index].split(":");
				sbhead.append(smallest);

				StringBuilder sb = new StringBuilder();
				sb.append(rest[1]);

				for (int i = 0; i < filecount; i++) {
					if (words[i] == -1)
						continue;
					if (smallest==words[i] && index != i) {
						String res[] = line[i].split(":");
						sb.append(","+res[1]);
						line[i] = br[i].readLine();
						if (line[i] != null) {
							res = line[i].split(":");
							words[i] = Integer.parseInt(res[0]);
						} else {
							words[i] = -1;
						}
					}
				}
				
				bw.write(sbhead.toString()+":"+sb.toString());
				bw.newLine();
				line[index] = br[index].readLine();
				if (line[index] != null) {
					rest = line[index].split(":");
					words[index] = Integer.parseInt(rest[0]);
				} else {
					words[index] = -1;
				}
			}
			
			for (int i = 0; i < filecount; i++) {
				br[i].close();
				fr[i].close();
			}
			bw.close();
			fw.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
