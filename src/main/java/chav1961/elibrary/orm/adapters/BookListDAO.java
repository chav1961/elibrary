package chav1961.elibrary.orm.adapters;

import java.util.List;

import org.hibernate.Session;

import chav1961.elibrary.orm.HibernateSession;
import chav1961.elibrary.orm.entities.BookList;
import chav1961.elibrary.orm.interfaces.OrmECRUD;

public class BookListDAO extends AbstractDAO implements OrmECRUD<BookList> {
	public BookListDAO(final HibernateSession session) {
		super(session);
	}

	@Override
	public List<BookList> findAll() {
		return readList(BookList.class);
	}

	@Override
	public void create(final BookList entity) {
		if (entity == null) {
			throw new NullPointerException("BookList to create can't be null");
		}
		else {
			entity.setId(getUniqueId());
			executeDML((s)->s.persist(entity));
		}
	}

	@Override
	public void update(final BookList entity) {
		if (entity == null) {
			throw new NullPointerException("BookList to update can't be null");
		}
		else {
			executeDML((s)->s.merge(entity));
		}
	}

	@Override
	public void delete(final BookList entity) {
		if (entity == null) {
			throw new NullPointerException("BookList to delete can't be null");
		}
		else {
			executeDML((s)->s.remove(entity));
		}
	}

	@Override
	public BookList findById(final long entityId) {
		try(final Session	session = getSession()) {
			return session.get(BookList.class, entityId);
		}
	}
}
