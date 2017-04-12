package edu.ktlab.bionlp.cdr.base;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Collection {
	private Map<String, Document> documents;

	public Collection() {
		documents = Maps.newHashMap();
	}

	public void addDocument(Document doc) {
		documents.put(doc.getPmid(), doc);
	}

	public java.util.Collection<Document> getDocuments() {
		return documents.values();
	}

	public Map<String, Document> getMapDocuments() {
		return documents;
	}

	public Document getDocument(String pmid) {
		return documents.get(pmid);
	}

	public void setDocuments(Map<String, Document> documents) {
		this.documents = documents;
	}

	

	public List<Sentence> getSentences() {
		List<Sentence> sents = Lists.newArrayList();
		for (Document doc : documents.values())
			sents.addAll(doc.getSentences());

		return sents;
	}
	
	public List<Annotation> getAnnotations() {
		List<Annotation> anns = Lists.newArrayList();
		for (Document doc : documents.values())
			anns.addAll(doc.getAnnotations());

		return anns;
	}

	public List<Relation> getRelations() {
		List<Relation> rels = Lists.newArrayList();
		for (Document doc : documents.values())
			rels.addAll(doc.getRelations());

		return rels;
	}

	public List<Relation> getRelationCandidates() {
		List<Relation> rels = Lists.newArrayList();
		for (Document doc : documents.values())
			rels.addAll(doc.getRelationCandidates());

		return rels;
	}

	public List<Passage> getPassages() {
		List<Passage> pass = Lists.newArrayList();
		for (Document doc : documents.values())
			pass.addAll(doc.getPassages());

		return pass;
	}
}
