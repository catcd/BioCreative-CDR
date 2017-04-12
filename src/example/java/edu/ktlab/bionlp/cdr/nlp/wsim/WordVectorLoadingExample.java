package edu.ktlab.bionlp.cdr.nlp.wsim;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import edu.ktlab.bionlp.cdr.nlp.wsim.distance.Distance;
import edu.ktlab.bionlp.cdr.nlp.wsim.distance.EuclideanDistance;
import edu.ktlab.bionlp.cdr.util.FileHelper;

public class WordVectorLoadingExample {

	public static void main(String[] args) {
		String file = "data/embedding/cbow.200.txt";
		String lines[] = FileHelper.readFileAsLines(file);
		Map<String, float[]> w2vecs = new HashMap<String, float[]>();
		for (String line : lines) {
			String[] fls = StringUtils.split(line, " ");
			float vec[] = new float[fls.length - 1];
			for (int i = 1; i < fls.length; i++)
				vec[i - 1] = Float.parseFloat(fls[i]);
			w2vecs.put(fls[0], vec);
		}
		
		Distance distance = new EuclideanDistance();
		Map<Float, String> topScores = new TreeMap<Float, String>();
		String word = "kidney";
		float bias = 0x1.0p-20f;
		int i = 0;
		for (String key : w2vecs.keySet()) {			
			topScores.put(distance.distance(w2vecs.get(key), w2vecs.get(word)) + bias * i, key);
			i++;
		}
			
		
		i = 0;
		int top = 20;
		for (float key : topScores.keySet()) {
			if (i == top)
				break;
			System.out.println(key + "\t" + topScores.get(key));
			i++;
		}

	}
	


}
