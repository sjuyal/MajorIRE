package com.ire.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ire.basic.Contributor;
import com.ire.basic.Page;
import com.ire.basic.Revision;
import com.ire.engine.Tokenizer;

public class SJHandler extends DefaultHandler {

	private Page page = null;
	private Revision revision = null;
	private Contributor contributor = null;
	private final Stack<String> stackoftags = new Stack<String>();
	public static TreeMap<String, ArrayList<String>> globalmap = new TreeMap<String, ArrayList<String>>();
	public static TreeMap<String, String> globaltreemap = new TreeMap<String, String>();
	private int pagecount = 0;
	private int filecount=0;
	private boolean first = true;
	private String finalfile = null;
	private static int Splitsize = 25000;
	public String to = new String();
	public String folder=new String();
	private int totalpages=0;

	
	
	public static int getSplitsize() {
		return Splitsize;
	}

	public static void setSplitsize(int splitsize) {
		Splitsize = splitsize;
	}

	public int getFilecount() {
		return filecount;
	}

	public void setFilecount(int filecount) {
		this.filecount = filecount;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public int getPagecount() {
		return pagecount;
	}

	public void setPagecount(int pagecount) {
		this.pagecount = pagecount;
	}

	public String getFinalfile() {
		return finalfile;
	}

	public void setFinalfile(String finalfile) {
		this.finalfile = finalfile;
	}

	public static TreeMap<String, ArrayList<String>> getGlobalmap() {
		return globalmap;
	}

	public static void setGlobalmap(TreeMap<String, ArrayList<String>> globalmap) {
		SJHandler.globalmap = globalmap;
	}

	public static TreeMap<String, String> getGlobaltreemap() {
		return globaltreemap;
	}

	public static void setGlobaltreemap(TreeMap<String, String> globaltreemap) {
		SJHandler.globaltreemap = globaltreemap;
	}

	StringBuilder tmpString;

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		try {
			tmpString = new StringBuilder();
		} catch (Exception e) {
			
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// System.out.println("\n"+tmpString);
		try {
			tmpString.setLength(0);
			stackoftags.push(qName);
			if (qName.equalsIgnoreCase("page")) {
				page = new Page();
			} else if (qName.equalsIgnoreCase("revision")) {
				revision = new Revision();
			} else if (qName.equalsIgnoreCase("contributor")) {
				contributor = new Contributor();
			}
		} catch (Exception e) {
			
		}
	}

	HashMap<Integer, String> globalhmap=new HashMap<Integer, String>();
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		try {
			stackoftags.pop();

			if (qName.equalsIgnoreCase("page")) {
				// System.out.println("Indexing Page:"+page.getId());

				//Tokenizer.forward(to,page);
				totalpages++;
				pagecount++;
				HashMap<String,String> localhmap = Tokenizer.backward(folder,page);
				
				for (Map.Entry<String, String> entry : localhmap.entrySet()) {
					if((entry.getKey()).equals(""))
						continue;
					if(globalhmap.containsKey(entry.getKey())){
						String val=globalhmap.get(entry.getKey());
						globalhmap.put(Integer.parseInt(entry.getKey()),val+","+entry.getValue());
					}else{
						globalhmap.put(Integer.parseInt(entry.getKey()), entry.getValue());
					}
				}
				
				if (pagecount == Splitsize) {
					System.out.println("Done till pageid:"+page.getId());
					File file = new File(folder+"/"+to + filecount);
					filecount++;
					FileWriter fw = new FileWriter(file);
					BufferedWriter bw = new BufferedWriter(fw);
					TreeMap<Integer, String> tmap = new TreeMap<Integer, String>();
					tmap.putAll(globalhmap);
					globalhmap.clear();
					try {
						for (Map.Entry<Integer, String> entry : tmap.entrySet()) {
							bw.write(entry.getKey() + ":" + entry.getValue());
							bw.newLine();
						}
						bw.close();
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					pagecount = 0;
				}
				
			} else if (qName.equalsIgnoreCase("contributor")) {
				revision.setContributor(contributor);
			} else if (qName.equalsIgnoreCase("revision")) {
				page.setRevision(revision);
			} else if (qName.equalsIgnoreCase("id")) {
				String parent = stackoftags.peek();
				int tid = Integer.valueOf(tmpString.toString().trim());
				if (parent.equalsIgnoreCase("page")) {
					page.setId(tid);
				} else if (parent.equalsIgnoreCase("revision")) {
					revision.setId(tid);
				} else if (parent.equalsIgnoreCase("contributor")) {
					contributor.setId(tid);
				}
			} else if (qName.equalsIgnoreCase("text")) {
				revision.setText(tmpString.toString());
			} else if (qName.equalsIgnoreCase("title")) {
				page.setTitle(tmpString.toString());
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		try {
			tmpString.append(new String(ch, start, length));
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
		if (pagecount != 0) {
			try {
				TreeMap<Integer, String> tmap = new TreeMap<Integer, String>();
				File file = new File(folder+"/"+to + filecount);
				filecount++;
				FileWriter fw = new FileWriter(file);
				BufferedWriter bw = new BufferedWriter(fw);
				tmap.putAll(globalhmap);

				for (Map.Entry<Integer, String> entry : tmap.entrySet()) {
					bw.write(entry.getKey() + ":" + entry.getValue());
					bw.newLine();
				}
				bw.close();
				fw.close();
				globalhmap.clear();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
