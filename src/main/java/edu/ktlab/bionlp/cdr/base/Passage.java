package edu.ktlab.bionlp.cdr.base;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

public class Passage {
	private int startOffset;
	private int endOffset;
	private String type;
	private String content;
	private List<Sentence> sentences;
	private List<Annotation> annotations;
	private List<Relation> relations;

	public Passage() {
		sentences = Lists.newArrayList();
		annotations = Lists.newArrayList();
		relations = Lists.newArrayList();
	}

	public Passage(String type, String content) {
		this();
		this.type = type;
		this.content = content;
	}

	public boolean addAnnotation(Annotation annotation) {
		return annotations.add(annotation);
	}

	public boolean addAnnotation(Relation relation) {
		return relations.add(relation);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Sentence> getSentences() {
		return sentences;
	}

	public void setSentences(List<Sentence> sentences) {
		this.sentences = sentences;
	}
	
	public boolean addSentence(Sentence sentence) {
		return sentences.add(sentence);
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}
	
	public Set<Relation> getRelationCandidates() {
		Set<Relation> rels = new HashSet<Relation>();
		Set<Annotation> chemicalAnnotations = new HashSet<Annotation>();
		Set<Annotation> diseaseAnnotations = new HashSet<Annotation>();
		
		for (Annotation ann : annotations) {
			if (ann.getType().equals("Chemical"))
				chemicalAnnotations.add(ann);
			if (ann.getType().equals("Disease"))
				diseaseAnnotations.add(ann);
		}
		
		for (Annotation ched : chemicalAnnotations)
			for (Annotation dis : diseaseAnnotations)
				rels.add(new Relation("CID", ched.getReference(), dis.getReference()));
		
		return rels;
	}

	@Override
	public String toString() {
		return "{" +
				"startOffset=" + startOffset +
				", endOffset=" + endOffset +
				", type='" + type + '\'' +
				", content='" + content + '\'' +
				", sentences=" + sentences +
				", annotations=" + annotations +
				", relations=" + relations +
				'}';
	}
}