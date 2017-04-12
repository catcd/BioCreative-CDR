/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.bionlp.cdr.base;

import java.util.Map;

import opennlp.tools.util.Span;

public class TextSpan extends Span {
	public TextSpan(int s, int e, String type) {
		super(s, e, type);
	}

	public TextSpan(int s, int e) {
		super(s, e);
	}

	public TextSpan(Span span, int offset) {
		super(span, offset);
	}

	public int getStartOffset(String[] tokens) {
		int startOffset = 0;
		for (int i = 0; i < getStart(); i++) {
			startOffset += tokens[i].length() + 1;
		}
		return startOffset;
	}

	public int getEndOffset(String[] tokens) {
		int endOffset = 0;
		for (int i = 0; i < getEnd(); i++) {
			endOffset += tokens[i].length() + 1;
		}
		return endOffset - 1;
	}

	public static String spanToString(Span span, String[] tokens) {
		StringBuffer cb = new StringBuffer();
		for (int ti = span.getStart(); ti < span.getEnd(); ti++) {
			cb.append(tokens[ti]).append(" ");
		}
		return cb.toString();
	}

	public static String[] spansToStrings(Span[] spans, CharSequence s) {
		String[] tokens = new String[spans.length];

		for (int si = 0, sl = spans.length; si < sl; si++) {
			tokens[si] = spans[si].getCoveredText(s).toString();
		}

		return tokens;
	}

	public static String[] spansToStrings(Span[] spans, String[] tokens) {
		String[] chunks = new String[spans.length];
		StringBuffer cb = new StringBuffer();
		for (int si = 0, sl = spans.length; si < sl; si++) {
			cb.setLength(0);
			for (int ti = spans[si].getStart(); ti < spans[si].getEnd(); ti++) {
				cb.append(tokens[ti]).append("_");
			}
			chunks[si] = cb.substring(0, cb.length() - 1);
		}
		return chunks;
	}

	public static String getStringNameSample(Span[] spans, String[] tokens) {
		String[] chunks = tokens;
		StringBuffer cb = new StringBuffer();
		for (int si = 0, sl = spans.length; si < sl; si++) {
			chunks[spans[si].getStart()] = "<START:" + spans[si].getType() + "> "
					+ chunks[spans[si].getStart()];
			chunks[spans[si].getEnd() - 1] = chunks[spans[si].getEnd() - 1] + " <END>";
		}

		for (int i = 0; i < chunks.length; i++)
			cb.append(chunks[i] + " ");
		return cb.substring(0, cb.length() - 1);
	}

	public static String getStringAnnotated(Span[] spans, String[] tokens) {
		String[] chunks = tokens;
		StringBuffer cb = new StringBuffer();
		for (int si = 0, sl = spans.length; si < sl; si++) {
			chunks[spans[si].getStart()] = "<" + spans[si].getType() + "> "
					+ chunks[spans[si].getStart()];
			chunks[spans[si].getEnd() - 1] = chunks[spans[si].getEnd() - 1] + " </"
					+ spans[si].getType() + ">";
		}

		for (int i = 0; i < chunks.length; i++)
			cb.append(chunks[i] + " ");
		return cb.substring(0, cb.length() - 1);
	}

	public static String getStringAnnotated(TupleToken tuple) {
		String[] chunks = tuple.getTokens();
		StringBuffer cb = new StringBuffer();
		Map<String, TextSpan[]> annotation = tuple.getAnnotation();

		for (String label : annotation.keySet())
			for (int si = 0, sl = annotation.get(label).length; si < sl; si++) {
				chunks[annotation.get(label)[si].getStart()] = "<" + label + ":"
						+ annotation.get(label)[si].getType() + ">"
						+ chunks[annotation.get(label)[si].getStart()];
				chunks[annotation.get(label)[si].getEnd() - 1] = chunks[annotation.get(label)[si]
						.getEnd() - 1]
						+ "</"
						+ label
						+ ":"
						+ annotation.get(label)[si].getType()
						+ ">";
			}

		for (int i = 0; i < chunks.length; i++)
			cb.append(chunks[i] + " ");
		return cb.substring(0, cb.length() - 1);
	}
}
