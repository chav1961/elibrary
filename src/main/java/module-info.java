module chav1961.elibrary {
	requires transitive chav1961.purelib;
	requires java.desktop;
	requires java.sql;
	requires java.naming;
	requires java.datatransfer;
	requires lucene.core;
	requires lucene.queryparser;
	requires lucene.highlighter;
	requires org.hibernate.orm.core;
	requires jakarta.persistence;
	requires flyway.core;

	opens chav1961.elibrary.admin to chav1961.purelib;
}
