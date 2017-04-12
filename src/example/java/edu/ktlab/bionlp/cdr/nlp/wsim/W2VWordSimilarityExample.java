package edu.ktlab.bionlp.cdr.nlp.wsim;

public class W2VWordSimilarityExample {

	public static void main(String[] args) throws Exception {
		WordSimilarity sim = new W2VWordSimilarity("data/embedding/glove.txt");
		System.out.println(sim.score("asystole", "systole"));
		System.out.println(sim.score("asystole", "asystoles"));
	}

}
