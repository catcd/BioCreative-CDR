package edu.ktlab.bionlp.cdr.dataset.bioc;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import bioc.BioCAnnotation;
import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.BioCPassage;
import bioc.io.BioCCollectionReader;
import bioc.io.BioCFactory;

public class TrainingStatisticalExample {
	static String file = "data/cdr/cdr_training/CDR_TrainingSet.xml";

	public static void main(String[] args) throws Exception {
		BioCFactory factory = BioCFactory.newFactory(BioCFactory.WOODSTOX);

		BioCCollectionReader reader = factory.createBioCCollectionReader(new FileReader(file));
		BioCCollection collection = reader.readCollection();

		ArrayList<String> uniques = new ArrayList<String>();

		for (BioCDocument doc : collection.getDocuments()) {
			List<BioCPassage> passages = doc.getPassages();
			for (BioCPassage passage : passages) {
				List<BioCAnnotation> anns = passage.getAnnotations();
				for (BioCAnnotation ann : anns) {
					if (ann.getInfon("type").equals("Chemical")) {
						// System.out.println(doc.getID() + "\t" + ann.getLocations() + "\t" +
						// ann.getText() + "\t" + ann.getInfon("MESH"));
						if (ann.getInfon("CHEBI") != null)
							uniques.add(ann.getInfon("CHEBI").toLowerCase());
					}
				}
			}
		}
		reader.close();

		for (String unique : uniques)
			System.out.println(unique);
	}

}
