package chav1961.elibrary.orm.adapters;

import java.util.List;

import org.hibernate.Session;

import chav1961.elibrary.orm.HibernateSession;
import chav1961.elibrary.orm.entities.BookAuthors;
import chav1961.elibrary.orm.interfaces.OrmECRUD;

public class BookAuthorsDAO extends AbstractDAO implements OrmECRUD<BookAuthors> {
	public BookAuthorsDAO(final HibernateSession session) {
		super(session);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<BookAuthors> findAll() {
		return readList(BookAuthors.class);
	}

	@Override
	public void create(final BookAuthors entity) {
		if (entity == null) {
			throw new NullPointerException("BookAuthors to create can't be null");
		}
		else {
			entity.setId(getUniqueId());
			executeDML((s)->s.persist(entity));
		}
	}

	@Override
	public void update(final BookAuthors entity) {
		if (entity == null) {
			throw new NullPointerException("BookAuthors to update can't be null");
		}
		else {
			executeDML((s)->s.merge(entity));
		}
	}

	@Override
	public void delete(final BookAuthors entity) {
		if (entity == null) {
			throw new NullPointerException("BookAuthors to delete can't be null");
		}
		else {
			executeDML((s)->s.remove(entity));
		}
	}

	@Override
	public BookAuthors findById(final long entityId) {
		try(final Session	session = getSession()) {
			return session.get(BookAuthors.class, entityId);
		}
	}
}
