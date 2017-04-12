package edu.ktlab.bionlp.cdr.nlp.wsim;

import opennlp.tools.tokenize.SimpleTokenizer;

public class WeightCosinePhraseSimilarityExample {

	public static void main(String[] args) {
		PhraseSimilarity sim = new WeightCosinePhraseSimilarity(new CosineWordSimilarity(), true);
		String concept1 = "alpha-methyldopa";
		String concept2 = "alpha Methyldopa";
		String[] tokens1 = SimpleTokenizer.INSTANCE.tokenize(concept1.toLowerCase());
		String[] tokens2 = SimpleTokenizer.INSTANCE.tokenize(concept2.toLowerCase());
		System.out.println(sim.score(tokens1, tokens2));
		
	}

}
