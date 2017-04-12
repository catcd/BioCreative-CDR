package edu.ktlab.bionlp.cdr.nlp.wsim.embedding;

public class W2VModelExample extends Model {
	public static void main(String...strings) throws Exception {
		W2VModel w2vmodel = new W2VModel("E:/Embedding/PubMed-w2v.bin");
		System.out.println(w2vmodel.getWords());
	}
}
