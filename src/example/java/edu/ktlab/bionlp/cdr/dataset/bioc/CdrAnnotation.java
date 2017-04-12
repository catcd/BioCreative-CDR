package edu.ktlab.bionlp.cdr.dataset.bioc;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import bioc.BioCAnnotation;

public class CdrAnnotation extends BioCAnnotation {
	String pmid;

	public CdrAnnotation(String pmid, BioCAnnotation annotation) {
		super(annotation);
		this.pmid = pmid;
	}

	public String getType() {
		return getInfon("type");
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof CdrAnnotation)) {
			return false;
		}
		CdrAnnotation rhs = (CdrAnnotation) obj;
		return Objects.equals(pmid, rhs.pmid) && Objects.equals(getText(), rhs.getText())
				&& Objects.equals(getLocations(), rhs.getLocations());
	}

	public String getPmid() {
		return pmid;
	}

	public void setPmid(String pmid) {
		this.pmid = pmid;
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("pmid", pmid)
				.append("text", getText()).append("infons", getInfons())
				.append("locations", getLocations()).toString();
	}
}
