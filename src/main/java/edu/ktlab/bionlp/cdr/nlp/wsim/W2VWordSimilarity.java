package edu.ktlab.bionlp.cdr.nlp.wsim;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import edu.ktlab.bionlp.cdr.nlp.wsim.distance.Distance;
import edu.ktlab.bionlp.cdr.nlp.wsim.distance.EuclideanDistance;

public class W2VWordSimilarity implements WordSimilarity {
	Map<String, float[]> w2vecs = new HashMap<String, float[]>();
	Distance distance = new EuclideanDistance();

	public W2VWordSimilarity(String fileVectors) throws Exception {
		loadVectors(fileVectors);
	}

	public W2VWordSimilarity(String fileVectors, Distance distance) throws Exception {
		this(fileVectors);
		this.distance = distance;
	}

	void loadVectors(String file) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String line;
		while ((line = in.readLine()) != null) {
			String[] fls = StringUtils.split(line, " ");
			float vec[] = new float[fls.length - 1];
			for (int i = 1; i < fls.length; i++)
				vec[i - 1] = Float.parseFloat(fls[i]);
			w2vecs.put(fls[0], vec);
		}

		in.close();
		in = null;
	}

	public float score(String word1, String word2) {
		float[] v1 = w2vecs.get(word1);
		float[] v2 = w2vecs.get(word2);
		
		if (v1 == null || v2 == null)
			return 0;		
	
		return distance.distance(v1, v2);
	}

}
