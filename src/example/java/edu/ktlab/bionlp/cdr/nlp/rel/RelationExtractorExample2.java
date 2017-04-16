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

public class RelationExtractorExample2 {
	static String fileCorpus = "data/cdr_test/cdr_test.gzip";

	public static void main(String[] args) throws Exception {
		Collection col = CollectionFactory.loadJsonFile(fileCorpus);

		File []fs = {
	              new File("/home/catcan/Desktop/data/test.directed.tag.pos"),
	              new File("/home/catcan/Desktop/data/test.directed.value.pos"),
	              new File("/home/catcan/Desktop/data/test.undirected.tag.pos"),
	              new File("/home/catcan/Desktop/data/test.undirected.value.pos"),
	              new File("/home/catcan/Desktop/data/test.directed.tag.neg"),
	              new File("/home/catcan/Desktop/data/test.directed.value.neg"),
	              new File("/home/catcan/Desktop/data/test.undirected.tag.neg"),
	              new File("/home/catcan/Desktop/data/test.undirected.value.neg")
		};
		
		for (File f : fs) {
			if (f.exists()) {
				f.createNewFile();
			}
		}

		FileWriter FR_P = new FileWriter(fs[0]);
		FileWriter FRV_P = new FileWriter(fs[1]);
		FileWriter FRU_P = new FileWriter(fs[2]);
		FileWriter FRUV_P = new FileWriter(fs[3]);
		FileWriter FR_N = new FileWriter(fs[4]);
		FileWriter FRV_N = new FileWriter(fs[5]);
		FileWriter FRU_N = new FileWriter(fs[6]);
		FileWriter FRUV_N = new FileWriter(fs[7]);

		for (Document doc : col.getDocuments()) {
			Set<Relation> candidateRels = doc.getRelationCandidates();
			List<Sentence> sents = doc.getSentences();
			for (Sentence sent : sents) {
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
									if (shortestPathEdges == null)
										continue;
									
									String r = "";
									String rR = "";
									String rV = "";
									String rRV = "";
									String rUndirected = "";
									String rUndirectedR = "";
									String rUndirectedV = "";
									String rUndirectedRV = "";
									IndexedWord next = idxTokenChed;
									for(SemanticGraphEdge edge : shortestPathEdges) {
										r += next + " ";
										rR = " " + next + rR;
										rV += next.value() + " ";
										rRV = " " + next.value() + rRV;
										rUndirected += next + " ";
										rUndirectedR = " " + next + rUndirectedR;
										rUndirectedV += next.value() + " ";
										rUndirectedRV = " " + next.value() + rUndirectedRV;
										if (edge.getSource().equals(next)) {
											r += "-ARR- (" + edge.getRelation() + ") ";
											rR = " -ARL- (" + edge.getRelation() + ")" + rR;
											rV += "-ARR- (" + edge.getRelation() + ") ";
											rRV = " -ARL- (" + edge.getRelation() + ")" + rRV;
											rUndirected += "(" + edge.getRelation() + ") ";
											rUndirectedR = " (" + edge.getRelation() + ")" + rUndirectedR;
											rUndirectedV += "(" + edge.getRelation() + ") ";
											rUndirectedRV = " (" + edge.getRelation() + ")" + rUndirectedRV;
											next = edge.getTarget();
										} else {
											r += "-ARL- (" + edge.getRelation() + ") ";
											rR = " -ARR- (" + edge.getRelation() + ")" + rR;
											rV += "-ARL- (" + edge.getRelation() + ") ";
											rRV = " -ARR- (" + edge.getRelation() + ")" + rRV;
											rUndirected += "(" + edge.getRelation() + ") ";
											rUndirectedR = " (" + edge.getRelation() + ")" + rUndirectedR;
											rUndirectedV += "(" + edge.getRelation() + ") ";
											rUndirectedRV = " (" + edge.getRelation() + ")" + rUndirectedRV;
											next = edge.getSource();
										}
									}
									r += next;
									rR = next + rR;
									rV += next.value();
									rRV = next.value() + rRV;
									rUndirected += next;
									rUndirectedR = next + rUndirectedR;
									rUndirectedV += next.value();
									rUndirectedRV = next.value() + rUndirectedRV;
									
									if (label.equals("CID")) {
										FR_P.write(r + '\n');
										FRV_P.write(rV + '\n');
										FRU_P.write(rUndirected + '\n');
										FRUV_P.write(rUndirectedV + '\n');
									} else {
										FR_N.write(r + '\n');
										FRV_N.write(rV + '\n');
										FRU_N.write(rUndirected + '\n');
										FRUV_N.write(rUndirectedV + '\n');
									}
								}
							}
						}
					}
				}
			}
		}

		FR_P.close();
		FRV_P.close();
		FRU_P.close();
		FRUV_P.close();
		FR_N.close();
		FRV_N.close();
		FRU_N.close();
		FRUV_N.close();
	}
}
