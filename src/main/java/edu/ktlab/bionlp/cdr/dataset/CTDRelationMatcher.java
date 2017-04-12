package edu.ktlab.bionlp.cdr.dataset;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jersey.repackaged.com.google.common.collect.Lists;
import edu.ktlab.bionlp.cdr.base.Relation;
import edu.ktlab.bionlp.cdr.util.FileHelper;

public class CTDRelationMatcher {
	private Set<Relation> relations = new HashSet<Relation>();

	public CTDRelationMatcher(String file) {
		loadRelations(file);
	}

	private void loadRelations(String file) {
		String[] lines = FileHelper.readFileAsLines(file);
		for (String line : lines) {
			String[] refs = line.split(" ");
			Relation rel = new Relation("CID", refs[0], refs[1]);
			relations.add(rel);
		}
	}

	public List<Relation> find(Relation rel) {
		List<Relation> rels = Lists.newArrayList();
		for (Relation relation : relations)
			if (rel.getChemicalID().contains(relation.getChemicalID())
					&& rel.getDiseaseID().contains(relation.getDiseaseID()))
				rels.add(relation);
		return rels;
	}
	
	public boolean match(Relation rel) {
		return relations.contains(rel);
	}

}
