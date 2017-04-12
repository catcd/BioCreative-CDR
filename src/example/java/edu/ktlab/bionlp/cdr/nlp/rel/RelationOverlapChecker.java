package edu.ktlab.bionlp.cdr.nlp.rel;

import java.util.Set;

import com.google.common.collect.Sets;

import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Relation;

public class RelationOverlapChecker {
	public static void main(String... strings) {
		CollectionFactory factory = new CollectionFactory(false);
		Collection colDev = factory.loadFile("data/cdr/cdr_dev/CDR_DevelopmentSet.txt");
		Collection colTrain = factory.loadFile("data/cdr/cdr_train/CDR_TrainingSet.txt");
		int countOverlap = 0;
		Set<Relation> uniqueRels = Sets.newHashSet();
		for (Relation rel : colDev.getRelations()) {
			if (colTrain.getRelations().contains(rel)) {
				countOverlap++;
				uniqueRels.add(rel);
			}				
		}
		System.out.println(countOverlap);
		System.out.println(uniqueRels.size());
	}

}