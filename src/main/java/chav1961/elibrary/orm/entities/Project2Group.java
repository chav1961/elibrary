package chav1961.elibrary.orm.entities;

import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table (name = "QCP_PROJECT2GROUP")
public class Project2Group {
	 @EmbeddedId
	 private PrimaryKey	pk = new PrimaryKey();
	 @Column(name = "P2G_Description")
	 private String		description;
	 @Column(name = "P2G_Added")
	 private Date		added;
	 @Column(name = "P2G_IsLocked")
	 private boolean	isLocked;
	 @Column(name = "P2G_ReadOnly")
	 private boolean	readOnly;

	 public Project2Group() {
	 }

	public UUID getGroupId() {
		return pk.groupId;
	}

	public void setGroupId(UUID groupId) {
		this.pk.groupId = groupId;
	}

	public UUID getProjectId() {
		return pk.projectId;
	}

	public void setProjectId(UUID projectId) {
		this.pk.projectId = projectId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public String toString() {
		return "Project2Group [primaryKey=" + pk + ", description=" + description
				+ ", added=" + added + ", isLocked=" + isLocked + ", readOnly=" + readOnly + "]";
	}
	
	public static class PrimaryKey implements Serializable {
		private static final long serialVersionUID = 3716930719012087947L;
		
		@Column(name = "GR_Id")
		private UUID		groupId;
		@Column(name = "PR_Id")
		private UUID		projectId;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
			result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
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
			if (projectId == null) {
				if (other.projectId != null) return false;
			} else if (!projectId.equals(other.projectId)) return false;
			return true;
		}
		@Override
		public String toString() {
			return "PrimaryKey [groupId=" + groupId + ", projectId=" + projectId + "]";
		}
	}
}
