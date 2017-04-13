package edu.ktlab.bionlp.cdr.nlp.rel;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.List;

import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;

public class DictExtractorExample {
	static String fileCorpus = "data/cdr_full/cdr_full.gzip";

	public static void main(String[] args) throws Exception {
		Collection col = CollectionFactory.loadJsonFile(fileCorpus);

		File []fs = {
	             new File("/home/catcan/Desktop/data/chemical"),
	             new File("/home/catcan/Desktop/data/disease")
		};
		
		for (File f : fs) {
			if (f.exists()) {
				f.createNewFile();
			}
		}

		FileWriter che = new FileWriter(fs[0]);
		FileWriter dis = new FileWriter(fs[1]);

		HashSet<String> ches = new HashSet<String>();
		HashSet<String> diss = new HashSet<String>();

		for (Document doc : col.getDocuments()) {
			List<Annotation> anns = doc.getAnnotations();
			
			for (Annotation ann : anns) {
				if (ann.getType().equals("Chemical"))
					ches.add(ann.getContent());
				if (ann.getType().equals("Disease"))
					diss.add(ann.getContent());
			}
		}
		
		for (String s : ches) {
			che.write(s + "\n");
		}
		for (String s : diss) {
			dis.write(s + "\n");
		}
		
		che.close();
		dis.close();
	}
}
