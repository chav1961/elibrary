package chav1961.elibrary.orm.entities;

import java.awt.Image;
import java.net.URI;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "BOOKLIST")
public class BookList {
	 @Id
	 @Column(name = "bl_Id")
	 private long	id;

	 @Column(name = "bl_Parent")
	 private long	parent;
	 
	 @Column(name = "bl_Code")
	 private URI	code;

	 @Column(name = "bs_Id")
	 private long	seriesId;

	 @Column(name = "bs_Seq")
	 private String	seriesSeq;
	 
	 @Column(name = "bl_Title")
	 private String	title;

	 @Column(name = "bl_Year")
	 private int	year;

	 @Column(name = "bp_Id")
	 private long	publisherId;

	 @Column(name = "bl_Comment")
	 private String	comment;

	 @Column(name = "bl_Tags")
	 private String	tags;

	 @Column(name = "bl_Image")
	 private Image	image;

	 @Column(name = "bl_Image")
	 private String	mimeType;

	 @Column(name = "bl_Content")
	 private byte[]	content;
	 
	 @Column(name = "bl_Page")
	 private int	page;

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

	public URI getCode() {
		return code;
	}

	public void setCode(URI code) {
		this.code = code;
	}

	public long getSeriesId() {
		return seriesId;
	}

	public void setSeriesId(long seriesId) {
		this.seriesId = seriesId;
	}

	public String getSeriesSeq() {
		return seriesSeq;
	}

	public void setSeriesSeq(String seriesSeq) {
		this.seriesSeq = seriesSeq;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public long getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(long publisherId) {
		this.publisherId = publisherId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	@Override
	public String toString() {
		return "BookList [id=" + id + ", parent=" + parent + ", code=" + code + ", seriesId=" + seriesId
				+ ", seriesSeq=" + seriesSeq + ", title=" + title + ", year=" + year + ", publisherId=" + publisherId
				+ ", comment=" + comment + ", tags=" + tags + ", mimeType=" + mimeType + ", page=" + page + "]";
	}
}
