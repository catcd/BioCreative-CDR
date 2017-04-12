package edu.ktlab.bionlp.cdr.nlp.wsim;

public interface PhraseSimilarity {
	public float score(String[] tokens1, String[] tokens2);
}
