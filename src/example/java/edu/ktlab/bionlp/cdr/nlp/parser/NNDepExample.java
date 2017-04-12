package edu.ktlab.bionlp.cdr.nlp.parser;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.Trees;
import edu.stanford.nlp.trees.TypedDependency;

public class NNDepExample {

	public static void main(String[] args) {
		String modelPath = DependencyParser.DEFAULT_MODEL;
		String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";

		String text = "Naloxone reverses the antihypertensive effect of clonidine";

		MaxentTagger tagger = new MaxentTagger(taggerPath);
		DependencyParser parser = DependencyParser.loadFromModelFile(modelPath);

		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
		for (List<HasWord> sentence : tokenizer) {
			List<TaggedWord> tagged = tagger.tagSentence(sentence);
			System.out.println(tagged);
			GrammaticalStructure gs = parser.predict(tagged);
			Collection<TypedDependency> typeDependencies = gs.typedDependencies();
			for (TypedDependency dep : typeDependencies)
				System.out.println(dep);
		}
	}

	public static TreeGraphNode getFirstNode(GrammaticalStructure grammaticalStructure) {
		return (TreeGraphNode) Trees.leaves(grammaticalStructure.root()).get(0);
	}

}
