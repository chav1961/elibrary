package chav1961.elibrary.orm.adapters;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;

import chav1961.elibrary.orm.HibernateSession;
import chav1961.elibrary.orm.entities.QuantumScheme;
import chav1961.elibrary.orm.interfaces.OrmECRUD;

public class QuantumSchemeDao extends AbstractDAO implements OrmECRUD<QuantumScheme> {

	public QuantumSchemeDao(final HibernateSession session) {
		super(session);
	}

	@Override
	public List<QuantumScheme> findAll() {
		return readList(QuantumScheme.class);
	}

	@Override
	public QuantumScheme findById(final UUID schemeId) {
		if (schemeId == null) {
			throw new NullPointerException("Quantum scheme id can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				return session.get(QuantumScheme.class, schemeId);
			}
		}
	}

	@Override
	public void create(final QuantumScheme entity) {
		if (entity == null) {
			throw new NullPointerException("Quantum scheme to create can't be null");
		}
		else {
			executeDML((s)->s.persist(entity));
		}
	}

	@Override
	public void update(final QuantumScheme entity) {
		if (entity == null) {
			throw new NullPointerException("Quantum scheme to update can't be null");
		}
		else {
			executeDML((s)->s.merge(entity));
		}
	}

	@Override
	public void delete(final QuantumScheme entity) {
		if (entity == null) {
			throw new NullPointerException("Quantim scheme to delete can't be null");
		}
		else {
			executeDML((s)->s.remove(entity));
		}
	}
}
