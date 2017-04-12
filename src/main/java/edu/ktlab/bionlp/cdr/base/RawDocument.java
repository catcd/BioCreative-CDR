package edu.ktlab.bionlp.cdr.base;

import java.util.HashSet;
import java.util.Set;

public class RawDocument {
	private String pmid;
	private String title;
	private String summary;
	private Set<Relation> relations;

	public RawDocument() {
		relations = new HashSet<Relation>();
	}

	public boolean addRelation(Relation relation) {
		return relations.add(relation);
	}

	public String getPmid() {
		return pmid;
	}

	public void setPmid(String pmid) {
		this.pmid = pmid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Set<Relation> getRelations() {
		return relations;
	}

	public void setRelations(Set<Relation> relations) {
		this.relations = relations;
	}
}
