package chav1961.elibrary.orm.adapters;

import java.util.List;
import java.util.UUID;

import org.hibernate.Session;

import chav1961.elibrary.orm.HibernateSession;
import chav1961.elibrary.orm.entities.QuantumMatrix;
import chav1961.elibrary.orm.interfaces.OrmECRUD;

public class QuantumMatrixDao extends AbstractDAO implements OrmECRUD<QuantumMatrix>{
	public QuantumMatrixDao(final HibernateSession session) {
		super(session);
	}

	@Override
	public List<QuantumMatrix> findAll() {
		return readList(QuantumMatrix.class);
	}

	@Override
	public QuantumMatrix findById(final UUID matrixId) {
		if (matrixId == null) {
			throw new NullPointerException("Quantum matrix id can't be null");
		}
		else {
			try(final Session	session = getSession()) {
				return session.get(QuantumMatrix.class, matrixId);
			}
		}
	}

	@Override
	public void create(final QuantumMatrix entity) {
		if (entity == null) {
			throw new NullPointerException("Quantum matrix to create can't be null");
		}
		else {
			executeDML((s)->s.persist(entity));
		}
	}

	@Override
	public void update(final QuantumMatrix entity) {
		if (entity == null) {
			throw new NullPointerException("Quantum matrix to update can't be null");
		}
		else {
			executeDML((s)->s.merge(entity));
		}
	}

	@Override
	public void delete(final QuantumMatrix entity) {
		if (entity == null) {
			throw new NullPointerException("Quantim matrix to delete can't be null");
		}
		else {
			executeDML((s)->s.remove(entity));
		}
	}
}
