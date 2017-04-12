package edu.ktlab.bionlp.cdr.base;

import java.util.HashMap;
import java.util.Map;

import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.util.FileHelper;

public class DiseaseMeshExample {
	public static void main(String[] args) {
		String[] lines = FileHelper.readFileAsLines("data/ctd_disease.txt");
		Map<String, String> disease_mesh = new HashMap<String, String>();
		for (String line : lines) {
			String[] fields = line.split("\t");
			disease_mesh.put(fields[0].toLowerCase(), fields[1]);
		}
		
		CollectionFactory factory = new CollectionFactory(false);
		Collection col = factory.loadFile("data/cdr/cdr_train/cdr_train.txt");
		int total = 0;
		int count = 0;
		for (Annotation ann : col.getAnnotations()) {
			if (ann.getType().equals("Disease")) {
				if (disease_mesh.containsKey(ann.getContent().toLowerCase())) {
					if (disease_mesh.get(ann.getContent().toLowerCase()).equals(ann.getReference()))
						count++;
				} else if (!ann.getReference().equals("-1"))
					if (!ann.getContent().matches("[A-Z]+") && !ann.getReference().contains("|"))
						System.out.println(ann);

			}
		}
		System.out.println(count);
		System.out.println(total);
	}

}
