package edu.ktlab.bionlp.cdr.base;

import java.util.List;

import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;

public class Document2OpenNLPFormatExample {

	public static void main(String[] args) {
		CollectionFactory factory = new CollectionFactory(false);
		Collection col = factory.loadFile("data/cdr/cdr_train/cdr_train.txt");
		for (Document doc : col.getDocuments()) {
			List<Annotation> anns = doc.getAnnotations();
			String taggedText = doc.getContent();
			for (int i = anns.size() - 1; i >= 0; i--) {
				taggedText = taggedText.substring(0, anns.get(i).getStartBaseOffset())
						+ " <START:"
						+ anns.get(i).getType()
						+ "> "
						+ taggedText.substring(anns.get(i).getStartBaseOffset(), anns.get(i)
								.getEndBaseOffset()) + " <END> "
						+ taggedText.substring(anns.get(i).getEndBaseOffset(), taggedText.length());
			}
			System.out.println(taggedText);
		}
	}
}
