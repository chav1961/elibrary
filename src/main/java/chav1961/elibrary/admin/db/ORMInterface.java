package chav1961.elibrary.admin.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import chav1961.purelib.sql.interfaces.InstanceManager;
import chav1961.purelib.ui.interfaces.FormManager;

public interface ORMInterface<Cl, Inst extends InstanceManager<Long, Cl>> extends AutoCloseable {
	Inst getInstanceManager();
	FormManager<Long, Cl> getFormManager();
	ResultSet getListResultSet(Long... key) throws SQLException;
	ResultSet getRecordResultSet(Long key) throws SQLException;
	void close() throws SQLException;
}
