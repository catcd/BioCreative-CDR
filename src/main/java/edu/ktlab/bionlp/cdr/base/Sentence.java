package edu.ktlab.bionlp.cdr.base;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class Sentence {
	private int startOffset;
	private int endOffset;
	private String content;
	private List<Annotation> annotations;
	private List<Token> tokens;
	private String deptree;

	public Sentence() {
		annotations = new ArrayList<Annotation>();
		tokens = new ArrayList<Token>();
	}

	public String getDeptree() {
		return deptree;
	}

	public void setDeptree(String deptree) {
		this.deptree = deptree;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}

	public boolean addAnnotation(Annotation annotation) {
		annotation.addTokens(tokens);
		return annotations.add(annotation);
	}

	public List<Annotation> getAnnotation(String reference) {
		List<Annotation> anns = Lists.newArrayList();

		for (Annotation annotation : annotations)
			if (annotation.getReference().equals(reference))
				anns.add(annotation);

		return anns;
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public String[] getStringTokens() {
		String[] strTokens = new String[tokens.size()];

		for (int i = 0; i < tokens.size(); i++)
			strTokens[i] = tokens.get(i).getContent();

		return strTokens;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	public void setTokens(String[] strTokens) {
		int offset = 0;
		for (int i = 0; i < strTokens.length; i++) {
			Token token = new Token();
			token.setId(i);
			token.setContent(strTokens[i]);
			int start = startOffset + content.indexOf(strTokens[i], offset);
			int end = start + strTokens[i].length();
			offset = end - startOffset;
			token.setStartBaseOffset(start);
			token.setEndBaseOffset(end);
			tokens.add(token);
		}
	}

	public boolean containAnnotation(Annotation annotation) {
		boolean check = false;
		if (annotation.getStartBaseOffset() >= startOffset && annotation.getEndBaseOffset() <= endOffset)
			return true;

		return check;
	}

	public boolean containRelation(Relation relation) {
		boolean checkNode1 = false;
		boolean checkNode2 = false;
		for (Annotation ann : annotations) {
			if (ann.getReference().contains(relation.getChemicalID()))
				checkNode1 = true;

			if (ann.getReference().contains(relation.getDiseaseID()))
				checkNode2 = true;
		}

		return checkNode1 && checkNode2;
	}

	public String getContent2OpenNLPFormat() {
		String taggedText = content;
		for (int i = annotations.size() - 1; i >= 0; i--) {
			taggedText = taggedText.substring(0, annotations.get(i).getStartBaseOffset() - startOffset)
					+ " <START:"
					+ annotations.get(i).getType()
					+ "> "
					+ taggedText.substring(annotations.get(i).getStartBaseOffset() - startOffset, annotations.get(i)
							.getEndBaseOffset() - startOffset) + " <END> "
					+ taggedText.substring(annotations.get(i).getEndBaseOffset() - startOffset, taggedText.length());
		}

		return taggedText.replace("  ", " ");
	}

	public String getTokenized2OpenNLPFormat() {
		String taggedText = "";

		List<Object> tokenizeds = new ArrayList<Object>();
		for (Token token : tokens) {
			boolean flContain = false;
			for (Annotation ann : annotations) {
				if (ann.containTokenByBaseOffset(token)) {
					flContain = true;
					if (!tokenizeds.contains(ann))
						tokenizeds.add(ann);
					break;
				}
			}
			if (!flContain)
				tokenizeds.add(token);
		}

		for (Object tokenized : tokenizeds) {
			if (tokenized instanceof Annotation)
				taggedText += ((Annotation) tokenized).getTokenizedLabel2OpenNLPFormat() + " ";
			if (tokenized instanceof Token)
				taggedText += ((Token) tokenized).getContent() + " ";
		}

		return taggedText.trim().replace("  ", " ");
	}

	public String getContentTokenized() {
		String[] strTokens = new String[tokens.size()];
		for (int i = 0; i < tokens.size(); i++) {
			strTokens[i] = tokens.get(i).getContent();
		}

		return StringUtils.join(strTokens, " ");
	}

	@Override
	public String toString() {
		return "Sentence [startOffset=" + startOffset + ", endOffset=" + endOffset + ", content=" + content
				+ ", annotations=" + annotations + ", tokens=" + tokens + "]";
	}
}