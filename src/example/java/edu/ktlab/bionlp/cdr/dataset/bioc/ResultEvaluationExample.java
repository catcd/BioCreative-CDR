package edu.ktlab.bionlp.cdr.dataset.bioc;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bioc.BioCAnnotation;
import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.BioCPassage;
import bioc.io.BioCCollectionReader;
import bioc.io.BioCFactory;

public class ResultEvaluationExample {
	static String predictFile = "data/cdr/cdr_predict.xml";
	static String goldenFile = "data/cdr/cdr_training/CDR_TrainingSet.xml";

	public static void main(String[] args) throws Exception {
		List<CdrAnnotation> annsPrd = getAnnotations(predictFile);
		List<CdrAnnotation> annsGold = getAnnotations(goldenFile);
		List<CdrAnnotation> truePositive = new ArrayList<CdrAnnotation>();

		for (CdrAnnotation annPrd : annsPrd) {
			String labelPrd = "";
			if (annPrd.getInfon("Disease") != null)
				labelPrd = annPrd.getInfon("Disease");
			else if (annPrd.getInfon("Chemical") != null)
				labelPrd = annPrd.getInfon("Chemical");

			for (CdrAnnotation annGold : annsGold) {
				String labelGold = "";
				if (annGold.getInfon("MESH") != null)
					labelGold = annGold.getInfon("MESH");
				else if (annGold.getInfon("CHEBI") != null)
					labelGold = "CHEBI:" + annGold.getInfon("CHEBI");

				if (annPrd.equals(annGold) && labelGold.equals(labelPrd)) {
					// System.out.println(annGold.getPmid() + "\t" + annGold.getType() + "\t" +
					// annGold.getText().get() + "\t"
					// + labelGold + "\t" + labelPrd);
					truePositive.add(annGold);
				}

			}
		}

		for (CdrAnnotation annGold : annsGold) {
			if (!truePositive.contains(annGold)) {
				String label = "";
				if (annGold.getInfon("MESH") != null)
					label = annGold.getInfon("MESH");
				else if (annGold.getInfon("CHEBI") != null)
					label = "CHEBI:" + annGold.getInfon("CHEBI");
				System.out.println(annGold.getPmid() + "\t" + annGold.getType() + "\t"
						+ annGold.getText() + "\t" + label);
				label = "";
				if (annGold.getInfon("Disease") != null)
					label = annGold.getInfon("Disease");
				else if (annGold.getInfon("Chemical") != null)
					label = annGold.getInfon("Chemical");
				System.out.println(annGold.getPmid() + "\t" + annGold.getType() + "\t"
						+ annGold.getText() + "\t" + label);

			}
		}

	}

	public static List<CdrAnnotation> getAnnotations(String file) throws Exception {
		List<CdrAnnotation> anns = new ArrayList<CdrAnnotation>();

		BioCFactory factory = BioCFactory.newFactory(BioCFactory.WOODSTOX);
		BioCCollectionReader reader = factory.createBioCCollectionReader(new FileReader(file));
		BioCCollection collection = reader.readCollection();

		Set<String> pmids = new HashSet<String>();
		for (BioCDocument doc : collection.getDocuments()) {
			if (!pmids.add(doc.getID())) {
				continue;
			}

			List<BioCPassage> passages = doc.getPassages();
			for (BioCPassage passage : passages) {
				Collection<BioCAnnotation> bioAnns = passage.getAnnotations();
				for (BioCAnnotation ann : bioAnns) {
					if (ann.getInfon("type").equals("Chemical")
							|| ann.getInfon("type").equals("Disease"))
						anns.add(new CdrAnnotation(doc.getID(), ann));
				}
			}
		}
		reader.close();
		return anns;
	}

}