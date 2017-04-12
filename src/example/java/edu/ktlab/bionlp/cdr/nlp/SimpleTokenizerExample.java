package edu.ktlab.bionlp.cdr.nlp;

import org.deeplearning4j.berkeley.StringUtils;

import opennlp.tools.tokenize.SimpleTokenizer;

public class SimpleTokenizerExample {

	public static void main(String[] args) {
		String[] tokens = SimpleTokenizer.INSTANCE.tokenize("alpha-methyldopa");
		System.out.println(StringUtils.join(tokens));
	}

}
