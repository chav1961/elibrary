package chav1961.elibrary.orm.adapters;


import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;

import jakarta.persistence.criteria.Root;
import chav1961.elibrary.orm.HibernateSession;
import chav1961.elibrary.orm.entities.Library2Scheme;
import chav1961.elibrary.orm.interfaces.OrmLCRUD;
import chav1961.elibrary.orm.metamodels.Library2Scheme_;

public class Library2SchemeDao extends AbstractDAO implements OrmLCRUD<Library2Scheme>{
	public Library2SchemeDao(final HibernateSession session) {
		super(session);
	}

	@Override
	public List<Library2Scheme> findAll() {
		return readList(Library2Scheme.class);
	}

	@Override
	public Library2Scheme findByIds(final UUID parentEntityId, final UUID childEntityId) {
		if (parentEntityId == null) {
			throw new NullPointerException("Parent entity can't be null");
		}
		else if (childEntityId == null) {
			throw new NullPointerException("Child entity can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				final HibernateCriteriaBuilder		builder = getCriteriaBuilder();
				final JpaCriteriaQuery<Library2Scheme>	query = builder.createQuery(Library2Scheme.class);
				final Root<Library2Scheme>				root = query.from(Library2Scheme.class); 
				
				query.select(root).where(builder.and(builder.equal(root.get(Library2Scheme_.libraryId), parentEntityId), builder.equal(root.get(Library2Scheme_.schemeId), childEntityId)));
				
				return session.createQuery(query).uniqueResult();
			}
		}
	}

	@Override
	public List<Library2Scheme> findByParentId(final UUID parentEntityId) {
		if (parentEntityId == null) {
			throw new NullPointerException("Parent entity can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				final HibernateCriteriaBuilder			builder = getCriteriaBuilder();
				final JpaCriteriaQuery<Library2Scheme>	query = builder.createQuery(Library2Scheme.class);
				final Root<Library2Scheme>				root = query.from(Library2Scheme.class); 
				
				query.select(root).where(builder.equal(root.get(Library2Scheme_.libraryId), parentEntityId));
				
				return session.createQuery(query).list();
			}
		}
	}

	@Override
	public List<Library2Scheme> findByChildId(final UUID childEntityId) {
		if (childEntityId == null) {
			throw new NullPointerException("Child entity can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				final HibernateCriteriaBuilder			builder = getCriteriaBuilder();
				final JpaCriteriaQuery<Library2Scheme>	query = builder.createQuery(Library2Scheme.class);
				final Root<Library2Scheme>				root = query.from(Library2Scheme.class); 
				
				query.select(root).where(builder.equal(root.get(Library2Scheme_.schemeId), childEntityId));
				
				return session.createQuery(query).list();
			}
		}
	}


	@Override
	public void create(final Library2Scheme entity) {
		if (entity == null) {
			throw new NullPointerException("Library2Scheme to create can't be null");
		}
		else {
			executeDML((s)->s.persist(entity));
		}
	}

	@Override
	public void update(final Library2Scheme entity) {
		if (entity == null) {
			throw new NullPointerException("Library2Scheme to update can't be null");
		}
		else {
			executeDML((s)->s.merge(entity));
		}
	}

	@Override
	public void delete(final Library2Scheme entity) {
		if (entity == null) {
			throw new NullPointerException("Library2Scheme to delete can't be null");
		}
		else {
			executeDML((s)->s.remove(entity));
		}
	}

}
