module chav1961.elibrary {
	requires transitive chav1961.purelib;
	requires java.desktop;
	requires java.sql;

	opens chav1961.elibrary.admin to chav1961.purelib;
	exports chav1961.elibrary.admin.entities to chav1961.purelib;
}
