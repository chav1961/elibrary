package chav1961.elibrary.orm.adapters;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;

import chav1961.elibrary.orm.HibernateSession;
import chav1961.elibrary.orm.entities.Library;
import chav1961.elibrary.orm.interfaces.OrmECRUD;

public class LibraryDao extends AbstractDAO implements OrmECRUD<Library>{
	public LibraryDao(final HibernateSession session) {
		super(session);
	}

	@Override
	public List<Library> findAll() {
		return readList(Library.class);
	}

	@Override
	public Library findById(final UUID libraryId) {
		if (libraryId == null) {
			throw new NullPointerException("Library id can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				return session.get(Library.class, libraryId);
			}
		}
	}

	@Override
	public void create(final Library entity) {
		if (entity == null) {
			throw new NullPointerException("Library to create can't be null");
		}
		else {
			executeDML((s)->s.persist(entity));
		}
	}

	@Override
	public void update(final Library entity) {
		if (entity == null) {
			throw new NullPointerException("Library to update can't be null");
		}
		else {
			executeDML((s)->s.merge(entity));
		}
	}

	@Override
	public void delete(final Library entity) {
		if (entity == null) {
			throw new NullPointerException("Library to delete can't be null");
		}
		else {
			executeDML((s)->s.remove(entity));
		}
	}
}
