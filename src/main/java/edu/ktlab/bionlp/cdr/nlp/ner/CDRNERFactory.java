package edu.ktlab.bionlp.cdr.nlp.ner;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.FMeasure;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import edu.ktlab.bionlp.cdr.base.TextSpan;

public class CDRNERFactory {
	AdaptiveFeatureGenerator featureGenerator;

	public CDRNERFactory(AdaptiveFeatureGenerator featureGenerator) {
		this.featureGenerator = featureGenerator;
	}

	public TokenNameFinderModel trainNER(InputStream trainStream, String algorithm, int iterator, int cutoff)
			throws Exception {
		Charset charset = Charset.forName("UTF-8");
		ObjectStream<String> lineStream = new PlainTextByLineStream(trainStream, charset);

		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
		TokenNameFinderModel model;

		TrainingParameters mlParams = new TrainingParameters();
		mlParams.put(TrainingParameters.ALGORITHM_PARAM, algorithm);
		mlParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iterator));
		mlParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));

		try {
			model = NameFinderME.train("en", "CID", sampleStream, mlParams, featureGenerator,
					Collections.<String, Object> emptyMap());
		} finally {
			sampleStream.close();
		}

		return model;
	}

	public void trainNER(String trainingPath, String modelFilePath, String algorithm, int iterator, int cutoff)
			throws Exception {
		TokenNameFinderModel model = trainNER(new FileInputStream(trainingPath), algorithm, iterator, cutoff);

		BufferedOutputStream modelOut = null;
		try {
			modelOut = new BufferedOutputStream(new FileOutputStream(modelFilePath));
			model.serialize(modelOut);
		} finally {
			if (modelOut != null) {
				modelOut.close();
			}
		}
	}

	public void evaluatebyExactMatching(String testPath, String modelPath, int beamsize) throws Exception {
		InputStream modelIn = new FileInputStream(modelPath);
		TokenNameFinderModel nerModel = new TokenNameFinderModel(modelIn);
		MaxentNEREvaluator evaluator = new MaxentNEREvaluator(new NameFinderME(nerModel, featureGenerator, beamsize));
		Charset charset = Charset.forName("UTF-8");
		ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(testPath), charset);
		ObjectStream<NameSample> testStream = new NameSampleDataStream(lineStream);
		evaluator.evaluate(testStream);
		MicroFMeasure result = evaluator.getFMeasure();
		System.out.println(result.toString());
	}

	public void recognize(String testPath, String modelPath) throws Exception {
		InputStream modelIn = new FileInputStream(modelPath);
		TokenNameFinderModel nerModel = new TokenNameFinderModel(modelIn);
		Charset charset = Charset.forName("UTF-8");
		ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStream(testPath), charset);
		ObjectStream<NameSample> testStream = new NameSampleDataStream(lineStream);
		NameSample sample;
		NameFinderME finder = new NameFinderME(nerModel, featureGenerator, 3);
		while ((sample = testStream.read()) != null) {
			Span[] spans = finder.find(sample.getSentence());
			System.out.println(TextSpan.getStringNameSample(spans, sample.getSentence()));
		}
	}

	public void nFoldEvaluate(String trainingPath, int numFolds, int iterator, int cutoff) throws Exception {
		FileInputStream sampleDataIn = new FileInputStream(trainingPath);
		Charset charset = Charset.forName("UTF-8");
		ObjectStream<String> lineStream = new PlainTextByLineStream(sampleDataIn.getChannel(), charset);
		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);

		MaxentNERCrossValidator evaluator = new MaxentNERCrossValidator("en", cutoff, iterator, featureGenerator);

		evaluator.evaluate(sampleStream, numFolds);
		FMeasure result = evaluator.getFMeasure();
		System.out.println(result.toString());
		sampleDataIn.close();
	}
}
