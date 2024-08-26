package chav1961.elibrary.orm.metamodels;

import chav1961.elibrary.orm.entities.Book2Authors;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel( Book2Authors.class )
public class Book2Authors_ {
	public static volatile SingularAttribute<Book2Authors, Long> bookId;
	public static volatile SingularAttribute<Book2Authors, Long> authorId;
}
