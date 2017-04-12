package edu.ktlab.bionlp.cdr.nlp.wsim;

import java.util.regex.Pattern;

import edu.ktlab.bionlp.cdr.util.PorterStemmer;

public class WeightCosinePhraseSimilarity implements PhraseSimilarity {
	WordSimilarity wsim;
	PorterStemmer stemmer;
	boolean isStemmer = true;

	public WeightCosinePhraseSimilarity(WordSimilarity wsim, boolean isStemmer) {
		this.wsim = wsim;
		this.isStemmer = isStemmer;
		if (isStemmer)
			stemmer = new PorterStemmer();
	}

	public float score(String[] tokens1, String[] tokens2) {
		float score = 0f;

		for (String token1 : tokens1) {
			if (Pattern.matches("\\p{Punct}", token1))
				continue;
			for (String token2 : tokens2) {
				if (Pattern.matches("\\p{Punct}", token2))
					continue;
				if (token1.equals(token2)) {
					score += 1;
				} else if (isStemmer && stemmer.stem(token1).equals(stemmer.stem(token2))) {
					score += 0.9;
				} else {
					float s = wsim.score(token1, token2);
					score += s;
				}

			}
		}

		return 2 * score / (tokens1.length + tokens2.length);
	}
}
