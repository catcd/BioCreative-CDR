package edu.ktlab.bionlp.cdr.util;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class ExtractAbbreviation {
	private static final char delimiter = '\t';

	private boolean isValidShortForm(String str) {
		return (hasLetter(str) && (Character.isLetterOrDigit(str.charAt(0)) || (str.charAt(0) == '(')));
	}

	private boolean hasLetter(String str) {
		for (int i = 0; i < str.length(); i++)
			if (Character.isLetter(str.charAt(i)))
				return true;
		return false;
	}

	private boolean hasCapital(String str) {
		for (int i = 0; i < str.length(); i++)
			if (Character.isUpperCase(str.charAt(i)))
				return true;
		return false;
	}

	public class AbbreviationPair {
		private String shortForm;
		private String longForm;

		public AbbreviationPair(String shortForm, String longForm) {
			if (shortForm == null)
				throw new IllegalArgumentException("Short form cannot be null");
			if (longForm == null)
				throw new IllegalArgumentException("Long form cannot be null");
			this.shortForm = shortForm;
			this.longForm = longForm;
		}

		public String getLongForm() {
			return longForm;
		}

		public String getShortForm() {
			return shortForm;
		}

		@Override
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + longForm.hashCode();
			result = PRIME * result + shortForm.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final AbbreviationPair other = (AbbreviationPair) obj;
			if (!longForm.equals(other.longForm))
				return false;
			if (!shortForm.equals(other.shortForm))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return shortForm + delimiter + longForm;
		}
	}

	public Set<AbbreviationPair> extractAbbrPairs(String text) {
		return extractAbbrPairs(new StringReader(text));
	}

	private Set<AbbreviationPair> extractAbbrPairs(Reader inFile) {

		String str, tmpStr, longForm = "", shortForm = "";
		String currSentence = "";
		int openParenIndex, closeParenIndex = -1, sentenceEnd, newCloseParenIndex, tmpIndex = -1;
		boolean newParagraph = true;
		StringTokenizer shortTokenizer;
		Set<AbbreviationPair> pairs = new HashSet<AbbreviationPair>();

		try {
			BufferedReader fin = new BufferedReader(inFile);
			while ((str = fin.readLine()) != null) {
				if (str.length() == 0 || newParagraph && !Character.isUpperCase(str.charAt(0))) {
					currSentence = "";
					newParagraph = true;
					continue;
				}
				newParagraph = false;
				str += " ";
				currSentence += str;
				openParenIndex = currSentence.indexOf(" (");
				do {
					if (openParenIndex > -1)
						openParenIndex++;
					sentenceEnd = Math.max(currSentence.lastIndexOf(". "), currSentence.lastIndexOf(", "));
					if ((openParenIndex == -1) && (sentenceEnd == -1)) {
						// Do nothing
					} else if (openParenIndex == -1) {
						currSentence = currSentence.substring(sentenceEnd + 2);
					} else if ((closeParenIndex = currSentence.indexOf(')', openParenIndex)) > -1) {
						sentenceEnd = Math.max(currSentence.lastIndexOf(". ", openParenIndex),
								currSentence.lastIndexOf(", ", openParenIndex));
						if (sentenceEnd == -1)
							sentenceEnd = -2;
						longForm = currSentence.substring(sentenceEnd + 2, openParenIndex);
						shortForm = currSentence.substring(openParenIndex + 1, closeParenIndex);
					}
					if (shortForm.length() > 0 || longForm.length() > 0) {
						if (longForm.length() > 1) {
							if ((shortForm.indexOf('(') > -1)
									&& ((newCloseParenIndex = currSentence.indexOf(')', closeParenIndex + 1)) > -1)) {
								shortForm = currSentence.substring(openParenIndex + 1, newCloseParenIndex);
								closeParenIndex = newCloseParenIndex;
							}
							if ((tmpIndex = shortForm.indexOf(", ")) > -1)
								shortForm = shortForm.substring(0, tmpIndex);
							if ((tmpIndex = shortForm.indexOf("; ")) > -1)
								shortForm = shortForm.substring(0, tmpIndex);
							shortTokenizer = new StringTokenizer(shortForm);
							if (shortTokenizer.countTokens() > 2 || shortForm.length() > longForm.length()) {
								// Long form in ( )
								tmpIndex = currSentence.lastIndexOf(" ", openParenIndex - 2);
								tmpStr = currSentence.substring(tmpIndex + 1, openParenIndex - 1);
								longForm = shortForm;
								shortForm = tmpStr;
								if (!hasCapital(shortForm))
									shortForm = "";
							}
							if (isValidShortForm(shortForm)) {
								AbbreviationPair pair = extractAbbrPair(shortForm.trim(), longForm.trim());
								if (pair != null)
									pairs.add(pair);
							}
						}
						currSentence = currSentence.substring(closeParenIndex + 1);
					} else if (openParenIndex > -1) {
						if ((currSentence.length() - openParenIndex) > 200)
							// Matching close paren was not found
							currSentence = currSentence.substring(openParenIndex + 1);
						break; // Read next line
					}
					shortForm = "";
					longForm = "";
				} while ((openParenIndex = currSentence.indexOf(" (")) > -1);
			}
			fin.close();
		} catch (Exception ioe) {
			ioe.printStackTrace();
			System.out.println(currSentence);
			System.out.println(tmpIndex);
		}
		return pairs;
	}

	private String findBestLongForm(String shortForm, String longForm) {
		int sIndex;
		int lIndex;
		char currChar;

		sIndex = shortForm.length() - 1;
		lIndex = longForm.length() - 1;
		for (; sIndex >= 0; sIndex--) {
			currChar = Character.toLowerCase(shortForm.charAt(sIndex));
			if (!Character.isLetterOrDigit(currChar))
				continue;
			while (((lIndex >= 0) && (Character.toLowerCase(longForm.charAt(lIndex)) != currChar))
					|| ((sIndex == 0) && (lIndex > 0) && (Character.isLetterOrDigit(longForm.charAt(lIndex - 1)))))
				lIndex--;
			if (lIndex < 0)
				return null;
			lIndex--;
		}
		lIndex = longForm.lastIndexOf(" ", lIndex) + 1;
		return longForm.substring(lIndex);
	}

	private AbbreviationPair extractAbbrPair(String shortForm, String longForm) {
		String bestLongForm;
		StringTokenizer tokenizer;
		int longFormSize, shortFormSize;

		// if (shortForm.length() == 1)
		// return null;
		bestLongForm = findBestLongForm(shortForm, longForm);
		if (bestLongForm == null)
			return null;
		tokenizer = new StringTokenizer(bestLongForm, " \t\n\r\f-");
		longFormSize = tokenizer.countTokens();
		shortFormSize = shortForm.length();
		for (int i = shortFormSize - 1; i >= 0; i--)
			if (!Character.isLetterOrDigit(shortForm.charAt(i)))
				shortFormSize--;
		if (bestLongForm.length() < shortForm.length() || bestLongForm.indexOf(shortForm + " ") > -1
				|| bestLongForm.endsWith(shortForm) || longFormSize > shortFormSize * 2
				|| longFormSize > shortFormSize + 5 || shortFormSize > 10)
			return null;

		// System.out.println(shortForm + delimiter + bestLongForm);
		return new AbbreviationPair(shortForm, bestLongForm);
	}

	public static void main(String[] args) throws Exception {
		ExtractAbbreviation extractAbbrev = new ExtractAbbreviation();
		String test = "The cardiomyopathy (CM) produced by the anticancer drug doxorubicin (DXR) (Adriamycin) provides a unique opportunity to analyze dose-effect and structure-function relationships during development of myocardial disease. We measured the degree of morphologic damage by ultrastructural examination of endomyocardial biopsy and the degree of performance abnormally by right heart catheterization in patients receiving DXR. Morphologic damage was variable but was proportional to the total cumulative DXR dose between 100 and 600 mg/m2. Performance abnormalities correlated weakly with dose, exhibited a curvilinear relationship, and had a \"threshold\" for expression. Catheterization abnormalities correlated well with morphologic damage (r = 0.57 to 0.78) in a subgroup of patients in whom exercise hemodynamics were measured, and this relationship also exhibited a curvilinear, threshold configuration. In DXR-CM myocardial damage is proportional to the degree of cytotoxic insult (DXR dose) while myocardial function is preserved until a critical dose or degree of damage is reached, after which myocardial performance deteriorates rapidly.";
		System.out.println(extractAbbrev.extractAbbrPairs(test));
	}
}
