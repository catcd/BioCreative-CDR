package edu.ktlab.bionlp.cdr.base;

import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;

public class CDRLoaderExample {

	public static void main(String[] args) throws Exception {
		CollectionFactory factory = new CollectionFactory(true);

		Collection col = factory.loadFile("data/cdr/cdr_train/cdr_train.txt");
		CollectionFactory.saveJsonFile("data/cdr/cdr_train/cdr_train.gzip", col);

		col = factory.loadFile("data/cdr/cdr_dev/cdr_dev.txt");
		CollectionFactory.saveJsonFile("data/cdr/cdr_dev/cdr_dev.gzip", col);

		col = factory.loadFile("data/cdr/cdr_full/cdr_full.txt");
		CollectionFactory.saveJsonFile("data/cdr/cdr_full/cdr_full.gzip", col);

		// Collection col = CollectionFactory.loadFile("data/cdr/cdr_test.txt");
		col = CollectionFactory.loadJsonFile("data/cdr/cdr_train/cdr_train.gzip");
		System.out.println(col.getDocuments().size());
	}

}
