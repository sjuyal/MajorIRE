package com.ire.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

public class InitialHandler extends DefaultHandler {

	private Page page = null;
	private Revision revision = null;
	private Contributor contributor = null;
	private final Stack<String> stackoftags = new Stack<String>();
	private int pagecount = 0;
	private int filecount = 0;
	private static int Splitsize = 25000;
	public String to = new String();

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

	public int getPagecount() {
		return pagecount;
	}

	public void setPagecount(int pagecount) {
		this.pagecount = pagecount;
	}

	StringBuilder tmpString;
	File file;
	FileWriter fw;
	BufferedWriter bw;
	HashMap<String, Integer> hmap = new HashMap<String, Integer>();

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		try {
			file = new File(to);
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			tmpString = new StringBuilder();
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		try {
			stackoftags.pop();

			if (qName.equalsIgnoreCase("page")) {
				pagecount++;
				hmap.put(page.getTitle(), page.getId());
				if (pagecount == 100000) {
					file = new File(to + filecount);
					filecount++;
					fw = new FileWriter(file);
					bw = new BufferedWriter(fw);
					TreeMap<String, Integer> tmap = new TreeMap<String, Integer>();
					tmap.putAll(hmap);
					try {
						for (Map.Entry<String, Integer> entry : tmap.entrySet()) {
							bw.write(entry.getKey() + ":" + entry.getValue());
							bw.newLine();
						}
						hmap.clear();
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
			e.printStackTrace();
		}
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
		if (pagecount != 0) {
			try {
				TreeMap<String, Integer> tmap = new TreeMap<String, Integer>();
				file = new File(to + filecount);
				filecount++;
				fw = new FileWriter(file);
				bw = new BufferedWriter(fw);
				tmap.putAll(hmap);

				for (Map.Entry<String, Integer> entry : tmap.entrySet()) {
					bw.write(entry.getKey() + ":" + entry.getValue());
					bw.newLine();
				}
				bw.close();
				fw.close();
				hmap.clear();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
