package edu.ktlab.bionlp.cdr.nlp.ner;

import java.util.Scanner;

import edu.ktlab.bionlp.cdr.nlp.ner.CDRNERRecognizer;
import edu.ktlab.bionlp.cdr.nlp.tokenizer.TokenizerMESingleton;

public class MaxentNERRecognizerExample {
	public static void main(String[] args) throws Exception {
		CDRNERRecognizer nerFinder = new CDRNERRecognizer("models/ner/vntrans.model",
				MaxentNERFactoryExample.createFeatureGenerator());
		System.out.print("Enter your sentence: ");
		Scanner scan = new Scanner(System.in);

		while (scan.hasNext()) {
			String text = scan.nextLine();
			if (text.equals("exit")) {
				break;
			}
			String[] tokens = TokenizerMESingleton.getInstance().tokenize(text);
			String output = nerFinder.recognize(tokens);
			System.out.println(output);
			System.out.print("Enter your sentence: ");
		}
		scan.close();
	}

}
