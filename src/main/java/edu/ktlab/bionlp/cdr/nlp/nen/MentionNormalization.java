package edu.ktlab.bionlp.cdr.nlp.nen;

import java.util.HashMap;
import java.util.Map;

import opennlp.tools.tokenize.SimpleTokenizer;

import org.apache.commons.lang.StringUtils;

import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.dataset.MESHSearcher;
import edu.ktlab.bionlp.cdr.nlp.wsim.CosineWordSimilarity;
import edu.ktlab.bionlp.cdr.nlp.wsim.PhraseSimilarity;
import edu.ktlab.bionlp.cdr.nlp.wsim.WeightCosinePhraseSimilarity;

public class MentionNormalization {
	PhraseSimilarity sim = new WeightCosinePhraseSimilarity(new CosineWordSimilarity(), true);
	Map<String, String> memoryLinked = new HashMap<String, String>();
	MESHSearcher searcher = new MESHSearcher();
	CollectionFactory factory = new CollectionFactory(false);

	public MentionNormalization(String trainFile, String meshFile) {
		try {
			loadMemoryLinked(trainFile);
			searcher.loadMESH(meshFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void loadMemoryLinked(String trainFile) {
		Collection colTrain = factory.loadFile(trainFile);
		for (Annotation ann : colTrain.getAnnotations()) {
			memoryLinked.put(ann.getContent().toLowerCase(), ann.getReference());
		}
	}

	public String normalize(String mention, String[] tokens) {
		String meshId = "-1";

		if (memoryLinked.containsKey(mention.toLowerCase())) {
			meshId = memoryLinked.get(mention.toLowerCase());
		} else {
			float max = 0.01f;
			String query = "";
			for (String token : tokens)
				if (StringUtils.isAlphanumeric(token))
					query += token + " ";

			Map<String, String> results;
			try {
				results = searcher.searchMESHByName(query.trim(), 11);
				for (String mesh : results.keySet()) {
					float score = sim.score(tokens, SimpleTokenizer.INSTANCE.tokenize(mesh.toLowerCase()));
					if (score > max) {
						max = score;
						meshId = results.get(mesh);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return meshId;
	}
}
