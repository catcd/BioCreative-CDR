package edu.ktlab.bionlp.cdr.nlp.tokenizer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenSampleStream;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class TokenizerMETrainUtil {
	@SuppressWarnings("deprecation")
	public static void trainTokenizerModel() throws IOException {
		InputStream trainDataIn = new FileInputStream(
				"data/trainset/tokenizer/biotokenizer.1.0.train");
		OutputStream out = new FileOutputStream("models/tokenizer/biotokenizer.1.0.model");
		ObjectStream<TokenSample> samples = new TokenSampleStream(new PlainTextByLineStream(
				new InputStreamReader(trainDataIn, "UTF-8")));
		TokenizerME.train("en", samples, true, 1, 100).serialize(out);
		out.close();
	}

	public static void main(String... args) throws IOException {
		trainTokenizerModel();
	}
}
