package edu.ktlab.bionlp.cdr.nlp.rel;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Set;

import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Relation;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.base.Token;
import edu.ktlab.bionlp.cdr.util.DependencyHelper;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.Pair;
import jersey.repackaged.com.google.common.collect.Lists;

public class SentExtractorExample {
	static String fileCorpus = "data/cdr_full/cdr_full.gzip";

	public static void main(String[] args) throws Exception {
		Collection col = CollectionFactory.loadJsonFile(fileCorpus);

		File []fs = {
	             new File("/home/catcan/Desktop/data/cdr.sent")
		};
		
		for (File f : fs) {
			if (f.exists()) {
				f.createNewFile();
			}
		}
		
		FileWriter parsed = new FileWriter(fs[0]);

		for (Document doc : col.getDocuments()) {
			Set<Relation> candidateRels = doc.getRelationCandidates();
			List<Sentence> sents = doc.getSentences();
			for (Sentence sent : sents) {
				for (Relation candidateRel : candidateRels) {
					if (sent.containRelation(candidateRel)) {
						parsed.write(sent.getContent() + "\n");
						break;
					}
				}
			}
		}
		parsed.close();
	}
}
