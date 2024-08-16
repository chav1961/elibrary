package chav1961.elibrary.orm.entities;

import java.net.URI;
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
@Table (name = "QCP_SCHEME")
public class QuantumScheme {
	 @Id
	 @GeneratedValue(strategy = GenerationType.UUID)
	 @Column(name = "QS_Id")
	 private UUID		schemeId;
	 @Column(name = "QS_Owner")
	 private UUID		owner;
	 @Column(name = "QS_Matrix")
	 private UUID		matrix;
	 @Column(name = "QS_Created")
	 private Date		created;
	 @Column(name = "QS_LastModified")
	 private Date		lastModified;
	 @Column(name = "QS_Description")
	 private String		description;
	 @Column(name = "QS_Tags")
	 private String[]	tags;
	 @Column(name = "QS_Path")
	 private URI		path;
	 
	 public QuantumScheme() {
		 super();
	 }

	public UUID getSchemeId() {
		return schemeId;
	}

	public void setSchemeId(UUID schemeId) {
		this.schemeId = schemeId;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public UUID getMatrix() {
		return matrix;
	}

	public void setMatrix(UUID matrix) {
		this.matrix = matrix;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
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

	public URI getPath() {
		return path;
	}

	public void setPath(URI path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "QuantumScheme [schemeId=" + schemeId + ", owner=" + owner + ", matrix=" + matrix + ", created="
				+ created + ", lastModified=" + lastModified + ", description=" + description + ", tags="
				+ Arrays.toString(tags) + ", path=" + path + "]";
	}
}
