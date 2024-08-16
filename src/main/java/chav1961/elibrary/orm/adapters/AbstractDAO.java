package chav1961.elibrary.orm.adapters;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import chav1961.elibrary.orm.HibernateSession;

public abstract class AbstractDAO {
	private final HibernateSession	hs;
	
	protected AbstractDAO(final HibernateSession session) {
		if (session == null) {
			throw new NullPointerException("Hibernate session can't be null");
		}
		else {
			this.hs = session;
		}
	}
	
	protected void executeDML(final Consumer<Session> callback) {
		try(final Session	session = getSession()) {
			final Transaction t = session.beginTransaction();
			
			callback.accept(session);
			t.commit();
		}
	}
	
	protected <T> T readUnique(final UUID entityId, final Class<T> awaited, final SingularAttribute<T, ?> key) {
		try(final Session	session = getSession()) {
			final HibernateCriteriaBuilder			builder = getCriteriaBuilder();
			final JpaCriteriaQuery<T>	query = builder.createQuery(awaited);
			final Root<T>				root = query.from(awaited); 
			
			query.select(root).where(builder.equal(root.get(key), entityId));
			
			return session.createQuery(query).uniqueResult();
		}
	}

	protected <T> List<T> readList(final Class<T> awaited) {
		try(final Session	session = getSession()) {
			final HibernateCriteriaBuilder			builder = getCriteriaBuilder();
			final JpaCriteriaQuery<T>	query = builder.createQuery(awaited);
			final Root<T>				root = query.from(awaited); 
			
			query.select(root);
			
			return session.createQuery(query).list();
		}
	}
	
	protected Session getSession() {
		return getFactory().openSession();
	}

	protected HibernateCriteriaBuilder getCriteriaBuilder() {
		return getFactory().getCriteriaBuilder();
	}
	
	
	
	private SessionFactory getFactory() {
		return hs.getSessionFactory();
	}
	

	
	
}
