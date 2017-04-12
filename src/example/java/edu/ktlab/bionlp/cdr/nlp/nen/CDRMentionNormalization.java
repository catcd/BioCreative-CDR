package edu.ktlab.bionlp.cdr.nlp.nen;

import java.io.File;
import java.nio.charset.Charset;
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
import edu.ktlab.bionlp.cdr.util.FileHelper;

public class CDRMentionNormalization {
	static String trainFile = "data/cdr/cdr_train/cdr_train.txt";
	static String testFile = "data/cdr/cdr_dev/cdr_dev.txt";
	static File outFile = new File("temp/norm.dev.cos.txt");

	public static void main(String[] args) throws Exception {
		if (outFile.exists())
			outFile.delete();

		System.out.print("MESH 2015 loading...");
		MESHSearcher searcher = new MESHSearcher();
		searcher.loadMESH("data/mesh2015.txt");
		System.out.println("done.");

		PhraseSimilarity sim = new WeightCosinePhraseSimilarity(new CosineWordSimilarity(), true);
		// PhraseSimilarity sim = new WeightCosinePhraseSimilarity(new
		// W2VWordSimilarity("data/embedding/glove.txt"), false);
		CollectionFactory factory = new CollectionFactory(false);
		Collection colTrain = factory.loadFile(trainFile);
		Collection colTest = factory.loadFile(testFile);
		
		Map<String, String> memoryLinked = new HashMap<String, String>();
		for (Annotation ann : colTrain.getAnnotations()) {
			memoryLinked.put(ann.getContent().toLowerCase(), ann.getReference());
		}
		
		for (Annotation ann : colTest.getAnnotations()) {
			String predict = "-1";
			String query = "";
			String info = "";

			if (memoryLinked.containsKey(ann.getContent().toLowerCase())) {
				predict = memoryLinked.get(ann.getContent().toLowerCase());
				query = ann.getContent().toLowerCase();
				info = ann.getContent().toLowerCase();
			} else {
				
				String[] tokens = ann.getStrTokens(true);
				
				float max = 0.01f;
				if (tokens.length == 0) {
					System.err.println(ann.getReference());
					continue;
				}

				for (String token : tokens)
					if (StringUtils.isAlphanumeric(token))
						query += token + " ";

				Map<String, String> results = searcher.searchMESHByName(query.trim(), 11);
				for (String mesh : results.keySet()) {
					float score = sim.score(tokens, SimpleTokenizer.INSTANCE.tokenize(mesh.toLowerCase()));
					if (score > max) {
						max = score;
						predict = results.get(mesh);
						info = score + "\t" + mesh;
					}
				}	
			}
				
			
			String output = query + "\t" + ann.getReference() + "\t" + predict + "\t"
					+ (ann.getReference().contains(predict) + "\t" + info);
			FileHelper.appendToFile(output + "\n", outFile, Charset.forName("UTF-8"));
		}

	}

}
