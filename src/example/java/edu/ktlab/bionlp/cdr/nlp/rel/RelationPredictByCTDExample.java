package edu.ktlab.bionlp.cdr.nlp.rel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jersey.repackaged.com.google.common.collect.Maps;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Relation;
import edu.ktlab.bionlp.cdr.util.FileHelper;

public class RelationPredictByCTDExample {

	public static void main(String[] args) {
		String[] lines = FileHelper.readFileAsLines("data/ctd_relations_m_pmid.txt");
		Map<String, List<String>> ctdMap = Maps.newHashMap();
		for (String line : lines) {
			String[] fi = line.split("\t");
			String[] ids = fi[1].split("\\|");
			ctdMap.put(fi[0], Arrays.asList(ids));
		}

		List<String> truePositive = new ArrayList<String>();
		int countPredict = 0;
		
		CollectionFactory factory = new CollectionFactory(false);
		Collection col = factory.loadFile("data/cdr/cdr_train/CDR_TrainingSet.txt");
		// Collection col = new Collection("data/cdr/cdr_dev/CDR_DevelopmentSet.txt", false);

		for (Document doc : col.getDocuments()) {
			System.out.println(doc.getPmid());
			List<String> predictRelations = new ArrayList<String>();

			for (String rel : ctdMap.keySet()) {
				if (ctdMap.get(rel).contains(doc.getPmid())) {
					predictRelations.add(rel);
					countPredict++;
					System.out.println(rel);
				}
			}

			for (Relation relGold : doc.getRelations()) {
				boolean falseNegative = false;
				for (String relPrd : predictRelations)
					if (relGold.equals(relPrd)) {
						truePositive.add(relPrd);
						System.out.println("True postive: " + relPrd);
						falseNegative = true;
						break;
					}
				if (!falseNegative)
					System.out.println("False negative/Missing: " + relGold);
			}

			for (String relPrd : predictRelations)
				if (!truePositive.contains(relPrd))
					System.out.println("False positive/Error: " + relPrd);
			System.out.println("======================");

		}
		System.out.println("===========================");
		double precision = (double) truePositive.size() / countPredict;
		double recall = (double) truePositive.size() / col.getRelations().size();
		double fscore = 2 * precision * recall / (precision + recall);

		System.out.println("P=" + truePositive.size() + "/" + countPredict + "=" + precision);
		System.out.println("R=" + truePositive.size() + "/" + col.getRelations().size() + "=" + recall);
		System.out.println("F=" + fscore);
	}

}
