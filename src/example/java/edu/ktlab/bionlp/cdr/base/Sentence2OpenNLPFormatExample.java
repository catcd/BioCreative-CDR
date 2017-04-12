package edu.ktlab.bionlp.cdr.base;

import java.util.List;

import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Sentence;

public class Sentence2OpenNLPFormatExample {

	public static void main(String[] args) {
		CollectionFactory factory = new CollectionFactory(false);
		Collection col = factory.loadFile("data/cdr/cdr_train/cdr_train.txt");
		for (Document doc : col.getDocuments()) {
			List<Sentence> sents = doc.getSentences();
			for (Sentence sent : sents) {
				// System.out.println(sent.getContent());
				// System.out.println(sent.getContentTokenized());
				System.out.println(sent.getTokenized2OpenNLPFormat());
				// System.out.println();
			}
		}

	}

}
