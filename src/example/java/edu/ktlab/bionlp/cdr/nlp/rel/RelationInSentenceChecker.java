package edu.ktlab.bionlp.cdr.nlp.rel;

import java.util.List;

import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Relation;
import edu.ktlab.bionlp.cdr.base.Sentence;

public class RelationInSentenceChecker {
	public static void main(String... strings) {
		CollectionFactory factory = new CollectionFactory(false);
		Collection col = factory.loadFile("data/cdr/cdr_test/cdr_test.txt");
		// Collection col = CollectionLoader.loadFromFile("data/cdr/cdr_train/cdr_train.txt");
		int countNotInSent = 0;
		for (Document doc : col.getDocuments()) {
			System.out.println(doc.getPassages().get(0).getSentences().size());
			System.out.println(doc.getPmid());
			int countChecks = 0;
			for (Relation rel : doc.getRelations()) {				
				boolean check = false;
				for (Sentence sent : doc.getSentences()) {
					if (sent.containRelation(rel)) {
						countChecks++;
						check = true;
						break;
					}
				}
				if (!check) {
					System.out.println(rel);
					List<Annotation> annsTitle = doc.getPassages().get(0).getAnnotations();

					for (Sentence sent : doc.getSentences()) {
						boolean titleContain = false;
						boolean sentenceContain = false;
						for (Annotation ann : sent.getAnnotations()) {
							if (ann.getReference().equals(rel.getChemicalID())) {
								sentenceContain = true;
								for (Annotation annTitle : annsTitle) {
									if (annTitle.getReference().equals(rel.getDiseaseID())) {
										titleContain = true;
										break;
									}									
								}
							}

							if (ann.getReference().equals(rel.getDiseaseID())) {
								sentenceContain = true;
								for (Annotation annTitle : annsTitle) {
									if (annTitle.getReference().equals(rel.getChemicalID())) {
										titleContain = true;
										break;
									}									
								}
							}
							if (titleContain)
								break;
						}
						if (sentenceContain) {
							if (titleContain)
								System.out.println("Title contain absence component");
							else {
								System.out.println("Title not contain");
							}							
						}
					}
					countNotInSent++;
				}

			}
			if (countChecks == doc.getRelations().size())
				System.out.println("Relation in sentence");	
			System.out.println("===========");
			
		}

		System.out.println(countNotInSent + "/" + col.getRelations().size() + "=" + (double) countNotInSent
				/ col.getRelations().size());
	}

}