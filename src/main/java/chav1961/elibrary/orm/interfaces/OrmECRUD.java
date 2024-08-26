package chav1961.elibrary.orm.interfaces;

public interface OrmECRUD<T> extends OrmCRUD<T>{
	T findById(long entityId);
}
