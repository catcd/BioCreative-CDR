package edu.ktlab.bionlp.cdr.nlp.rel.feature;

import java.util.ArrayList;

public interface FeatureGenerator<T1, T2> {
	public ArrayList<String> extractFeatures(T1 candidate, T2 context);
}
