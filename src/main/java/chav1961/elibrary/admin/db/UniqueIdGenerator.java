package chav1961.elibrary.admin.db;

import java.sql.SQLException;

@FunctionalInterface
public interface UniqueIdGenerator {
	long getId() throws SQLException;
}
