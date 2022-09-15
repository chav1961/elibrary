module chav1961.elibrary {
	requires transitive chav1961.purelib;
	requires java.desktop;
	requires java.sql;
	requires java.naming;
	requires lucene.core;
	requires lucene.queryparser;
	requires java.datatransfer;
	requires lucene.highlighter;

	opens chav1961.elibrary.admin to chav1961.purelib;
	exports chav1961.elibrary.admin.entities to chav1961.purelib;
}
