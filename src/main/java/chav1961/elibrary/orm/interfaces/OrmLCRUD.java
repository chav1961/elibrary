package chav1961.elibrary.orm.interfaces;

import java.util.List;
import java.util.UUID;

public interface OrmLCRUD<T> extends OrmCRUD<T> {
	T findByIds(UUID parentEntityId, UUID childEntityId);
	List<T> findByParentId(UUID parentEntityId);
	List<T> findByChildId(UUID childEntityId);
}
