package chav1961.elibrary.orm.adapters;

import java.util.List;

import org.hibernate.Session;

import chav1961.elibrary.orm.HibernateSession;
import chav1961.elibrary.orm.entities.BookPublishers;
import chav1961.elibrary.orm.interfaces.OrmECRUD;

public class BookPublishersDAO extends AbstractDAO implements OrmECRUD<BookPublishers> {
	public BookPublishersDAO(HibernateSession session) {
		super(session);
	}

	@Override
	public List<BookPublishers> findAll() {
		return readList(BookPublishers.class);
	}

	@Override
	public void create(final BookPublishers entity) {
		if (entity == null) {
			throw new NullPointerException("BookPublishers to create can't be null");
		}
		else {
			entity.setId(getUniqueId());
			executeDML((s)->s.persist(entity));
		}
	}

	@Override
	public void update(final BookPublishers entity) {
		if (entity == null) {
			throw new NullPointerException("BookPublishers to update can't be null");
		}
		else {
			executeDML((s)->s.merge(entity));
		}
	}

	@Override
	public void delete(final BookPublishers entity) {
		if (entity == null) {
			throw new NullPointerException("BookPublishers to delete can't be null");
		}
		else {
			executeDML((s)->s.remove(entity));
		}
	}

	@Override
	public BookPublishers findById(long entityId) {
		try(final Session	session = getSession()) {
			return session.get(BookPublishers.class, entityId);
		}
	}
}
