package edu.ktlab.bionlp.cdr.nlp.rel;

import java.util.Set;

import com.google.common.collect.Sets;

import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Relation;
import edu.ktlab.bionlp.cdr.util.FileHelper;

public class RelationInCTDChecker {
	public static void main(String... strings) throws Exception {
		Collection col = CollectionFactory.loadJsonFile("data/cdr/cdr_train/cdr_train.gzip");
		String[] ctd_m = FileHelper.readFileAsLines("data/ctd_relations_t.txt");
		Set<String> ctd_m_relations = Sets.newHashSet(ctd_m);
		for (Document doc : col.getDocuments()) {
			for (Relation rel : doc.getRelations()) {
				if (!ctd_m_relations.contains(rel.getChemicalID() + "\t" + rel.getDiseaseID()))
					System.out.println(doc.getPmid() + "\t" + rel);
			}
		}
	}

}
