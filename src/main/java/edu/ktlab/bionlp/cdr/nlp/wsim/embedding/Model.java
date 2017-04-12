package edu.ktlab.bionlp.cdr.nlp.wsim.embedding;

import java.util.HashMap;

public abstract class Model {
	protected HashMap<String, float[]> wordVectors = new HashMap<String, float[]>();
	protected int words;
	protected int size;

	public float[] getWordVector(String word) {
		return wordVectors.get(word);
	}

	public HashMap<String, float[]> getWordVectors() {
		return wordVectors;
	}

	public int getWords() {
		return words;
	}

	public int getSize() {
		return size;
	}
}
