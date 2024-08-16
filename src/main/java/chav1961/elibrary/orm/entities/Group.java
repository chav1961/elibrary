package chav1961.elibrary.orm.entities;

import java.sql.Date;
import java.util.Arrays;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "QCP_GROUP")
public class Group {
	 @Id
	 @GeneratedValue(strategy = GenerationType.UUID)
	 @Column(name = "GR_Id")
	 private UUID		groupId;
	 @Column(name = "GR_Owner")
	 private UUID		owner;
	 @Column(name = "GR_Name")
	 private String		groupName;
	 @Column(name = "GR_Description")
	 private String		description;
	 @Column(name = "GR_Tags")
	 private String[]	tags;
	 @Column(name = "GR_Created")
	 private Date		created;

	 public Group() {
	 }

	public UUID getGroupId() {
		return groupId;
	}

	public void setUserId(UUID groupId) {
		this.groupId = groupId;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public String toString() {
		return "Group [groupId=" + groupId + ", owner=" + owner + ", groupName=" + groupName + ", description="
				+ description + ", tags=" + Arrays.toString(tags) + ", created=" + created + "]";
	}
}
