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

public class RelationExtractorExample3 {
	static String fileCorpus = "data/cdr_test/cdr_test.txt";
	static String fileCorpus2 = "data/cdr_test/cdr_test.gzip";

	public static void main(String[] args) throws Exception {
		Collection col = CollectionFactory.loadFile(fileCorpus);

		System.out.println(col.getDocuments().size());
		CollectionFactory.saveJsonFile(fileCorpus2, col);
	}
}
