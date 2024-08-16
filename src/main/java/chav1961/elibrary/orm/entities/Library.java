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
@Table (name = "QCP_LIBRARY")
public class Library {
	 @Id
	 @GeneratedValue(strategy = GenerationType.UUID)
	 @Column(name = "LB_Id")
	 private UUID		libraryId;
	 @Column(name = "LB_Owner")
	 private UUID		owner;
	 @Column(name = "LB_Name")
	 private String		libraryName;
	 @Column(name = "LB_Description")
	 private String		description;
	 @Column(name = "LB_Tags")
	 private String[]	tags;
	 @Column(name = "LB_Created")
	 private Date		created;
	 
	 public Library() {
	 }

	public UUID getLibraryId() {
		return libraryId;
	}

	public void setLibraryId(UUID libraryId) {
		this.libraryId = libraryId;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public String getLibraryName() {
		return libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
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
		return "Library [libraryId=" + libraryId + ", owner=" + owner + ", libraryName=" + libraryName + ", description="
				+ description + ", tags=" + Arrays.toString(tags) + ", created=" + created + "]";
	}
}
