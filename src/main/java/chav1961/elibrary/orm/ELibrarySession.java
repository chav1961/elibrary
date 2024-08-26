package chav1961.elibrary.orm;

import java.util.Properties;

import chav1961.elibrary.orm.adapters.Book2AuthorsDAO;
import chav1961.elibrary.orm.adapters.BookAuthorsDAO;
import chav1961.elibrary.orm.adapters.BookListDAO;
import chav1961.elibrary.orm.adapters.BookPublishersDAO;
import chav1961.elibrary.orm.adapters.BookSeriesDAO;

public class ELibrarySession extends HibernateSession {
	private final Book2AuthorsDAO	b2aDao;
	private final BookAuthorsDAO	baDao;
	private final BookListDAO		blDao;
	private final BookPublishersDAO	bpDao;
	private final BookSeriesDAO		bsDao;
	
	public ELibrarySession(final Properties props) {
		super(props);
		this.b2aDao = new Book2AuthorsDAO(this);
		this.baDao = new BookAuthorsDAO(this);
		this.blDao = new BookListDAO(this);
		this.bpDao = new BookPublishersDAO(this);
		this.bsDao = new BookSeriesDAO(this);
	}

	public Book2AuthorsDAO getB2aDao() {
		return b2aDao;
	}

	public BookAuthorsDAO getBaDao() {
		return baDao;
	}

	public BookListDAO getBlDao() {
		return blDao;
	}

	public BookPublishersDAO getBpDao() {
		return bpDao;
	}

	public BookSeriesDAO getBsDao() {
		return bsDao;
	}
}
