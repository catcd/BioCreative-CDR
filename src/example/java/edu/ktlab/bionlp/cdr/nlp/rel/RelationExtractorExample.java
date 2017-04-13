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

public class RelationExtractorExample {
	static String fileCorpus = "data/cdr_full/cdr_full.gzip";

	public static void main(String[] args) throws Exception {
		Collection col = CollectionFactory.loadJsonFile(fileCorpus);

		File []fs = {
		              new File("/home/catcan/Desktop/data/cdr_full_parsed"),
		              new File("/home/catcan/Desktop/data/cdr_full_related_directed"),
		              new File("/home/catcan/Desktop/data/cdr_full_not_related_directed"),
		              new File("/home/catcan/Desktop/data/cdr_full_related_undirected"),
		              new File("/home/catcan/Desktop/data/cdr_full_not_related_undirected")
		};
		
		for (File f : fs) {
			if (f.exists()) {
				f.createNewFile();
			}
		}

		FileWriter parsed = new FileWriter(fs[0]);
		FileWriter relatedDirected = new FileWriter(fs[1]);
		FileWriter notRelatedDirected = new FileWriter(fs[2]);
		FileWriter relatedUndirected = new FileWriter(fs[3]);
		FileWriter notRelatedUndirected = new FileWriter(fs[4]);

		for (Document doc : col.getDocuments()) {
			Set<Relation> candidateRels = doc.getRelationCandidates();
			List<Sentence> sents = doc.getSentences();
			for (Sentence sent : sents) {
				parsed.write("Sentence" + sent + "\n");
				SemanticGraph semgraph = DependencyHelper.convertSemanticGraph(sent.getDeptree());
				for (Relation candidateRel : candidateRels) {
					if (sent.containRelation(candidateRel)) {
						String label = "CID";
						if (!doc.getRelations().contains(candidateRel))
							label = "NONE";
						
						List<Annotation> annsChed = sent.getAnnotation(candidateRel.getChemicalID());
						List<Annotation> annsDis = sent.getAnnotation(candidateRel.getDiseaseID());

						List<Pair<Annotation, Annotation>> pairs = Lists.newArrayList();
						for (Annotation annChed : annsChed)
							for (Annotation annDis : annsDis) {
								pairs.add(new Pair<Annotation, Annotation>(annChed, annDis));
							}

						for (Pair<Annotation, Annotation> pair : pairs) {
							parsed.write(label + pair + "\n");

							Annotation annChed = pair.first;
							Annotation annDis = pair.second;
							List<Token> tokensChed = annChed.getTokens();
							List<Token> tokensDis = annDis.getTokens();

							for (Token tokenChed : tokensChed) {
								IndexedWord idxTokenChed = semgraph.getNodeByIndexSafe(tokenChed.getId() + 1);
								if (idxTokenChed == null)
									continue;

								for (Token tokenDis : tokensDis) {
									IndexedWord idxTokenDis = semgraph.getNodeByIndexSafe(tokenDis.getId() + 1);
									if (idxTokenDis == null)
										continue;

									List<SemanticGraphEdge> shortestPathEdges = semgraph.getShortestUndirectedPathEdges(idxTokenChed, idxTokenDis);
									String r = "";
									String rUndirected = "";
									IndexedWord next = idxTokenChed;
									for(SemanticGraphEdge edge : shortestPathEdges) {
										r += next + " ";
										rUndirected += next + " ";
										if (edge.getSource().equals(next)) {
											r += "-ARR- (" + edge.getRelation() + ") ";
											rUndirected += "(" + edge.getRelation() + ") ";
											next = edge.getTarget();
										} else {
											r += "-ARL- (" + edge.getRelation() + ") ";
											rUndirected += "(" + edge.getRelation() + ") ";
											next = edge.getSource();
										}
									}
									r += next;
									rUndirected += next;
									if (label.equals("CID")) {
										relatedDirected.write(r + "\n");
										relatedUndirected.write(rUndirected + "\n");
									} else {
										notRelatedDirected.write(r + "\n");
										notRelatedUndirected.write(rUndirected + "\n");
									}
								}
							}
						}
					}
				}
			}
		}
		
		parsed.close();
		relatedDirected.close();
		notRelatedDirected.close();
		relatedUndirected.close();
		notRelatedUndirected.close();
	}
}
