package chav1961.elibrary.admin.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import chav1961.elibrary.admin.dialogs.AuthorsDescriptor;
import chav1961.elibrary.admin.dialogs.SeriesDescriptor;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.sql.interfaces.UniqueIdGenerator;
import chav1961.purelib.ui.interfaces.FormManager;

public class AuthorsORMInterface implements ORMInterface<AuthorsDescriptor, AuthorsDescriptorMgr> {
	private final Statement				stmt;
	private final ResultSet				rs;
	private final UniqueIdGenerator		gen;
	private final AuthorsDescriptorMgr	mgr;
	private final AuthorsDescriptor		desc;
	
	public AuthorsORMInterface(final LoggerFacade logger, final Connection conn, final UniqueIdGenerator gen) throws SQLException {
		this.stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		this.rs = stmt.executeQuery("select * from elibrary.bookseries order by \"bs_Id\"");
		this.gen = gen;
		this.mgr = new AuthorsDescriptorMgr(logger, gen);
		this.desc = new AuthorsDescriptor(logger);
	}
	
	@Override
	public AuthorsDescriptorMgr getInstanceManager() {
		return mgr;
	}

	@Override
	public FormManager<Long, AuthorsDescriptor> getFormManager() {
		return desc;
	}

	@Override
	public ResultSet getResultSet() {
		return rs;
	}

	@Override
	public void close() throws SQLException {
		mgr.close();
		rs.close();
		stmt.close();
	}
}
