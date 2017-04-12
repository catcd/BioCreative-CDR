package edu.ktlab.bionlp.cdr.nlp.rel;

import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Relation;
import edu.ktlab.bionlp.cdr.base.Sentence;

public class RelationInTitleChecker {
	public static void main(String... strings) {
		CollectionFactory factory = new CollectionFactory(false);

		// Collection col = CollectionLoader.loadFromFile("data/cdr/cdr_dev/cdr_dev.txt", false);
		Collection col = factory.loadFile("data/cdr/cdr_train/cdr_train.txt");
		
		for (Document doc : col.getDocuments()) {
			System.out.println(doc.getPmid());
			boolean containChed = false;
			boolean containDis = false;
			
			for (Sentence sent : doc.getPassages().get(0).getSentences())
				for (Annotation ann : sent.getAnnotations()) {
					if (ann.getType().equals("Chemical")) {
						containChed = true;
					}
						
					if (ann.getType().equals("Disease")) {
						containDis = true;
					}						
				}
			
			if (containChed && containDis) {
				System.out.print("contain Both");
				
				for (Relation relCandidate : doc.getPassages().get(0).getRelationCandidates()) {
					if (doc.getRelations().contains(relCandidate)) {
						System.out.print("\tcontain CID");
						break;
					}						
				}
				System.out.println();
				
				continue;
			}
				
			
			if (containChed)
				System.out.println("contain Ched");
			if (containDis)
				System.out.println("contain Dis");
		}

		
	}

}