package edu.ktlab.bionlp.cdr.nlp.splitter;

import java.io.FileInputStream;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

public class SentSplitterMESingleton {
	final String modelSplitter = "models/sentencesplitter/biosent.1.0.model";
	SentenceDetectorME splitter;
	static SentSplitterMESingleton ourInstance = new SentSplitterMESingleton();

	SentSplitterMESingleton() {
		splitter = createSentenceDetectorModel();
	}

	public static SentSplitterMESingleton getInstance() {
		return ourInstance;
	}

	public SentenceDetectorME createSentenceDetectorModel() {
		InputStream in;
		try {
			in = new FileInputStream(modelSplitter);
			SentenceModel sentModel = new SentenceModel(in);

			return new SentenceDetectorME(sentModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] split(String text) {
		String[] sents = splitter.sentDetect(text);
		return sents;
	}

	public Span[] senPosSplit(String text) {
		Span[] sents = splitter.sentPosDetect(text);
		return sents;
	}
}
