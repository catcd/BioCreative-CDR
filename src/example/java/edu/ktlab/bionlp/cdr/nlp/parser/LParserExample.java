package edu.ktlab.bionlp.cdr.nlp.parser;

import java.io.StringReader;

import opennlp.tools.tokenize.SimpleTokenizer;
import edu.ktlab.bionlp.cdr.util.DependencyHelper;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.Options;
import edu.stanford.nlp.pipeline.DefaultPaths;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.LabeledScoredTreeFactory;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreeReader;

public class LParserExample {

	public static void main(String[] args) throws Exception {
		LexicalizedParser parser = LexicalizedParser
				.getParserFromFile(DefaultPaths.DEFAULT_PARSER_MODEL, new Options());
		String text = "A (B)";
		String[] tokens = SimpleTokenizer.INSTANCE.tokenize(text);
		
		Tree parseTree = parser.parseStrings(DependencyHelper.transform(tokens));
	    
		TreePrint treePrint = new TreePrint("typedDependencies");
		treePrint.printTree(parseTree);
		
		TreeReader reader = new PennTreeReader(new StringReader(parseTree.toString()), new LabeledScoredTreeFactory(), null);
		treePrint.printTree(reader.readTree());
		
		treePrint.printTree(Tree.valueOf(parseTree.toString()));
		
		GrammaticalStructure grammarStructure = DependencyHelper.convertGrammaticalStructure(parseTree.toString());
		SemanticGraph semanticGraph = DependencyHelper.convertSemanticGraph(parseTree.toString());
	}
}
