package chav1961.elibrary.orm.adapters;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;

import chav1961.elibrary.orm.HibernateSession;
import chav1961.elibrary.orm.entities.Group;
import chav1961.elibrary.orm.interfaces.OrmECRUD;

public class GroupDao extends AbstractDAO implements OrmECRUD<Group> {
	public GroupDao(final HibernateSession session) {
		super(session);
	}

	@Override
	public List<Group> findAll() {
		return readList(Group.class);
	}

	@Override
	public Group findById(final UUID groupId) {
		if (groupId == null) {
			throw new NullPointerException("Group id can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				return session.get(Group.class, groupId);
			}
		}
	}

	@Override
	public void create(final Group entity) {
		if (entity == null) {
			throw new NullPointerException("Group to create can't be null");
		}
		else {
			executeDML((s)->s.persist(entity));
		}
	}

	@Override
	public void update(final Group entity) {
		if (entity == null) {
			throw new NullPointerException("Group to update can't be null");
		}
		else {
			executeDML((s)->s.merge(entity));
		}
	}

	@Override
	public void delete(Group entity) {
		if (entity == null) {
			throw new NullPointerException("Group to delete can't be null");
		}
		else {
			executeDML((s)->s.remove(entity));
		}
	}
}
