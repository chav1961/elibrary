package chav1961.elibrary.orm.adapters;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;

import chav1961.elibrary.orm.HibernateSession;
import chav1961.elibrary.orm.entities.Book2Authors;
import chav1961.elibrary.orm.interfaces.OrmLCRUD;
import chav1961.elibrary.orm.metamodels.Book2Authors_;
import jakarta.persistence.criteria.Root;

public class Book2AuthorsDAO extends AbstractDAO implements OrmLCRUD<Book2Authors> {

	public Book2AuthorsDAO(final HibernateSession session) {
		super(session);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Book2Authors> findAll() {
		return readList(Book2Authors.class);
	}

	@Override
	public void create(final Book2Authors entity) {
		if (entity == null) {
			throw new NullPointerException("Book2Authors to create can't be null");
		}
		else {
			executeDML((s)->s.persist(entity));
		}
	}

	@Override
	public void update(final Book2Authors entity) {
		if (entity == null) {
			throw new NullPointerException("Book2Authors to update can't be null");
		}
		else {
			executeDML((s)->s.merge(entity));
		}
	}

	@Override
	public void delete(final Book2Authors entity) {
		if (entity == null) {
			throw new NullPointerException("Book2Authors to delete can't be null");
		}
		else {
			executeDML((s)->s.remove(entity));
		}
	}

	@Override
	public Book2Authors findByIds(final long parentEntityId, final long childEntityId) {
		try(final Session	session = getSession()) {
			final HibernateCriteriaBuilder			builder = getCriteriaBuilder();
			final JpaCriteriaQuery<Book2Authors>	query = builder.createQuery(Book2Authors.class);
			final Root<Book2Authors>				root = query.from(Book2Authors.class); 
			
			query.select(root).where(builder.and(builder.equal(root.get(Book2Authors_.bookId), parentEntityId), builder.equal(root.get(Book2Authors_.authorId), childEntityId)));
			
			return session.createQuery(query).uniqueResult();
		}
	}

	@Override
	public List<Book2Authors> findByParentId(final long parentEntityId) {
		try(final Session	session = getSession()) {
			final HibernateCriteriaBuilder			builder = getCriteriaBuilder();
			final JpaCriteriaQuery<Book2Authors>	query = builder.createQuery(Book2Authors.class);
			final Root<Book2Authors>				root = query.from(Book2Authors.class); 
			
			query.select(root).where(builder.equal(root.get(Book2Authors_.bookId), parentEntityId));
			
			return session.createQuery(query).list();
		}
	}

	@Override
	public List<Book2Authors> findByChildId(final long childEntityId) {
		try(final Session	session = getSession()) {
			final HibernateCriteriaBuilder			builder = getCriteriaBuilder();
			final JpaCriteriaQuery<Book2Authors>	query = builder.createQuery(Book2Authors.class);
			final Root<Book2Authors>				root = query.from(Book2Authors.class); 
			
			query.select(root).where(builder.equal(root.get(Book2Authors_.authorId), childEntityId));
			
			return session.createQuery(query).list();
		}
	}
}
