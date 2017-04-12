package edu.ktlab.bionlp.cdr.dataset.bioc;

import java.io.FileReader;
import java.util.Collection;
import java.util.List;

import bioc.BioCAnnotation;
import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.BioCPassage;
import bioc.io.BioCCollectionReader;
import bioc.io.BioCFactory;

public class BioCCollectionLoadingExample {
	static String file = "data/cdr/cdr_training/CDR_TrainingSet.xml";

	public static void main(String[] args) throws Exception {
		BioCFactory factory = BioCFactory.newFactory(BioCFactory.WOODSTOX);
		BioCCollectionReader reader = factory.createBioCCollectionReader(new FileReader(file));
		BioCCollection collection = reader.readCollection();
		
		for (BioCDocument doc : collection.getDocuments()) {
			List<BioCPassage> passages = doc.getPassages();
			for (BioCPassage passage : passages) {
				Collection<BioCAnnotation> anns = passage.getAnnotations();
				for (BioCAnnotation ann : anns) {
					if (ann.getInfon("type").equals("Chemical")
							|| ann.getInfon("type").equals("Disease"))
						System.out.println(doc.getID() + "\t" + ann.getInfon("type"));					
				}
			}
			Collection<BioCAnnotation> anns = doc.getAnnotations();
			for (BioCAnnotation ann : anns)
				System.out.println(ann.getInfons());
		}
		reader.close();
	}

}
