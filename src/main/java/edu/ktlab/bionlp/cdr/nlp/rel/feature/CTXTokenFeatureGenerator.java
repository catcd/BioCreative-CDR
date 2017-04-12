package edu.ktlab.bionlp.cdr.nlp.rel.feature;

import java.util.ArrayList;

import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.base.Token;
import edu.ktlab.bionlp.cdr.util.DependencyHelper;
import edu.ktlab.bionlp.cdr.util.StringHelper;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.semgraph.SemanticGraph;

public class CTXTokenFeatureGenerator implements FeatureGenerator<Token, Sentence> {

	public ArrayList<String> extractFeatures(Token token, Sentence sentence) {
		ArrayList<String> featureCollector = new ArrayList<String>();
		String word = token.getContent();

		String orth = StringHelper.wordFeatures(word);
		if (orth != null) {
			featureCollector.add("TC:" + orth);
			if (!orth.equals("NUM") && !orth.equals("PUNCT")) {
				featureCollector.addAll(getCurrentWordFeatures(token, sentence));
			}
		} else {
			featureCollector.addAll(getCurrentWordFeatures(token, sentence));
		}

		return featureCollector;
	}

	public ArrayList<String> getCurrentWordFeatures(Token token, Sentence sentence) {
		ArrayList<String> featureCollector = new ArrayList<String>();
		SemanticGraph semgraph = DependencyHelper.convertSemanticGraph(sentence.getDeptree());
		IndexedWord idxword = semgraph.getNodeByIndexSafe(token.getId() + 1);
		if (idxword == null)
			return new ArrayList<String>();
		
		String word = token.getContent().toLowerCase();
		String stemText = StringHelper.stemmerFeature(word);

		featureCollector.add("W:" + word);

		featureCollector.add("STEM:" + stemText);
		featureCollector.addAll(StringHelper.ngramCharacter(2, 4, word, "", "CNG:"));
		featureCollector.addAll(getNGramWindowFeatures(token, sentence, 4, 3));
		// Get POS of this token
		String postag = idxword.tag();
		String lemma = Morphology.lemmaStatic(word, postag, true);

		featureCollector.add("POS:" + postag);
		if (!lemma.equals(word)) {
			featureCollector.add("BASE:" + lemma);
		}
		return featureCollector;
	}

	public ArrayList<String> getNGramWindowFeatures(Token token, Sentence sentence, int size, int ngram) {
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
		featureCollector.addAll(StringHelper.ngramWord(ngram, window.toArray(new String[window.size()]), "_", "WNG:"));

		return featureCollector;
	}
}
