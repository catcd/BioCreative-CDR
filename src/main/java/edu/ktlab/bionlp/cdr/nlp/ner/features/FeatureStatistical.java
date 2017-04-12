package edu.ktlab.bionlp.cdr.nlp.ner.features;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class FeatureStatistical {
	class FeatureLabel {
		String feature;
		Map<String, Integer> labelValue = new TreeMap<String, Integer>();

		public FeatureLabel(String feature) {
			this.feature = feature;
		}

		public void addLabel(String label) {
			if (!labelValue.containsKey(label))
				labelValue.put(label, 1);
			else {
				int value = labelValue.get(label) + 1;
				labelValue.remove(label);
				labelValue.put(label, value);
			}
		}

		public int getSumValue() {
			int sum = 0;
			for (int value : labelValue.values())
				sum += value;
			return sum;
		}

		public String toString() {
			String output = feature + " ";
			int sum = getSumValue();
			for (String label : labelValue.keySet()) {
				output += label + "=" + (labelValue.get(label) / (double) sum) + " ";
			}
			return output;
		}

		public String toString(Set<String> labels) {
			String output = feature + " ";
			int sum = getSumValue();
			for (String label : labels) {
				if (labelValue.get(label) != null)
					output += label + ":" + (labelValue.get(label) / (double) sum) + " ";
				else
					output += label + ":0 ";
			}
			return output;
		}
	}

	Map<String, FeatureLabel> features = new TreeMap<String, FeatureLabel>();
	Set<String> labels = new HashSet<String>();

	public void addFeatureLabel(String feature, String label) {
		if (!features.containsKey(feature)) {
			FeatureLabel featureLabel = new FeatureLabel(feature);
			featureLabel.addLabel(label);
			features.put(feature, featureLabel);
		} else {
			FeatureLabel featureLabel = features.get(feature);
			featureLabel.addLabel(label);
		}
		labels.add(label);
	}

	public String toString() {
		String output = "";
		for (String feature : features.keySet()) {
			output += features.get(feature).toString(labels) + "\n";
		}
		return output;
	}

}
