package edu.ktlab.bionlp.cdr.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tartarus.snowball.ext.EnglishStemmer;

import edu.stanford.nlp.util.Characters;
import edu.stanford.nlp.util.StringUtils;

public class StringHelper {
	public static List<String> findStringRegex(String text, String rule) {
		List<String> matchList = new ArrayList<String>();
		
		Pattern regex = Pattern.compile(rule, Pattern.DOTALL);
		Matcher regexMatcher = regex.matcher(text);
		while (regexMatcher.find()) {
			matchList.add(regexMatcher.group());
		}

		return matchList;
	}

	public static String wordFeatures(String word) {
		if (StringUtils.isPunct(word))
			return "PUNCT";
		if (StringUtils.isNumeric(word))
			return "NUM";
		if (StringUtils.isAcronym(word))
			return "ALLCAP";
		if (StringUtils.isCapitalized(word))
			return "CAPT";
		return null;
	}

	public static String stemmerFeature(String token) {
		EnglishStemmer stemmer = new EnglishStemmer();
		stemmer.setCurrent(token);
		stemmer.stem();
		return stemmer.getCurrent();
	}

	public static ArrayList<String> gramWord(int n, String[] text, String separator, String prefix) {
		ArrayList<String> grams = new ArrayList<String>();
		for (int i = 0; i < text.length; i++) {
			if (i >= text.length - n + 1)
				break;
			String[] segs = new String[n];
			for (int j = i; j < i + n; j++) {
				segs[j - i] = text[j];

			}
			grams.add(prefix + StringUtils.join(segs, separator));
		}
		return grams;
	}

	public static ArrayList<String> gramCharacter(int n, String text, String separator, String prefix) {
		ArrayList<String> grams = new ArrayList<String>();
		Character[] characters = Characters.asCharacterArray(text);
		for (int i = 0; i < characters.length; i++) {
			if (i >= characters.length - n + 1)
				break;
			Character[] segs = new Character[n];
			for (int j = i; j < i + n; j++) {
				segs[j - i] = characters[j];
			}
			grams.add(prefix + StringUtils.join(segs, separator));
		}
		return grams;
	}

	public static ArrayList<String> ngramWord(int n, String[] text, String separator, String prefix) {
		ArrayList<String> ngrams = new ArrayList<String>();
		for (int i = 1; i <= n; i++) {
			ngrams.addAll(gramWord(i, text, separator, prefix));
		}
		return ngrams;
	}

	public static ArrayList<String> ngramWord(int min, int max, String[] text, String separator, String prefix) {
		ArrayList<String> ngrams = new ArrayList<String>();
		for (int i = min; i <= max; i++) {
			ngrams.addAll(gramWord(i, text, separator, prefix));
		}
		return ngrams;
	}

	public static ArrayList<String> ngramCharacter(int n, String text, String separator, String prefix) {
		ArrayList<String> ngrams = new ArrayList<String>();
		for (int i = 1; i <= n; i++) {
			ngrams.addAll(gramCharacter(i, text, separator, prefix));
		}
		return ngrams;
	}

	public static ArrayList<String> ngramCharacter(int min, int max, String text, String separator, String prefix) {
		ArrayList<String> ngrams = new ArrayList<String>();
		for (int i = min; i <= max; i++) {
			ngrams.addAll(gramCharacter(i, text, separator, prefix));
		}
		return ngrams;
	}
}
