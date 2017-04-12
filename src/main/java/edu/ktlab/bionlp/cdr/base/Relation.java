package edu.ktlab.bionlp.cdr.base;

public class Relation {
	private String id;
	private String chemicalID;
	private String diseaseID;

	public Relation() {
	}

	public Relation(String id, String chemicalID, String diseaseID) {
		this.id = id;
		this.chemicalID = chemicalID;
		this.diseaseID = diseaseID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChemicalID() {
		return chemicalID;
	}

	public void setChemicalID(String chemicalID) {
		this.chemicalID = chemicalID;
	}

	public String getDiseaseID() {
		return diseaseID;
	}

	public void setDiseaseID(String diseaseID) {
		this.diseaseID = diseaseID;
	}

	@Override
	public String toString() {
		return "Relation [id=" + id + ", chemicalID=" + chemicalID + ", diseaseID=" + diseaseID + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chemicalID == null) ? 0 : chemicalID.hashCode());
		result = prime * result + ((diseaseID == null) ? 0 : diseaseID.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Relation other = (Relation) obj;
		if (chemicalID == null) {
			if (other.chemicalID != null)
				return false;
		} else if (!chemicalID.equals(other.chemicalID))
			return false;
		if (diseaseID == null) {
			if (other.diseaseID != null)
				return false;
		} else if (!diseaseID.equals(other.diseaseID))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public boolean equals(String chemicalID, String diseaseID) {
		return this.chemicalID.equals(chemicalID) && this.diseaseID.equals(diseaseID);
	}

	public boolean equals(String relation) {
		return (this.chemicalID + " " + this.diseaseID).equals(relation);
	}
}
