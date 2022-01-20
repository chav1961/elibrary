package chav1961.elibrary.admin.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import chav1961.elibrary.admin.dialogs.SeriesDescriptor;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.ui.interfaces.FormManager;

public class SeriesORMInterface implements ORMInterface<SeriesDescriptor, SeriesDescriptorMgr> {
	private final Statement				stmt;
	private final ResultSet				rs;
	private final UniqueIdGenerator		gen;
	private final SeriesDescriptorMgr	mgr;
	private final SeriesDescriptor		desc;
	
	public SeriesORMInterface(final LoggerFacade logger, final Connection conn, final UniqueIdGenerator gen) throws SQLException {
		this.stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		this.rs = stmt.executeQuery("select * from elibrary.bookseries order by \"bs_Id\"");
		this.gen = gen;
		this.mgr = new SeriesDescriptorMgr(logger, gen);
		this.desc = new SeriesDescriptor(logger);
	}
	
	@Override
	public SeriesDescriptorMgr getInstanceManager() {
		return mgr;
	}

	@Override
	public FormManager<Long, SeriesDescriptor> getFormManager() {
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
