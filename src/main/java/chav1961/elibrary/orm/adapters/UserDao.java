package chav1961.elibrary.orm.adapters;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;

import chav1961.elibrary.orm.HibernateSession;
import chav1961.elibrary.orm.entities.User;
import chav1961.elibrary.orm.interfaces.OrmECRUD;

public class UserDao extends AbstractDAO implements OrmECRUD<User> {
	public UserDao(final HibernateSession session) {
		super(session);
	}
	
	@Override
	public List<User> findAll() {
		return readList(User.class);
	}
	
	@Override
	public User findById(final UUID userId) {
		if (userId == null) {
			throw new NullPointerException("User id can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				return session.get(User.class, userId);
			}
		}
	}
	
	@Override
	public void create(final User user) {
		if (user == null) {
			throw new NullPointerException("User to create can't be null");
		}
		else {
			executeDML((s)->s.persist(user));
		}
	}

	@Override
	public void update(final User user) {
		if (user == null) {
			throw new NullPointerException("User to update can't be null");
		}
		else {
			executeDML((s)->s.merge(user));
		}
	}

	@Override
	public void delete(final User user) {
		if (user == null) {
			throw new NullPointerException("User to delete can't be null");
		}
		else {
			executeDML((s)->s.remove(user));
		}
	}
}
