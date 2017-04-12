package edu.ktlab.bionlp.cdr.base;

import java.util.HashSet;
import java.util.Set;

import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Sentence;

public class CDRStatExample {

	public static void main(String[] args) {
		CollectionFactory factory = new CollectionFactory(false);
		Collection col = factory.loadFile("data/cdr/cdr_test/cdr_test.txt");

		System.out.println(col.getSentences().size());
		System.out.println(col.getAnnotations().size());
		System.out.println(col.getRelations().size());
		System.out.println(col.getRelationCandidates().size());

		int tokensDis = 0;
		int countDis = 0;
		int countNotMesh = 0;
		int tokens = 0;
		Set<String> uniqueDis = new HashSet<String>();
		for (Annotation ann : col.getAnnotations()) {
			if (ann.getType().equals("Disease")) {
				uniqueDis.add(ann.getContent().toLowerCase());
				tokensDis += ann.getTokens().size();
				countDis++;
				if (ann.getReference().equals("-1"))
					countNotMesh++;
			}
		}
		System.out.println(tokensDis + "/" + countDis + "=" + (double) tokensDis / countDis);
		System.out.println(uniqueDis.size());
		System.out.println(countNotMesh);

		for (Sentence sent : col.getSentences()) {
			tokens += sent.getTokens().size();
		}
		System.out.println(tokens);
	}

}
