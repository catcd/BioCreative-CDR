package edu.ktlab.bionlp.cdr.nlp.ner;

import java.io.File;
import java.nio.charset.Charset;

import edu.ktlab.bionlp.cdr.nlp.ner.CDRNERRecognizer;
import edu.ktlab.bionlp.cdr.nlp.tokenizer.TokenizerMESingleton;
import edu.ktlab.bionlp.cdr.util.FileHelper;

public class MaxentNERRecognizerFileExample {
	static String file = "eventAnnnotation.txt";

	public static void main(String[] args) throws Exception {
		CDRNERRecognizer nerFinder = new CDRNERRecognizer("models/ner/htmldata.model",
				MaxentNERFactoryExample.createFeatureGenerator());
		String[] sents = FileHelper.readFileAsLines(file);
		String output = "";
		for (String sent : sents) {
			String[] tokens = TokenizerMESingleton.getInstance().tokenize(sent);
			output += nerFinder.recognize(tokens) + "\n";
		}
		FileHelper.writeToFile(output, new File("eventAnnotated.txt"), Charset.forName("UTF-8"));
	}
}
