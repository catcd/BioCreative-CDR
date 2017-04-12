package edu.ktlab.bionlp.cdr.nlp.rel.feature;

import java.util.ArrayList;

public class ContextGenerator<T1, T2> {
	private FeatureGenerator<T1, T2>[] mFeatureGenerators;

	public ContextGenerator(FeatureGenerator<T1, T2>[] mFeatureGenerators) {
		this.mFeatureGenerators = mFeatureGenerators;
	}

	public ArrayList<String> getContext(T1 t1, T2 t2) {
		ArrayList<String> context = new ArrayList<String>();
		for (FeatureGenerator<T1, T2> generator : mFeatureGenerators) {
			ArrayList<String> extractedFeatures = generator.extractFeatures(t1, t2);
			context.addAll(extractedFeatures);
		}
		return context;
	}
}
