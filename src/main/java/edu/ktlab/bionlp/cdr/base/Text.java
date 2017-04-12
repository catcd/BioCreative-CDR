package edu.ktlab.bionlp.cdr.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Text {
	private String content;
	private String[] tokens;
	private boolean isTokenize;
	private List<TextAnnotation> annotations = new ArrayList<TextAnnotation>();

	public Text(String content) {
		this.content = content;
	}

	public Text(String content, String[] tokens, boolean isTokenize, List<TextAnnotation> annotations) {
		this.content = content;
		this.tokens = tokens;
		this.isTokenize = isTokenize;
		this.annotations = annotations;
	}

	public String getText() {
		return content;
	}

	public void setText(String content) {
		this.content = content;
	}

	public String[] getTokens() {
		return tokens;
	}

	public void setTokens(String[] tokens) {
		this.tokens = tokens;
	}

	public boolean getIsTokenize() {
		return isTokenize;
	}

	public void setTokenize(boolean isTokenize) {
		this.isTokenize = isTokenize;
	}

	public List<TextAnnotation> getAnnotations() {
		return annotations;
	}

	public boolean addAnnotations(TextAnnotation annotation) {
		return annotations.add(annotation);
	}

	public void setAnnotations(List<TextAnnotation> annotations) {
		this.annotations = annotations;
	}

	@Override
	public String toString() {
		return "Document [text=" + content + ", tokens=" + Arrays.toString(tokens) + ", tokenize="
				+ isTokenize + ", annotations=" + annotations + "]";
	}
}
