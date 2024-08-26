package chav1961.elibrary.orm.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "BOOKSERIES")
public class BookSeries {
	 @Id
	 @Column(name = "bs_Id")
	 private long	id;

	 @Column(name = "bs_Parent")
	 private long	parent;
	 
	 @Column(name = "bs_Name")
	 private String	name;

	 @Column(name = "bs_Comment")
	 private String	comment;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getParent() {
		return parent;
	}

	public void setParent(long parent) {
		this.parent = parent;
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
		return "BookSeries [id=" + id + ", parent=" + parent + ", name=" + name + ", comment=" + comment + "]";
	}
}
