package edu.ktlab.bionlp.cdr.nlp.utils.jeniatagger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.jmcejuela.bio.jenia.common.Sentence;
import com.jmcejuela.bio.jenia.common.Token;

import edu.ktlab.bionlp.cdr.nlp.utils.jeniatagger.Jenia;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

public class JeniaTaggerExample {
	public static void main(String[] args) {
		String modelPath = DependencyParser.DEFAULT_MODEL;
		DependencyParser parser = DependencyParser.loadFromModelFile(modelPath);

		Jenia.setModelsPath("models/genia");
		String text = "Naloxone reverses the antihypertensive effect of clonidine";
		Sentence sentence = Jenia.analyzeAll(text, true);
		System.out.println(sentence);
		List<TaggedWord> tws = new ArrayList<TaggedWord>();
		for (Token token : sentence) {
			TaggedWord tw = new TaggedWord(token.text, token.pos);
			tws.add(tw);
		}
			
		GrammaticalStructure gs = parser.predict(tws);
		Collection<TypedDependency> typeDependencies = gs.typedDependencies();
		for (TypedDependency dep : typeDependencies)
			System.out.println(dep);
	}
}
