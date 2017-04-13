package edu.ktlab.bionlp.cdr.base;

public class Token {
	private int id;
	private String content;
	private String longForm;
	private int startBaseOffset;
	private int endBaseOffset;
	private int startTokenizedOffset;
	private int endTokenizedOffset;

	public Token() {
	}

	public Token(int id, String content, int startBaseOffset, int endBaseOffset) {
		this.id = id;
		this.content = content;
		this.startBaseOffset = startBaseOffset;
		this.endBaseOffset = endBaseOffset;
	}

	public Token(int id, String content, int startBaseOffset, int endBaseOffset, int startTokenizedOffset,
			int endTokenizedOffset) {
		this.id = id;
		this.content = content;
		this.startBaseOffset = startBaseOffset;
		this.endBaseOffset = endBaseOffset;
		this.startTokenizedOffset = startTokenizedOffset;
		this.endTokenizedOffset = endTokenizedOffset;
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

	public int getStartTokenizedOffset() {
		return startTokenizedOffset;
	}

	public void setStartTokenizedOffset(int startTokenizedOffset) {
		this.startTokenizedOffset = startTokenizedOffset;
	}

	public int getEndTokenizedOffset() {
		return endTokenizedOffset;
	}

	public void setEndTokenizedOffset(int endTokenizedOffset) {
		this.endTokenizedOffset = endTokenizedOffset;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLongForm() {
		return longForm;
	}

	public void setLongForm(String longForm) {
		this.longForm = longForm;
	}

	@Override
	public String toString() {
		return "{" +
				"'id'=" + id +
				", 'content'='" + content + '\'' +
				", 'longForm'='" + longForm + '\'' +
				", 'startBaseOffset'=" + startBaseOffset +
				", 'endBaseOffset'=" + endBaseOffset +
				", 'startTokenizedOffset'=" + startTokenizedOffset +
				", 'endTokenizedOffset'=" + endTokenizedOffset +
				'}';
	}
}
