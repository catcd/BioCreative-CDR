package edu.ktlab.bionlp.cdr.nlp.rel.feature;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.base.Token;
import edu.ktlab.bionlp.cdr.util.DependencyHelper;
import edu.ktlab.bionlp.cdr.util.StringHelper;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;

public class CTXDependencyFeatureGenerator implements FeatureGenerator<Token, Sentence> {

	public ArrayList<String> extractFeatures(Token token, Sentence sentence) {
		ArrayList<String> featureCollector = new ArrayList<String>();
		SemanticGraph semgraph = DependencyHelper.convertSemanticGraph(sentence.getDeptree());
		IndexedWord idxword = semgraph.getNodeByIndexSafe(token.getId() + 1);
		if (idxword == null)
			return new ArrayList<String>();

		// Get neighbouring features
		ArrayList<String> neighfeatures = getNeighbouringFeatures(idxword, semgraph);
		if (neighfeatures != null)
			featureCollector.addAll(neighfeatures);

		return featureCollector;
	}

	ArrayList<String> getNeighbouringFeatures(IndexedWord word, SemanticGraph semgraph) {
		ArrayList<String> featureCollector = new ArrayList<String>();

		List<SemanticGraphEdge> layer1 = semgraph.getOutEdgesSorted(word);
		ArrayList<Pair<SemanticGraphEdge, SemanticGraphEdge>> twoStepPaths = new ArrayList<Pair<SemanticGraphEdge, SemanticGraphEdge>>();
		if (layer1 != null)
			for (SemanticGraphEdge edge1 : layer1) {
				List<SemanticGraphEdge> layer2 = semgraph.getOutEdgesSorted(edge1.getSource());
				if (layer2 != null) {
					for (SemanticGraphEdge edge2 : layer2) {
						Pair<SemanticGraphEdge, SemanticGraphEdge> twoStepPath = new MutablePair<SemanticGraphEdge, SemanticGraphEdge>(
								edge1, edge2);
						twoStepPaths.add(twoStepPath);
					}
				} else {
					String step0 = Morphology.lemmaStatic(edge1.getSource().word(), edge1.getSource().tag(), true);
					String step1 = Morphology.lemmaStatic(edge1.getTarget().word(), edge1.getTarget().tag(), true);
					featureCollector.add("1DP:F:" + step0 + "_" + edge1.getRelation().getShortName().toUpperCase()
							+ "_" + step1);
					featureCollector.add("1DP:W:" + step0 + "_" + step1);
				}
			}

		if (twoStepPaths.size() == 0)
			return null;
		else {
			for (Pair<SemanticGraphEdge, SemanticGraphEdge> twoStepPath : twoStepPaths) {
				String[] twoStepFullSequence = new String[5];
				String[] twoStepWordSequence = new String[3];
				String[] twoStepDependencySequence = new String[2];

				try {
					String step0 = Morphology.lemmaStatic(twoStepPath.getLeft().getSource().word(), twoStepPath
							.getLeft().getSource().tag(), true);
					String step1 = Morphology.lemmaStatic(twoStepPath.getLeft().getTarget().word(), twoStepPath
							.getLeft().getTarget().tag(), true);
					String step2 = Morphology.lemmaStatic(twoStepPath.getRight().getTarget().word(), twoStepPath
							.getRight().getTarget().tag(), true);

					twoStepFullSequence[0] = step0;
					twoStepFullSequence[1] = twoStepPath.getLeft().getRelation().getShortName().toUpperCase();
					twoStepFullSequence[2] = step1;
					twoStepFullSequence[3] = twoStepPath.getRight().getRelation().getShortName().toUpperCase();
					twoStepFullSequence[4] = step2;

					twoStepWordSequence[0] = step0;
					twoStepWordSequence[1] = step1;
					twoStepWordSequence[2] = step2;

					twoStepDependencySequence[0] = twoStepPath.getLeft().getRelation().getShortName().toUpperCase();
					twoStepDependencySequence[1] = twoStepPath.getRight().getRelation().getShortName().toUpperCase();

					featureCollector.addAll(StringHelper.ngramWord(2, 3, twoStepFullSequence, "_", "2DP:F:"));
					featureCollector.addAll(StringHelper.ngramWord(2, 2, twoStepWordSequence, "_", "2DP:W:"));
					featureCollector.addAll(StringHelper.gramWord(2, twoStepDependencySequence, "_", "2DP:D:"));
				} catch (Exception e) {

				}

			}
		}

		return featureCollector;
	}

}
