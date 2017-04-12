package edu.ktlab.bionlp.cdr.nlp.tokenizer;

import java.io.FileInputStream;
import java.io.InputStream;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class TokenizerMESingleton {
	TokenizerME tokenizer;
	final String modelTokenizer = "models/tokenizer/biotokenizer.1.0.model";
	static TokenizerMESingleton ourInstance = new TokenizerMESingleton();

	public TokenizerMESingleton() {
		tokenizer = createTokenizerModel();
	}

	public static TokenizerMESingleton getInstance() {
		return ourInstance;
	}

	public TokenizerME createTokenizerModel() {
		InputStream in;
		try {
			in = new FileInputStream(modelTokenizer);
			TokenizerModel tokenizerModel = new TokenizerModel(in);

			return new TokenizerME(tokenizerModel);
		} catch (Exception e) {
		}
		return null;
	}

	public String[] tokenize(String text) {
		String[] tokens = tokenizer.tokenize(text);
		return tokens;
	}
}
