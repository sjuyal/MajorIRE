package com.ire.basic;

public class Page {
	private String title;
	private Integer id;
	private Revision revision;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Revision getRevision() {
		return revision;
	}
	public void setRevision(Revision revision) {
		this.revision = revision;
	}
	@Override
	public String toString() {
		String data="Title:"+this.title+"\nID:"+this.id;
		data+="\n**Revision**\n\tRev ID:"+this.revision.getId()+"\n\tRev Timestamp:"+this.revision.getTimestamp()+"\n";
		data+="\n\t--Contributor--\n\t\tCont Username:"+this.revision.getContributor().getUsername()+"\n";
		data+="\t\tCont ID:"+this.revision.getContributor().getId()+"\n";
		data+="\tMinor:"+this.revision.getMinor()+"\n";
		data+="\tComment:"+this.revision.getComment()+"\n";
		data+="\tText:"+this.revision.getText()+"\n";
		return data;
	}
	
	
}
