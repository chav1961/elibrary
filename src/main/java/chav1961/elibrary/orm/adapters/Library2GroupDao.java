package chav1961.elibrary.orm.adapters;


import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;

import jakarta.persistence.criteria.Root;
import chav1961.elibrary.orm.HibernateSession;
import chav1961.elibrary.orm.entities.Library2Group;
import chav1961.elibrary.orm.interfaces.OrmLCRUD;
import chav1961.elibrary.orm.metamodels.Library2Group_;

public class Library2GroupDao extends AbstractDAO implements OrmLCRUD<Library2Group>{
	public Library2GroupDao(final HibernateSession session) {
		super(session);
	}

	@Override
	public List<Library2Group> findAll() {
		return readList(Library2Group.class);
	}

	@Override
	public Library2Group findByIds(final UUID parentEntityId, final UUID childEntityId) {
		if (parentEntityId == null) {
			throw new NullPointerException("Parent entity can't be null");
		}
		else if (childEntityId == null) {
			throw new NullPointerException("Child entity can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				final HibernateCriteriaBuilder		builder = getCriteriaBuilder();
				final JpaCriteriaQuery<Library2Group>	query = builder.createQuery(Library2Group.class);
				final Root<Library2Group>				root = query.from(Library2Group.class); 
				
				query.select(root).where(builder.and(builder.equal(root.get(Library2Group_.groupId), parentEntityId), builder.equal(root.get(Library2Group_.libraryId), childEntityId)));
				
				return session.createQuery(query).uniqueResult();
			}
		}
	}

	@Override
	public List<Library2Group> findByParentId(final UUID parentEntityId) {
		if (parentEntityId == null) {
			throw new NullPointerException("Parent entity can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				final HibernateCriteriaBuilder			builder = getCriteriaBuilder();
				final JpaCriteriaQuery<Library2Group>	query = builder.createQuery(Library2Group.class);
				final Root<Library2Group>				root = query.from(Library2Group.class); 
				
				query.select(root).where(builder.equal(root.get(Library2Group_.groupId), parentEntityId));
				
				return session.createQuery(query).list();
			}
		}
	}

	@Override
	public List<Library2Group> findByChildId(final UUID childEntityId) {
		if (childEntityId == null) {
			throw new NullPointerException("Child entity can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				final HibernateCriteriaBuilder		builder = getCriteriaBuilder();
				final JpaCriteriaQuery<Library2Group>	query = builder.createQuery(Library2Group.class);
				final Root<Library2Group>				root = query.from(Library2Group.class); 
				
				query.select(root).where(builder.equal(root.get(Library2Group_.libraryId), childEntityId));
				
				return session.createQuery(query).list();
			}
		}
	}

	@Override
	public void create(final Library2Group entity) {
		if (entity == null) {
			throw new NullPointerException("Library2Group to create can't be null");
		}
		else {
			executeDML((s)->s.persist(entity));
		}
	}

	@Override
	public void update(final Library2Group entity) {
		if (entity == null) {
			throw new NullPointerException("Library2Group to update can't be null");
		}
		else {
			executeDML((s)->s.merge(entity));
		}
	}

	@Override
	public void delete(final Library2Group entity) {
		if (entity == null) {
			throw new NullPointerException("Library2Group to delete can't be null");
		}
		else {
			executeDML((s)->s.remove(entity));
		}
	}
}
