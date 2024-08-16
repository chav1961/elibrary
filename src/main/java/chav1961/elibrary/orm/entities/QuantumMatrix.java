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
@Table (name = "QCP_MATRIX")
public class QuantumMatrix {
	 @Id
	 @GeneratedValue(strategy = GenerationType.UUID)
	 @Column(name = "QM_Id")
	 private UUID		matrixId;
	 @Column(name = "QM_Owner")
	 private UUID		owner;
	 @Column(name = "QM_Schema")
	 private UUID		schema;
	 @Column(name = "QM_Created")
	 private Date		created;
	 @Column(name = "QM_LastModified")
	 private Date		lastModified;
	 @Column(name = "QM_Description")
	 private String		description;
	 @Column(name = "QM_Tags")
	 private String[]	tags;
	 @Column(name = "QM_Path")
	 private URI		path;

	 public QuantumMatrix() {
	 }

	public UUID getMatrixId() {
		return matrixId;
	}

	public void setMatrixId(UUID matrixId) {
		this.matrixId = matrixId;
	}

	public UUID getOwner() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public UUID getSchema() {
		return schema;
	}

	public void setSchema(UUID schema) {
		this.schema = schema;
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
		return "QuantumMatrix [matrixId=" + matrixId + ", owner=" + owner + ", schema=" + schema + ", created="
				+ created + ", lastModified=" + lastModified + ", description=" + description + ", tags="
				+ Arrays.toString(tags) + ", path=" + path + "]";
	}
}
