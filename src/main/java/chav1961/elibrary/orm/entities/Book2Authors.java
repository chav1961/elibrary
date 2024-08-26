package chav1961.elibrary.orm.entities;

import java.io.Serializable;
import java.sql.Date;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


@Entity
@Table (name = "BOOK2AUTHORS")
public class Book2Authors {
	@EmbeddedId
	private PrimaryKey	pk = new PrimaryKey();

	public long getBookId() {
		return pk.bookId;
	}

	public void setBookId(long bookId) {
		this.pk.bookId = bookId;
	}

	public long getAuthorId() {
		return pk.authorId;
	}

	public void setAuthorId(long authorId) {
		this.pk.authorId = authorId;
	}

	@Override
	public String toString() {
		return "Book2Authors [pk=" + pk + "]";
	}

	@Embeddable
	public static class PrimaryKey implements Serializable {
		private static final long serialVersionUID = -4680789307126158906L;
		
		@Column(name = "bl_Id")
		private long	bookId;
		
		@Column(name = "ba_Id")
		private long	authorId;

		@Override
		public int hashCode() {
			return Objects.hash(authorId, bookId);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			PrimaryKey other = (PrimaryKey) obj;
			return authorId == other.authorId && bookId == other.bookId;
		}

		@Override
		public String toString() {
			return "PrimaryKey [bookId=" + bookId + ", authorId=" + authorId + "]";
		}
	}	 
}
