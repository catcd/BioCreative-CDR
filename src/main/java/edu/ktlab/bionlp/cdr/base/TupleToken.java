/*******************************************************************************
 * Copyright (c) 2013 Mai-Vu Tran.
 ******************************************************************************/
package edu.ktlab.bionlp.cdr.base;

import java.util.HashMap;
import java.util.Map;

public class TupleToken {
	private String[] tokens;
	private Map<String, TextSpan[]> annotation = new HashMap<String, TextSpan[]>();

	public TupleToken(String[] tokens) {
		this.tokens = tokens;
	}

	public TupleToken(String[] tokens, Map<String, TextSpan[]> annotation) {
		this.tokens = tokens;
		this.annotation = annotation;
	}

	public void put(String label, TextSpan[] tagged) {
		annotation.put(label, tagged);
	}

	public String[] getTokens() {
		return tokens;
	}

	public void setTokens(String[] tokens) {
		this.tokens = tokens;
	}

	public Map<String, TextSpan[]> getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Map<String, TextSpan[]> annotation) {
		this.annotation = annotation;
	}
}
