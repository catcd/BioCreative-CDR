package edu.ktlab.bionlp.cdr.base;

import java.util.HashSet;
import java.util.Set;

import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.base.Token;

public class CDRIntersectionStatExample {

	public static void main(String[] args) {
		CollectionFactory factory = new CollectionFactory(false);

		Collection colTrain = factory.loadFile("data/cdr/cdr_train/CDR_TrainingSet.txt");
		Collection colDev = factory.loadFile("data/cdr/cdr_dev/CDR_DevelopmentSet.txt");

		Set<String> uniqueTokens = new HashSet<String>();
		int interToken = 0;

		for (Sentence sent : colTrain.getSentences()) {
			for (Token token : sent.getTokens()) {
				uniqueTokens.add(token.getContent().toLowerCase());
			}
		}

		for (Sentence sent : colDev.getSentences()) {
			for (Token token : sent.getTokens()) {
				if (!uniqueTokens.contains(token.getContent().toLowerCase()))
					interToken++;
			}
		}

		Set<String> uniqueTrainAnns = new HashSet<String>();
		int interDevAnn = 0;

		for (Annotation ann : colTrain.getAnnotations()) {
			for (Token token : ann.getTokens())
				uniqueTrainAnns.add(token.getContent().toLowerCase());
		}

		for (Annotation ann : colDev.getAnnotations()) {
			for (Token token : ann.getTokens()) {
				if (!uniqueTrainAnns.contains(token.getContent().toLowerCase())) {
					interDevAnn++;
				}
			}
		}

		Set<String> uniqueTrainRefs = new HashSet<String>();
		int interDevRef = 0;

		for (Annotation ann : colTrain.getAnnotations()) {
			uniqueTrainRefs.add(ann.getReference());
		}

		for (Annotation ann : colDev.getAnnotations()) {
			if (!uniqueTrainRefs.contains(ann.getReference())) {
				interDevRef++;
			}
		}

		System.out.println(interToken);
		System.out.println(interDevAnn);
		System.out.println(interDevRef);
	}

}
