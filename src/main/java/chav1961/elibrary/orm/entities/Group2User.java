package chav1961.elibrary.orm.entities;

import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table (name = "QCP_GROUP2USER")
public class Group2User {
	 @EmbeddedId
	 private PrimaryKey	pk = new PrimaryKey();
	 @Column(name = "G2U_Added")
	 private Date		added;
	 @Column(name = "G2U_IsLocked")
	 private boolean	isLocked;

	 public Group2User() {
	 }

	public UUID getGroupId() {
		return pk.groupId;
	}

	public void setGroupId(UUID groupId) {
		this.pk.groupId = groupId;
	}

	public UUID getUserId() {
		return pk.userId;
	}

	public void setUserId(UUID userId) {
		this.pk.userId = userId;
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
		return "Group2User [pk=" + pk + ", added=" + added + ", isLocked=" + isLocked
				+ "]";
	}

	@Embeddable
	public static class PrimaryKey implements Serializable {
		private static final long serialVersionUID = -7679083325032768013L;
		
		@Column(name = "GR_Id")
		private UUID		groupId;
		@Column(name = "US_Id")
		private UUID		userId;
		 
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
			result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
			if (userId == null) {
				if (other.userId != null) return false;
			} else if (!userId.equals(other.userId)) return false;
			return true;
		}

		@Override
		public String toString() {
			return "PrimaryKey [groupId=" + groupId + ", userId=" + userId + "]";
		}
	}
}
