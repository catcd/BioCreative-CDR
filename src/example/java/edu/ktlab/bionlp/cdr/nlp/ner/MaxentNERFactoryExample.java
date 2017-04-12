package edu.ktlab.bionlp.cdr.nlp.ner;

import opennlp.model.TrainUtil;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.StringList;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.featuregen.CachedFeatureGenerator;
import opennlp.tools.util.featuregen.DictionaryFeatureGenerator;
import opennlp.tools.util.featuregen.OutcomePriorFeatureGenerator;
import opennlp.tools.util.featuregen.PrefixFeatureGenerator;
import opennlp.tools.util.featuregen.PreviousMapFeatureGenerator;
import opennlp.tools.util.featuregen.SentenceFeatureGenerator;
import opennlp.tools.util.featuregen.SuffixFeatureGenerator;
import opennlp.tools.util.featuregen.TokenClassFeatureGenerator;
import opennlp.tools.util.featuregen.TokenFeatureGenerator;
import opennlp.tools.util.featuregen.WindowFeatureGenerator;

import edu.ktlab.bionlp.cdr.nlp.ner.features.JeniaFeatureGenerator;
import edu.ktlab.bionlp.cdr.nlp.ner.features.NgramTokenFeatureGenerator;
import edu.ktlab.bionlp.cdr.nlp.ner.features.WordLengthFeatureGenerator;
import edu.ktlab.bionlp.cdr.nlp.utils.jeniatagger.Jenia;
import edu.ktlab.bionlp.cdr.util.FileHelper;

public class MaxentNERFactoryExample {
	static Dictionary loadDictionary(String file) throws Exception {
		String[] lines = FileHelper.readFileAsLines(file);
		Dictionary dict = new Dictionary(false);
		for (String line : lines) {
			dict.put(new StringList(SimpleTokenizer.INSTANCE.tokenize(line)));
		}
		return dict;
	}

	public static AdaptiveFeatureGenerator createFeatureGenerator() throws Exception {
		Jenia.setModelsPath("models/genia");
		AdaptiveFeatureGenerator featureGenerator = new CachedFeatureGenerator(new AdaptiveFeatureGenerator[] {
				new WindowFeatureGenerator(new TokenClassFeatureGenerator(true), 2, 2),
				new WindowFeatureGenerator(new TokenFeatureGenerator(true), 2, 2),
				new WindowFeatureGenerator(new NgramTokenFeatureGenerator(true, 2, 2), 2, 2),
				new WindowFeatureGenerator(new NgramTokenFeatureGenerator(true, 3, 3), 2, 2),
				new DictionaryFeatureGenerator("CTD_CD", loadDictionary("models/ner/ctd_ched.gzip")),
				new DictionaryFeatureGenerator("CTD_DS", loadDictionary("models/ner/ctd_dis.gzip")),
				new WindowFeatureGenerator(new JeniaFeatureGenerator(), 2, 2), new PrefixFeatureGenerator(),
				new SuffixFeatureGenerator(), new WordLengthFeatureGenerator(), new OutcomePriorFeatureGenerator(),
				new PreviousMapFeatureGenerator(), new SentenceFeatureGenerator(true, false) });
		return featureGenerator;
	}

	public static void main(String[] args) throws Exception {
		CDRNERFactory ner = new CDRNERFactory(createFeatureGenerator());
		// ner.trainNER("data/cdr/cdr_train/cdr_train.opennlp", "models/ner/cdr_train.perc.model", TrainUtil.PERCEPTRON_VALUE, 100, 1);
		ner.trainNER("data/cdr/cdr_full/cdr_full.opennlp", "models/ner/cdr_full.perc.model", TrainUtil.PERCEPTRON_VALUE, 100, 1);
		// ner.evaluatebyExactMatching("data/cdr/cdr_dev/cdr_dev.opennlp", "models/ner/cdr_full.perc.model", 5);
	}
}
