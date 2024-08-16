package chav1961.elibrary.orm.entities;

import java.io.Serializable;
import java.sql.Date;
import java.util.Arrays;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table (name = "QCP_LIBRARY2GROUP")
public class Library2Group {
	 @EmbeddedId
	 private PrimaryKey	pk = new PrimaryKey();
	 @Column(name = "L2G_Description")
	 private String		description;
	 @Column(name = "L2G_Tags")
	 private String[]	tags;
	 @Column(name = "L2G_Added")
	 private Date		added;
	 @Column(name = "L2G_IsLocked")
	 private boolean	isLocked;

	 public Library2Group() {
	 }

	public UUID getGroupId() {
		return pk.groupId;
	}

	public void setGroupId(UUID groupId) {
		this.pk.groupId = groupId;
	}

	public UUID getLibraryId() {
		return pk.libraryId;
	}

	public void setLibraryId(UUID libraryId) {
		this.pk.libraryId = libraryId;
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

	public Date getAdded() {
		return added;
	}

	public void setAdded(Date added) {
		this.added = added;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	@Override
	public String toString() {
		return "Library2Group [pk=" + pk + ", description=" + description
				+ ", tags=" + Arrays.toString(tags) + ", added=" + added + ", isLocked=" + isLocked + "]";
	}

	@Embeddable
	public static class PrimaryKey implements Serializable {
		private static final long serialVersionUID = -4680789307126158906L;
		
		@Column(name = "GR_Id")
		private UUID		groupId;
		@Column(name = "LB_Id")
		private UUID		libraryId;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
			result = prime * result + ((libraryId == null) ? 0 : libraryId.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			PrimaryKey other = (PrimaryKey) obj;
			if (groupId == null) {
				if (other.groupId != null) return false;
			} else if (!groupId.equals(other.groupId)) return false;
			if (libraryId == null) {
				if (other.libraryId != null) return false;
			} else if (!libraryId.equals(other.libraryId)) return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "PrimaryKey [groupId=" + groupId + ", libraryId=" + libraryId + "]";
		}
	}
}
