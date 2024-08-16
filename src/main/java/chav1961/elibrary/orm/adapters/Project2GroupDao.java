package chav1961.elibrary.orm.adapters;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;

import jakarta.persistence.criteria.Root;
import chav1961.elibrary.orm.HibernateSession;
import chav1961.elibrary.orm.entities.Project2Group;
import chav1961.elibrary.orm.interfaces.OrmLCRUD;
import chav1961.elibrary.orm.metamodels.Project2Group_;

public class Project2GroupDao extends AbstractDAO implements OrmLCRUD<Project2Group>{
	public Project2GroupDao(final HibernateSession session) {
		super(session);
	}

	@Override
	public List<Project2Group> findAll() {
		return readList(Project2Group.class);
	}

	@Override
	public Project2Group findByIds(final UUID parentEntityId, final UUID childEntityId) {
		if (parentEntityId == null) {
			throw new NullPointerException("Parent entity can't be null");
		}
		else if (childEntityId == null) {
			throw new NullPointerException("Child entity can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				final HibernateCriteriaBuilder			builder = getCriteriaBuilder();
				final JpaCriteriaQuery<Project2Group>	query = builder.createQuery(Project2Group.class);
				final Root<Project2Group>				root = query.from(Project2Group.class); 
				
				query.select(root).where(builder.and(builder.equal(root.get(Project2Group_.groupId), parentEntityId), builder.equal(root.get(Project2Group_.projectId), childEntityId)));
				
				return session.createQuery(query).uniqueResult();
			}
		}
	}

	@Override
	public List<Project2Group> findByParentId(final UUID parentEntityId) {
		if (parentEntityId == null) {
			throw new NullPointerException("Parent entity can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				final HibernateCriteriaBuilder			builder = getCriteriaBuilder();
				final JpaCriteriaQuery<Project2Group>	query = builder.createQuery(Project2Group.class);
				final Root<Project2Group>				root = query.from(Project2Group.class); 
				
				query.select(root).where(builder.equal(root.get(Project2Group_.groupId), parentEntityId));
				
				return session.createQuery(query).list();
			}
		}
	}

	@Override
	public List<Project2Group> findByChildId(UUID childEntityId) {
		if (childEntityId == null) {
			throw new NullPointerException("Child entity can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				final HibernateCriteriaBuilder			builder = getCriteriaBuilder();
				final JpaCriteriaQuery<Project2Group>	query = builder.createQuery(Project2Group.class);
				final Root<Project2Group>				root = query.from(Project2Group.class); 
				
				query.select(root).where(builder.equal(root.get(Project2Group_.projectId), childEntityId));
				
				return session.createQuery(query).list();
			}
		}
	}
	
	@Override
	public void create(final Project2Group entity) {
		if (entity == null) {
			throw new NullPointerException("Project2Group to create can't be null");
		}
		else {
			executeDML((s)->s.persist(entity));
		}
	}

	@Override
	public void update(final Project2Group entity) {
		if (entity == null) {
			throw new NullPointerException("Project2Group to update can't be null");
		}
		else {
			executeDML((s)->s.merge(entity));
		}
	}

	@Override
	public void delete(final Project2Group entity) {
		if (entity == null) {
			throw new NullPointerException("Project2Group to delete can't be null");
		}
		else {
			executeDML((s)->s.remove(entity));
		}
	}

}
