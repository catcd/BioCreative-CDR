package edu.ktlab.bionlp.cdr.nlp.utils.jeniatagger;

import org.apache.commons.lang3.StringUtils;

import com.jmcejuela.bio.jenia.JeniaTagger;
import com.jmcejuela.bio.jenia.common.Sentence;

public class Jenia extends JeniaTagger {
	public static Sentence analyzeAll(final String[] tokens, boolean dont_tokenize) {
		try {
			return analyzeAll(StringUtils.join(tokens, " "), dont_tokenize);
		} catch (Exception e) {
			return null;
		}

	}
}
