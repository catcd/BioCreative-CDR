package edu.ktlab.bionlp.cdr.nlp.ner.features;

import java.util.List;

import opennlp.tools.util.featuregen.FeatureGeneratorAdapter;
import opennlp.tools.util.featuregen.FeatureGeneratorUtil;

public class TrigramNameFeatureGenerator extends FeatureGeneratorAdapter {
	public void createFeatures(List<String> features, String[] tokens, int index,
			String[] previousOutcomes) {
		String wc = FeatureGeneratorUtil.tokenFeature(tokens[index]);
		// tri-gram features
		if (index > 0 && index + 1 < tokens.length) {
			features.add("pw,w,nw=" + tokens[index - 1] + "," + tokens[index] + ","
					+ tokens[index + 1]);
			String pwc = FeatureGeneratorUtil.tokenFeature(tokens[index - 1]);
			String nwc = FeatureGeneratorUtil.tokenFeature(tokens[index + 1]);
			features.add("pwc,wc,nwc=" + pwc + "," + wc + "," + nwc);
		}
	}
}
