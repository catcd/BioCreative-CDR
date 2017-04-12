package edu.ktlab.bionlp.cdr.nlp.parser;

import java.util.ArrayList;
import java.util.List;

import edu.emory.clir.clearnlp.component.AbstractComponent;
import edu.emory.clir.clearnlp.component.mode.dep.DEPConfiguration;
import edu.emory.clir.clearnlp.component.utils.GlobalLexica;
import edu.emory.clir.clearnlp.component.utils.NLPUtils;
import edu.emory.clir.clearnlp.dependency.DEPTree;
import edu.emory.clir.clearnlp.util.lang.TLanguage;
import edu.stanford.nlp.util.StringUtils;

public class ClearNLPParserExample {
	public static AbstractComponent[] getBioinformaticsModels(TLanguage language) {
		// initialize global lexicons
		List<String> paths = new ArrayList<>();
		paths.add("brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1.txt.xz");
		GlobalLexica.initDistributionalSemanticsWords(paths);

		// initialize statistical models
		AbstractComponent morph = NLPUtils.getMPAnalyzer(language);
		AbstractComponent pos = NLPUtils.getPOSTagger(language, "bioinformatics-en-pos.xz");
		AbstractComponent dep = NLPUtils.getDEPParser(language, "bioinformatics-en-dep.xz",
				new DEPConfiguration("root"));

		return new AbstractComponent[] { pos, morph, dep };
	}

	public static void main(String[] args) {
		AbstractComponent[] components = getBioinformaticsModels(TLanguage.ENGLISH);

		String text = "Based on clinical data, indicating that chloroacetaldehyde ( CAA ) is an important metabolite of oxazaphosphorine cytostatics , an experimental study was carried out in order to elucidate the role of CAA in the development of hemorrhagic cystitis";
		List<String> tokens = StringUtils.split(text);
		DEPTree tree;

		tree = new DEPTree(tokens);

		for (AbstractComponent component : components)
			component.process(tree);

		System.out.println(tree.toString());
	}

}
