package chav1961.elibrary.orm.adapters;

import java.util.List;

import org.hibernate.Session;

import chav1961.elibrary.orm.HibernateSession;
import chav1961.elibrary.orm.entities.BookPublishers;
import chav1961.elibrary.orm.entities.BookSeries;
import chav1961.elibrary.orm.interfaces.OrmECRUD;

public class BookSeriesDAO extends AbstractDAO implements OrmECRUD<BookSeries> {

	public BookSeriesDAO(final HibernateSession session) {
		super(session);
	}

	@Override
	public List<BookSeries> findAll() {
		return readList(BookSeries.class);
	}

	@Override
	public void create(final BookSeries entity) {
		if (entity == null) {
			throw new NullPointerException("BookSeries to create can't be null");
		}
		else {
			entity.setId(getUniqueId());
			executeDML((s)->s.persist(entity));
		}
	}

	@Override
	public void update(final BookSeries entity) {
		if (entity == null) {
			throw new NullPointerException("BookSeries to update can't be null");
		}
		else {
			executeDML((s)->s.merge(entity));
		}
	}

	@Override
	public void delete(final BookSeries entity) {
		if (entity == null) {
			throw new NullPointerException("BookSeries to delete can't be null");
		}
		else {
			executeDML((s)->s.remove(entity));
		}
	}

	@Override
	public BookSeries findById(long entityId) {
		try(final Session	session = getSession()) {
			return session.get(BookSeries.class, entityId);
		}
	}
}
