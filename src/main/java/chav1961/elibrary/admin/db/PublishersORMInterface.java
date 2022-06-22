package chav1961.elibrary.admin.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import chav1961.elibrary.admin.entities.PublishersDescriptor;
import chav1961.elibrary.admin.entities.SeriesDescriptor;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.sql.interfaces.UniqueIdGenerator;
import chav1961.purelib.ui.interfaces.FormManager;

public class PublishersORMInterface implements ORMInterface<PublishersDescriptor, PublishersDescriptorMgr> {
	private final Statement					stmt;
	private final ResultSet					rs;
	private final UniqueIdGenerator			gen;
	private final PublishersDescriptorMgr	mgr;
	private final PublishersDescriptor		desc;
	
	public PublishersORMInterface(final LoggerFacade logger, final Connection conn, final UniqueIdGenerator gen) throws SQLException {
		this.stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		this.rs = stmt.executeQuery("select * from elibrary.bookpublishers order by \"bp_Id\"");
		this.gen = gen;
		this.mgr = new PublishersDescriptorMgr(logger, gen);
		this.desc = new PublishersDescriptor(logger);
	}
	
	@Override
	public PublishersDescriptorMgr getInstanceManager() {
		return mgr;
	}

	@Override
	public FormManager<Long, PublishersDescriptor> getFormManager() {
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
