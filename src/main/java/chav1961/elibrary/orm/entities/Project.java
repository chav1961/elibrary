package chav1961.elibrary.orm.entities;

import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "QCP_PROJECT")
public class Project {
	 @Id
	 @GeneratedValue(strategy = GenerationType.UUID)
	 @Column(name = "PR_Id")
	 private UUID		projectId;
	 @Column(name = "PR_Name")
	 private String		projectName;
	 @Column(name = "PR_Description")
	 private String		description;
	 @Column(name = "PR_Tags")
	 private String[]	tags;
	 @Column(name = "PR_Shared")
	 private boolean	shared;
	 @Column(name = "PR_Path")
	 private URI		path;
	 
	 public Project() {
	 }

	public UUID getProjectId() {
		return projectId;
	}

	public void setProjectId(UUID projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
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

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public URI getPath() {
		return path;
	}

	public void setPath(URI path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "Project [projectId=" + projectId + ", projectName=" + projectName + ", description=" + description
				+ ", tags=" + Arrays.toString(tags) + ", shared=" + shared + ", path=" + path + "]";
	}
}
