package edu.ktlab.bionlp.cdr.nlp.nen;

import java.util.Set;

import opennlp.tools.tokenize.SimpleTokenizer;
import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.util.ExtractAbbreviation.AbbreviationPair;

public class MentionNormalizationExample {

	public static void main(String[] args) throws Exception {
		MentionNormalization normalizer = new MentionNormalization("data/cdr/cdr_train/cdr_train.txt",
				"models/nen/mesh2015.gzip");
		CollectionFactory factory = new CollectionFactory(false);
		Collection colTest = factory.loadFile("data/cdr/cdr_dev/cdr_dev.txt");

		for (Document doc : colTest.getDocuments()) {
			Set<AbbreviationPair> abbrs = doc.getAbbreviations();
			for (Annotation ann : doc.getAnnotations()) {
				String mention = ann.getContent();
				for (AbbreviationPair abbr : abbrs) {
					if (abbr.getShortForm().equals(ann.getContent()))
						mention = abbr.getLongForm();
				}

				String[] tokens = SimpleTokenizer.INSTANCE.tokenize(mention.toLowerCase());

				String meshId = normalizer.normalize(mention, tokens);
				System.out.println(ann.getContent() + "\t" + meshId + "\t" + ann.getReference() + "\t" + ann.getReference().contains(meshId));
			}
		}
	}

}
