package chav1961.elibrary.orm.entities;

import java.io.Serializable;
import java.net.URI;
import java.sql.Date;
import java.util.Arrays;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table (name = "QCP_LIBRARY2SCHEME")
public class Library2Scheme {
	 @EmbeddedId
	 private PrimaryKey	pk = new PrimaryKey();
	 @Column(name = "L2Q_Name")
	 private String		itemName;
	 @Column(name = "L2Q_Description")
	 private String		description;
	 @Column(name = "L2Q_Tags")
	 private String[]	tags;
	 @Column(name = "L2Q_Icon")
	 private URI		icon;
	 @Column(name = "L2Q_Added")
	 private Date		added;
	 @Column(name = "L2Q_Parameters")
	 private String		parameters;
	 @Column(name = "L2Q_S2MRules")
	 private String		matrixRules;
	 @Column(name = "L2Q_S2SRules")
	 private String		schemeRules;
	 
	 public Library2Scheme() {
	 }

	public UUID getLibraryId() {
		return pk.libraryId;
	}

	public void setLibraryId(UUID libraryId) {
		this.pk.libraryId = libraryId;
	}

	public UUID getSchemeId() {
		return pk.schemeId;
	}

	public void setSchemeId(UUID schemeId) {
		this.pk.schemeId = schemeId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}

	public URI getIcon() {
		return icon;
	}

	public void setIcon(URI icon) {
		this.icon = icon;
	}

	public Date getAdded() {
		return added;
	}

	public void setAdded(Date added) {
		this.added = added;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getMatrixRules() {
		return matrixRules;
	}

	public void setMatrixRules(String matrixRules) {
		this.matrixRules = matrixRules;
	}

	public String getSchemeRules() {
		return schemeRules;
	}

	public void setSchemeRules(String schemeRules) {
		this.schemeRules = schemeRules;
	}

	@Override
	public String toString() {
		return "Library2Scheme [primaryKey=" + pk + ", itemName=" + itemName
				+ ", description=" + description + ", tags=" + Arrays.toString(tags) + ", icon=" + icon + ", added="
				+ added + ", parameters=" + parameters + ", matrixRules=" + matrixRules + ", schemeRules=" + schemeRules
				+ "]";
	}
	
	public static class PrimaryKey implements Serializable {
		private static final long serialVersionUID = -1050545925152336115L;
		
		@Column(name = "LB_Id")
		private UUID		libraryId;
		@Column(name = "QS_Id")
		private UUID		schemeId;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((libraryId == null) ? 0 : libraryId.hashCode());
			result = prime * result + ((schemeId == null) ? 0 : schemeId.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			PrimaryKey other = (PrimaryKey) obj;
			if (libraryId == null) {
				if (other.libraryId != null) return false;
			} else if (!libraryId.equals(other.libraryId)) return false;
			if (schemeId == null) {
				if (other.schemeId != null) return false;
			} else if (!schemeId.equals(other.schemeId)) return false;
			return true;
		}

		@Override
		public String toString() {
			return "PrimaryKey [libraryId=" + libraryId + ", schemeId=" + schemeId + "]";
		}
	}
}
