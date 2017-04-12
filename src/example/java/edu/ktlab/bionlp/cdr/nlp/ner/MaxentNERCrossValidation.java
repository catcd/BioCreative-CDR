package edu.ktlab.bionlp.cdr.nlp.ner;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.ktlab.bionlp.cdr.nlp.ner.MaxentNEREvaluator;
import edu.ktlab.bionlp.cdr.nlp.ner.CDRNERFactory;
import edu.ktlab.bionlp.cdr.nlp.ner.MicroFMeasure;
import opennlp.model.TrainUtil;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class MaxentNERCrossValidation {
	static int numFolds = 10;
	static String encoding = "UTF-8";
	static String inputData = "data/cdr/cdr_full/cdr_full.opennlp";
	static int numIterator = 100;
	static int cutoff = 1;
	static int beamsize = 5;

	public static void main(String[] args) throws Exception {
		CDRNERFactory ner = new CDRNERFactory(
				MaxentNERFactoryExample.createFeatureGenerator());

		InputStream in = new FileInputStream(inputData);

		BufferedReader buffin = new BufferedReader(new InputStreamReader(in, encoding));
		String line = "";
		List<String> lines = new ArrayList<String>();
		while ((line = buffin.readLine()) != null) {
			if (line.trim().equals(""))
				continue;
			lines.add(line);
		}
		buffin.close();

		Collections.shuffle(lines);

		double avgPrecision = 0.0;
		double avgRecall = 0.0;
		double avgF1 = 0.0;

		double index = 0.0;
		double increment = lines.size() / (double) numFolds;
		for (int i = 0; i < numFolds; i++) {
			int start = (int) Math.round(index);
			int end = (int) Math.round(index + increment);

			StringBuffer test = new StringBuffer();
			StringBuffer train = new StringBuffer();

			for (int j = 0; j < lines.size(); j++) {
				if (j >= start && j < end)
					test.append(lines.get(j) + "\n");
				else
					train.append(lines.get(j) + "\n");
			}

			InputStream is;
			// Training
			is = new ByteArrayInputStream(train.toString().getBytes());
			//TokenNameFinderModel model = ner.trainNER(is, TrainUtil.PERCEPTRON_VALUE, numIterator, cutoff);
			TokenNameFinderModel model = ner.trainNER(is, TrainUtil.MAXENT_VALUE, numIterator, cutoff);
			
			MaxentNEREvaluator evaluator = new MaxentNEREvaluator(new NameFinderME(model,
					MaxentNERFactoryExample.createFeatureGenerator(), beamsize));

			// Testing
			is = new ByteArrayInputStream(test.toString().getBytes());
			ObjectStream<String> lineStream = new PlainTextByLineStream(is, encoding);
			ObjectStream<NameSample> testStream = new NameSampleDataStream(lineStream);
			evaluator.evaluate(testStream);
			MicroFMeasure result = evaluator.getFMeasure();
			System.out.println(result.toString());

			System.out.println("F-Measure: " + evaluator.getFMeasure().getFMeasure());
			System.out.println("Recall: " + evaluator.getFMeasure().getRecallScore());
			System.out.println("Precision: " + evaluator.getFMeasure().getPrecisionScore());

			avgPrecision += evaluator.getFMeasure().getPrecisionScore();
			avgRecall += evaluator.getFMeasure().getRecallScore();
			avgF1 += evaluator.getFMeasure().getFMeasure();

			System.out.println();
			index += increment;
		}

		System.out.println("Average F-Measure: " + (double) avgF1 / numFolds);
		System.out.println("Average Recall: " + (double) avgRecall / numFolds);
		System.out.println("Average Precision: " + (double) avgPrecision / numFolds);

	}

}
