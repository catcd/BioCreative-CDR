package edu.ktlab.bionlp.cdr.nlp.rel;

import java.util.HashMap;
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
import edu.stanford.nlp.util.Pair;
import jersey.repackaged.com.google.common.collect.Lists;

public class RelationExtractorExample2 {
	static String fileCorpus = "data/cdr_full/cdr_full.gzip";
	static HashMap<String, Integer> l = new HashMap<String, Integer>();

	public static void main(String[] args) throws Exception {
		Collection col = CollectionFactory.loadJsonFile(fileCorpus);

		for (Document doc : col.getDocuments()) {
			Set<Relation> candidateRels = doc.getRelationCandidates();
			List<Sentence> sents = doc.getSentences();
			for (Sentence sent : sents) {
				SemanticGraph semgraph = DependencyHelper.convertSemanticGraph(sent.getDeptree());
				for (Relation candidateRel : candidateRels) {
					if (sent.containRelation(candidateRel)) {
						List<Annotation> annsChed = sent.getAnnotation(candidateRel.getChemicalID());
						List<Annotation> annsDis = sent.getAnnotation(candidateRel.getDiseaseID());

						List<Pair<Annotation, Annotation>> pairs = Lists.newArrayList();
						for (Annotation annChed : annsChed)
							for (Annotation annDis : annsDis) {
								pairs.add(new Pair<Annotation, Annotation>(annChed, annDis));
							}

						for (Pair<Annotation, Annotation> pair : pairs) {
							Annotation annChed = pair.first;
							Annotation annDis = pair.second;
							List<Token> tokensChed = annChed.getTokens();
							List<Token> tokensDis = annDis.getTokens();

							for (Token tokenChed : tokensChed) {
								IndexedWord idxTokenChed = semgraph.getNodeByIndexSafe(tokenChed.getId() + 1);
								if (idxTokenChed == null)
									continue;
								
								Integer old = l.get(idxTokenChed.tag());
								if (old == null) {
									l.put(idxTokenChed.tag(), 1);
								} else {
									l.put(idxTokenChed.tag(), old + 1);
								}
							}

							for (Token tokenDis : tokensDis) {
								IndexedWord idxTokenDis = semgraph.getNodeByIndexSafe(tokenDis.getId() + 1);
								if (idxTokenDis == null)
									continue;
								
								Integer old = l.get(idxTokenDis.tag());
								if (old == null) {
									l.put(idxTokenDis.tag(), 1);
								} else {
									l.put(idxTokenDis.tag(), old + 1);
								}
							}
						}
					}
				}
			}
		}
		System.out.println(l);
	}
}
