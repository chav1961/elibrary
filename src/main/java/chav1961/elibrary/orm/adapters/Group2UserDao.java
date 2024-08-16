package chav1961.elibrary.orm.adapters;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;

import jakarta.persistence.criteria.Root;
import chav1961.elibrary.orm.HibernateSession;
import chav1961.elibrary.orm.entities.Group2User;
import chav1961.elibrary.orm.interfaces.OrmLCRUD;
import chav1961.elibrary.orm.metamodels.Group2User_;

public class Group2UserDao extends AbstractDAO implements OrmLCRUD<Group2User>{
	public Group2UserDao(HibernateSession session) {
		super(session);
	}

	@Override
	public List<Group2User> findAll() {
		return readList(Group2User.class);
	}

	@Override
	public Group2User findByIds(final UUID parentEntityId, final UUID childEntityId) {
		if (parentEntityId == null) {
			throw new NullPointerException("Parent entity can't be null");
		}
		else if (childEntityId == null) {
			throw new NullPointerException("Child entity can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				final HibernateCriteriaBuilder		builder = getCriteriaBuilder();
				final JpaCriteriaQuery<Group2User>	query = builder.createQuery(Group2User.class);
				final Root<Group2User>				root = query.from(Group2User.class); 
				
				query.select(root).where(builder.and(builder.equal(root.get(Group2User_.groupId), parentEntityId), builder.equal(root.get(Group2User_.userId), childEntityId)));
				
				return session.createQuery(query).uniqueResult();
			}
		}
	}

	@Override
	public List<Group2User> findByParentId(final UUID parentEntityId) {
		if (parentEntityId == null) {
			throw new NullPointerException("Parent entity can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				final HibernateCriteriaBuilder		builder = getCriteriaBuilder();
				final JpaCriteriaQuery<Group2User>	query = builder.createQuery(Group2User.class);
				final Root<Group2User>				root = query.from(Group2User.class); 
				
				query.select(root).where(builder.equal(root.get(Group2User_.groupId), parentEntityId));
				
				return session.createQuery(query).list();
			}
		}
	}

	@Override
	public List<Group2User> findByChildId(final UUID childEntityId) {
		if (childEntityId == null) {
			throw new NullPointerException("Child entity can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				final HibernateCriteriaBuilder		builder = getCriteriaBuilder();
				final JpaCriteriaQuery<Group2User>	query = builder.createQuery(Group2User.class);
				final Root<Group2User>				root = query.from(Group2User.class); 
				
				query.select(root).where(builder.equal(root.get(Group2User_.userId), childEntityId));
				
				return session.createQuery(query).list();
			}
		}
	}
	
	@Override
	public void create(final Group2User entity) {
		if (entity == null) {
			throw new NullPointerException("Group2User to create can't be null");
		}
		else {
			executeDML((s)->s.persist(entity));
		}
	}

	@Override
	public void update(final Group2User entity) {
		if (entity == null) {
			throw new NullPointerException("Group2User to update can't be null");
		}
		else {
			executeDML((s)->s.merge(entity));
		}
	}

	@Override
	public void delete(final Group2User entity) {
		if (entity == null) {
			throw new NullPointerException("Group2User to delete can't be null");
		}
		else {
			executeDML((s)->s.remove(entity));
		}
	}
}
