package edu.ktlab.bionlp.cdr.nlp.wsim;

public class CosineWordSimilarity implements WordSimilarity {
	public float score(String word1, String word2) {
		if (word1.equals(word2))
			return 1;
		return 0;
	}

}
