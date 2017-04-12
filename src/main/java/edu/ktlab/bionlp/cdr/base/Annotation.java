package edu.ktlab.bionlp.cdr.base;

import java.util.ArrayList;
import java.util.List;

public class Annotation {
	private int startBaseOffset;
	private int endBaseOffset;
	private String content;
	private String type;
	private String reference;
	private List<Token> tokens;

	public Annotation() {
		tokens = new ArrayList<Token>();
	}

	public int getStartBaseOffset() {
		return startBaseOffset;
	}

	public void setStartBaseOffset(int startBaseOffset) {
		this.startBaseOffset = startBaseOffset;
	}

	public int getEndBaseOffset() {
		return endBaseOffset;
	}

	public void setEndBaseOffset(int endBaseOffset) {
		this.endBaseOffset = endBaseOffset;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public String[] getStrTokens(boolean lowercase) {
		String[] strTokens = new String[tokens.size()];
		for (int i = 0; i < strTokens.length; i++)
			strTokens[i] = (lowercase) ? tokens.get(i).getContent().toLowerCase() : tokens.get(i).getContent();
		return strTokens;
	}

	public void setTokens(List<Token> tokens) {
		this.tokens = tokens;
	}

	public void addToken(Token token) {
		if (containTokenByBaseOffset(token))
			tokens.add(token);
	}

	public void addTokens(List<Token> tokens) {
		for (Token token : tokens)
			addToken(token);
	}

	public boolean inner(Annotation ann) {
		if (startBaseOffset >= ann.getStartBaseOffset() && endBaseOffset <= ann.getEndBaseOffset())
			return true;

		return false;
	}

	public boolean containTokenByBaseOffset(Token token) {
		if (startBaseOffset <= token.getStartBaseOffset() && endBaseOffset >= token.getEndBaseOffset())
			return true;

		return false;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Annotation other = (Annotation) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (endBaseOffset != other.endBaseOffset)
			return false;
		if (reference == null) {
			if (other.reference != null)
				return false;
		} else if (!reference.equals(other.reference))
			return false;
		if (startBaseOffset != other.startBaseOffset)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Annotation [startOffset=" + startBaseOffset + ", endOffset=" + endBaseOffset + ", content=" + content
				+ ", type=" + type + ", reference=" + reference + ", tokens=" + tokens + "]";
	}

	public String getTokenizedLabel2OpenNLPFormat() {
		String taggedText = " <START:" + type + "> ";

		for (Token token : tokens) {
			taggedText += token.getContent() + " ";
		}

		taggedText += "<END>";

		return taggedText;
	}
}
