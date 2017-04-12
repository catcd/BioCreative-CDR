/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.bionlp.cdr.nlp.matcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.ktlab.bionlp.cdr.base.TextSpan;

public class LongestMatching {
	Map<String, String> dictionary = new HashMap<String, String>();

	public LongestMatching(Map<String, String> dictionary) {
		this.dictionary = dictionary;
	}

	public TextSpan[] tagging(String[] tokens, int max_key_size, boolean caseSensitive) {
		ArrayList<TextSpan> names = new ArrayList<TextSpan>();

		int N = tokens.length;
		if (max_key_size == -1) {
			max_key_size = N;
		}
		int i = 0;
		while (i < N) {
			boolean tagged = false;
			int j = Math.min(i + max_key_size, N);
			while (j > i) {
				String[] literal_tokens = Arrays.copyOfRange(tokens, i, j);
				String literal = join(literal_tokens, " ");
				String tag;
				if (caseSensitive)
					tag = (String) dictionary.get(literal.toLowerCase());
				else
					tag = (String) dictionary.get(literal);

				if (tag != null) {
					TextSpan token = new TextSpan(i, j, tag);
					names.add(token);
					i = j;
					tagged = true;
				} else {
					j -= 1;
				}
			}
			if (!tagged) {
				i += 1;
			}
		}

		return names.toArray(new TextSpan[names.size()]);
	}

	private static String join(String[] sentence, String separator) {
		String result = "";
		for (int i = 0; i < sentence.length; i++) {
			String word = sentence[i];
			result += word + separator;
		}
		return result.trim();
	}
}
