package edu.ktlab.bionlp.cdr.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeReader;
import edu.stanford.nlp.trees.UniversalEnglishGrammaticalStructure;

public class DependencyHelper {
	public static Tree convertTree(String dtree) {
		TreeReader reader = new PennTreeReader(new StringReader(dtree));
		Tree tree;
		try {
			tree = reader.readTree();
			reader.close();
			return tree;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static GrammaticalStructure convertGrammaticalStructure(String dtree) {
		try {
			return new UniversalEnglishGrammaticalStructure(convertTree(dtree));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static SemanticGraph convertSemanticGraph(String dtree) {
		try {
			return SemanticGraphFactory.makeFromTree(convertTree(dtree));	
		} catch (Exception e) {
			return null;
		}		
	}

	public static List<String> transform(String[] tokens) {
		List<String> toks = new ArrayList<String>();

		for (String tok : tokens) {
			toks.add(escapeToken(tok));
		}

		return toks;
	}

	private static String escapeToken(String token) {
		if (token.equals("("))
			return "-LRB-";
		if (token.equals(")"))
			return "-RRB-";
		if (token.equals("["))
			return "-LSB-";
		if (token.equals("]"))
			return "-RSB-";
		if (token.equals("{"))
			return "-LCB-";
		if (token.equals("}"))
			return "-RCB-";

		return token;
	}
}
