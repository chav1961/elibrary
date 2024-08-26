package chav1961.elibrary.orm.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "BOOKPUBLISHERS")
public class BookPublishers {
	 @Id
	 @Column(name = "bp_Id")
	 private long	id;

	 @Column(name = "bp_Name")
	 private String	name;

	 @Column(name = "bp_Comment")
	 private String	comment;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "BookPublishers [id=" + id + ", name=" + name + ", comment=" + comment + "]";
	}
}
