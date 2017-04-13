package edu.ktlab.bionlp.cdr.nlp.rel.feature;

import java.util.ArrayList;
import java.util.List;

import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.base.Token;
import edu.ktlab.bionlp.cdr.util.DependencyHelper;
import edu.ktlab.bionlp.cdr.util.StringHelper;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.Pair;
// TODO CATCAN Dependency tree
public class CIDDependencyFeatureGenerator implements FeatureGenerator<Pair<Annotation, Annotation>, Sentence> {
	public ArrayList<String> extractFeatures(Pair<Annotation, Annotation> pair, Sentence sentence) {
		ArrayList<String> featureCollector = new ArrayList<String>();
		SemanticGraph semgraph = DependencyHelper.convertSemanticGraph(sentence.getDeptree());
		if (semgraph == null)
			return featureCollector;
		
		Annotation annChed = pair.first;
		Annotation annDis = pair.second;
		featureCollector.add("CD:RF:" + annChed.getReference());
		featureCollector.add("DS:RF:" + annDis.getReference());
		featureCollector.add("REL:RF:" + annChed.getReference() + "_" + annDis.getReference());
		List<Token> tokensChed = annChed.getTokens();
		List<Token> tokensDis = annDis.getTokens();

		for (Token tokenChed : tokensChed) {
			IndexedWord idxTokenChed = semgraph.getNodeByIndexSafe(tokenChed.getId() + 1);
			if (idxTokenChed == null)
				continue;

			featureCollector.addAll(getNeighbouringFeatures("CD:", idxTokenChed, semgraph));
			featureCollector.addAll(getTokenFeatures("CD:", tokenChed, sentence, semgraph));

			for (Token tokenDis : tokensDis) {
				IndexedWord idxTokenDis = semgraph.getNodeByIndexSafe(tokenDis.getId() + 1);
				if (idxTokenDis == null)
					continue;

				featureCollector.addAll(getNeighbouringFeatures("DS:", idxTokenDis, semgraph));
				featureCollector.addAll(getTokenFeatures("DS:", tokenDis, sentence, semgraph));

				featureCollector.addAll(getShortestFeatures(idxTokenChed, idxTokenDis, semgraph));
			}
		}

		return featureCollector;
	}

	public ArrayList<String> getShortestFeatures(IndexedWord tokenChed, IndexedWord tokenDis, SemanticGraph semgraph) {
		ArrayList<String> featureCollector = new ArrayList<String>();

		List<SemanticGraphEdge> shortestPathEdges = semgraph.getShortestUndirectedPathEdges(tokenChed, tokenDis);

		if (shortestPathEdges == null)
			return featureCollector;
		
		if (shortestPathEdges.size() == 0)
			return featureCollector;
		
		featureCollector.add("SPE:LEN:" + shortestPathEdges.size());

		ArrayList<String> fullSPSequence = new ArrayList<String>();
		ArrayList<String> wordSPSequence = new ArrayList<String>();
		ArrayList<String> wordposSPSequence = new ArrayList<String>();
		ArrayList<String> dependencySPSequence = new ArrayList<String>();

		fullSPSequence.add(tokenChed.word());
		wordSPSequence.add(tokenChed.word());
		wordposSPSequence.add(tokenChed.word() + "-" + tokenChed.tag().toUpperCase());

		for (SemanticGraphEdge edge : shortestPathEdges) {
			fullSPSequence.add(edge.getRelation().getShortName().toUpperCase());
			fullSPSequence.add(edge.getTarget().word());

			wordSPSequence.add(edge.getTarget().word());
			if (edge.getTarget().tag() != null)
				wordposSPSequence.add(edge.getTarget().word() + "-" + edge.getTarget().tag().toUpperCase());

			dependencySPSequence.add(edge.getRelation().getShortName().toUpperCase());
		}

		featureCollector.addAll(StringHelper.ngramWord(2, 2, fullSPSequence.toArray(new String[fullSPSequence.size()]),
				"_", "SPE:F:"));
		featureCollector.addAll(StringHelper.ngramWord(2, 2, wordSPSequence.toArray(new String[wordSPSequence.size()]),
				"_", "SPE:W:"));
		featureCollector.addAll(StringHelper.ngramWord(2, 2,
				wordposSPSequence.toArray(new String[wordposSPSequence.size()]), "_", "SPE:WP:"));
		featureCollector.addAll(StringHelper.ngramWord(2,
				dependencySPSequence.toArray(new String[dependencySPSequence.size()]), "_", "SPE:D:"));

		featureCollector.addAll(StringHelper.ngramWord(2, 2, fullSPSequence.toArray(new String[fullSPSequence.size()]),
				"_", "SPE:F:"));
		featureCollector.addAll(StringHelper.ngramWord(2, 2, wordSPSequence.toArray(new String[wordSPSequence.size()]),
				"_", "SPE:W:"));
		featureCollector.addAll(StringHelper.ngramWord(2, 2,
				wordposSPSequence.toArray(new String[wordposSPSequence.size()]), "_", "SPE:WP:"));
		featureCollector.addAll(StringHelper.ngramWord(2,
				dependencySPSequence.toArray(new String[dependencySPSequence.size()]), "_", "SPE:D:"));

		return featureCollector;
	}

	public ArrayList<String> getNeighbouringFeatures(String prefix, IndexedWord word, SemanticGraph semgraph) {
		ArrayList<String> featureCollector = new ArrayList<String>();

		List<SemanticGraphEdge> layer1 = semgraph.getOutEdgesSorted(word);
		ArrayList<Pair<SemanticGraphEdge, SemanticGraphEdge>> twoStepPaths = new ArrayList<Pair<SemanticGraphEdge, SemanticGraphEdge>>();
		if (layer1 != null)
			for (SemanticGraphEdge edge1 : layer1) {
				List<SemanticGraphEdge> layer2 = semgraph.getOutEdgesSorted(edge1.getSource());
				if (layer2 != null) {
					for (SemanticGraphEdge edge2 : layer2) {
						Pair<SemanticGraphEdge, SemanticGraphEdge> twoStepPath = new Pair<SemanticGraphEdge, SemanticGraphEdge>(
								edge1, edge2);
						twoStepPaths.add(twoStepPath);
					}
				} else {
					featureCollector.add(prefix + "1DP:F:" + edge1.getSource().word() + "_"
							+ edge1.getRelation().getShortName().toUpperCase() + "_" + edge1.getTarget().word());
					featureCollector.add(prefix + "1DP:W:" + edge1.getSource().word() + "_" + edge1.getTarget().word());
				}
			}

		if (twoStepPaths.size() == 0)
			return new ArrayList<String>();
		else {
			for (Pair<SemanticGraphEdge, SemanticGraphEdge> twoStepPath : twoStepPaths) {
				String[] twoStepFullSequence = new String[5];
				String[] twoStepWordSequence = new String[3];
				String[] twoStepDependencySequence = new String[2];

				try {
					String step0 = twoStepPath.first().getSource().word();
					String step1 = twoStepPath.first().getTarget().word();
					String step2 = twoStepPath.second().getTarget().word();

					twoStepFullSequence[0] = step0;
					twoStepFullSequence[1] = twoStepPath.first().getRelation().getShortName().toUpperCase();
					twoStepFullSequence[2] = step1;
					twoStepFullSequence[3] = twoStepPath.second().getRelation().getShortName().toUpperCase();
					twoStepFullSequence[4] = step2;

					twoStepWordSequence[0] = step0;
					twoStepWordSequence[1] = step1;
					twoStepWordSequence[2] = step2;

					twoStepDependencySequence[0] = twoStepPath.first().getRelation().getShortName().toUpperCase();
					twoStepDependencySequence[1] = twoStepPath.second().getRelation().getShortName().toUpperCase();

					featureCollector.addAll(StringHelper.ngramWord(2, 3, twoStepFullSequence, "_", prefix + "2DP:F:"));
					featureCollector.addAll(StringHelper.ngramWord(2, 2, twoStepWordSequence, "_", prefix + "2DP:W:"));
					featureCollector
							.addAll(StringHelper.gramWord(2, twoStepDependencySequence, "_", prefix + "2DP:D:"));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		return featureCollector;
	}

	public ArrayList<String> getTokenFeatures(String prefix, Token token, Sentence sentence, SemanticGraph semgraph) {
		ArrayList<String> featureCollector = new ArrayList<String>();
		String word = token.getContent();

		String orth = StringHelper.wordFeatures(word);
		if (orth != null) {
			featureCollector.add(prefix + "TC:" + orth);
			if (!orth.equals("NUM") && !orth.equals("PUNCT")) {
				featureCollector.addAll(getCurrentWordFeatures(prefix, token, sentence, semgraph));
			}
		} else {
			featureCollector.addAll(getCurrentWordFeatures(prefix, token, sentence, semgraph));
		}

		return featureCollector;
	}

	public ArrayList<String> getCurrentWordFeatures(String prefix, Token token, Sentence sentence,
			SemanticGraph semgraph) {
		ArrayList<String> featureCollector = new ArrayList<String>();
		IndexedWord idxword = semgraph.getNodeByIndexSafe(token.getId() + 1);
		if (idxword == null)
			return new ArrayList<String>();

		String word = token.getContent().toLowerCase();
		String stemText = StringHelper.stemmerFeature(word);

		featureCollector.add(prefix + "W:" + word);

		featureCollector.add(prefix + "STEM:" + stemText);
		featureCollector.addAll(StringHelper.ngramCharacter(2, 4, word, "", prefix + "CNG:"));
		featureCollector.addAll(getNGramWindowFeatures(prefix, token, sentence, 4, 3));
		// Get POS of this token
		String postag = idxword.tag();
		// String lemma = Morphology.lemmaStatic(word, postag, true);

		featureCollector.add(prefix + "POS:" + postag);
		// if (!lemma.equals(word)) {
		// featureCollector.add(prefix + "BASE:" + lemma);
		// }
		return featureCollector;
	}

	public ArrayList<String> getNGramWindowFeatures(String prefix, Token token, Sentence sentence, int size, int ngram) {
		ArrayList<String> featureCollector = new ArrayList<String>();

		ArrayList<String> window = new ArrayList<String>();
		int indexToken = token.getId();
		int leftIndex = indexToken - size;
		if (leftIndex < 1)
			leftIndex = 1;
		int rightIndex = indexToken + size;
		if (rightIndex > sentence.getTokens().size())
			rightIndex = sentence.getTokens().size();
		for (int i = leftIndex; i <= rightIndex; i++)
			window.add(sentence.getTokens().get(i - 1).getContent().toLowerCase());
		featureCollector.addAll(StringHelper.ngramWord(ngram, window.toArray(new String[window.size()]), "_", prefix
				+ "WNG:"));

		return featureCollector;
	}

}
