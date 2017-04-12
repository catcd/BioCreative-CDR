package edu.ktlab.bionlp.cdr.nlp.ner;

import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderEvaluationMonitor;
import opennlp.tools.util.Span;
import opennlp.tools.util.eval.Evaluator;

public class MaxentNEREvaluator extends Evaluator<NameSample> {
	private MicroFMeasure fmeasure = new MicroFMeasure();
	private TokenNameFinder nameFinder;

	public MaxentNEREvaluator(TokenNameFinder nameFinder,
			TokenNameFinderEvaluationMonitor... listeners) {
		super(listeners);
		this.nameFinder = nameFinder;
	}

	protected NameSample processSample(NameSample reference) {

		if (reference.isClearAdaptiveDataSet()) {
			nameFinder.clearAdaptiveData();
		}

		Span predictedNames[] = nameFinder.find(reference.getSentence());
		Span references[] = reference.getNames();

		for (int i = 0; i < references.length; i++) {
			if (references[i].getType() == null) {
				references[i] = new Span(references[i].getStart(), references[i].getEnd(),
						"default");
			}
		}

		fmeasure.updateScores(references, predictedNames);

		return new NameSample(reference.getSentence(), predictedNames,
				reference.isClearAdaptiveDataSet());
	}

	public MicroFMeasure getFMeasure() {
		return fmeasure;
	}

	public static void main(String[] args) {

	}

}
