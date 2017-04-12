package edu.ktlab.bionlp.cdr.nlp.postagger;

import java.io.FileInputStream;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

public class POSTaggerSingleton {
	static String modelPOSTagger = "models/postagger/biopos.wsj_genia.1.0.model";
	static POSTaggerME tagger;
	static POSTaggerSingleton ourInstance = new POSTaggerSingleton();

	POSTaggerSingleton() {
		tagger = createSentenceDetectorModel();
	}

	public static POSTaggerSingleton getInstance() {
		return ourInstance;
	}

	public POSTaggerME createSentenceDetectorModel() {
		InputStream in;
		try {
			in = new FileInputStream(getClass().getClassLoader().getResource(modelPOSTagger).getFile());
			POSModel posModel = new POSModel(in);
			in.close();
			return new POSTaggerME(posModel);
		} catch (Exception e) {
		}
		return null;
	}

	public String[] tagging(String[] tokens) {
		String tags[] = tagger.tag(tokens);
		return tags;
	}
}
