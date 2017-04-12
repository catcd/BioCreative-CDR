package edu.ktlab.bionlp.cdr.nlp.tokenizer;

import java.io.DataInputStream;
import java.io.FileInputStream;

import opennlp.maxent.io.BinaryGISModelReader;
import opennlp.model.AbstractModel;
import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class Tokenizer {
	final TokenizerME tokenizer;
	final String modelTokenizer = "models/tokenizer/biotokenizer.1.0.model";

	public Tokenizer() {
		tokenizer = createTokenizerModel();
	}

	public TokenizerME createTokenizerModel() {
		try {
			AbstractModel model = new BinaryGISModelReader(new DataInputStream(new FileInputStream(
					getClass().getClassLoader().getResource(modelTokenizer).getFile()))).getModel();
			TokenizerModel tokenizerModel = new TokenizerModel(model, null, new TokenizerFactory(
					"en", null, true, null));
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
