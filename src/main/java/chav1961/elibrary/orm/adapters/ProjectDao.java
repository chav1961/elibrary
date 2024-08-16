package chav1961.elibrary.orm.adapters;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;

import chav1961.elibrary.orm.HibernateSession;
import chav1961.elibrary.orm.entities.Project;
import chav1961.elibrary.orm.interfaces.OrmECRUD;

public class ProjectDao extends AbstractDAO implements OrmECRUD<Project> {

	public ProjectDao(HibernateSession session) {
		super(session);
	}

	@Override
	public List<Project> findAll() {
		return readList(Project.class);
	}

	@Override
	public Project findById(final UUID projectId) {
		if (projectId == null) {
			throw new NullPointerException("Project id can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				return session.get(Project.class, projectId);
			}
		}
	}

	@Override
	public void create(final Project entity) {
		if (entity == null) {
			throw new NullPointerException("Project to create can't be null");
		}
		else {
			executeDML((s)->s.persist(entity));
		}
	}

	@Override
	public void update(final Project entity) {
		if (entity == null) {
			throw new NullPointerException("Project to update can't be null");
		}
		else {
			executeDML((s)->s.merge(entity));
		}
	}

	@Override
	public void delete(final Project entity) {
		if (entity == null) {
			throw new NullPointerException("Project to delete can't be null");
		}
		else {
			executeDML((s)->s.remove(entity));
		}
	}
}
