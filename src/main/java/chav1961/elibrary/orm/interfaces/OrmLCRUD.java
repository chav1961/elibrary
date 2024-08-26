package chav1961.elibrary.orm.interfaces;

import java.util.List;

public interface OrmLCRUD<T> extends OrmCRUD<T> {
	T findByIds(long parentEntityId, long childEntityId);
	List<T> findByParentId(long parentEntityId);
	List<T> findByChildId(long childEntityId);
}
