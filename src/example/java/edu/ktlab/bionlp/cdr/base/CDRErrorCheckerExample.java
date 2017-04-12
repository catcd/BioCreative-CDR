package edu.ktlab.bionlp.cdr.base;

import java.util.List;

import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;

public class CDRErrorCheckerExample {

	public static void main(String[] args) {
		CollectionFactory factory = new CollectionFactory(false);
		Collection col = factory.loadFile("data/cdr/cdr_train/cdr_train.txt");
		for (Document doc : col.getDocuments()) {
			List<Annotation> anns = doc.getAnnotations();
			for (Annotation ann1 : anns)
				for (Annotation ann2 : anns)
					if (ann1.inner(ann2) && !ann1.equals(ann2))
						System.out.println(doc.getPmid() + "\t" + ann1 + "\t" + ann2);
		}
	}

}
