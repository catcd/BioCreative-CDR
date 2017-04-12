package edu.ktlab.bionlp.cdr.nlp.rel;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Sentence;

public class CDRContextMentionExample {

	public static void main(String[] args) throws Exception {
		CollectionFactory factory = new CollectionFactory(false);
		Collection colTest = factory.loadFile("data/cdr/cdr_test.txt");
		
		for (Document doc : colTest.getDocuments()) {
			System.out.println(doc.getPmid());
			Set<String> refs = doc.getRelationReferences();
			List<Sentence> sents = doc.getSentences();
			for (Sentence sent : sents) {
				for (Annotation ann : sent.getAnnotations()) {
					String[] mentionRefs = StringUtils.split(ann.getReference(), "|");
					for (String ref : mentionRefs)
						if (refs.contains(ref))
							System.out.println(ann);
				}
			}

		}
	}

}
