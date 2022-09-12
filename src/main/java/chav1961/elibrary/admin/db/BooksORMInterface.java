package chav1961.elibrary.admin.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;

import chav1961.elibrary.admin.entities.BookDescriptor;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.sql.interfaces.UniqueIdGenerator;
import chav1961.purelib.ui.interfaces.FormManager;

public class BooksORMInterface implements DedicatedORMInterface<BookDescriptor, BooksDescriptorMgr> {
	private final Statement				stmtList;
	private final ResultSet				rsList;
	private final PreparedStatement		stmtRec;
	private final BooksDescriptorMgr	mgr;
	private final BookDescriptor		desc;
	private final BookDescriptor		dedicatedDesc;
	
	public BooksORMInterface(final LoggerFacade logger, final Connection conn, final UniqueIdGenerator gen, final ContentNodeMetadata meta) throws SQLException, NamingException {
		this.stmtList = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		this.rsList = stmtList.executeQuery("select * from elibrary.booklist order by \"bl_Id\"");
		this.stmtRec = conn.prepareStatement("select * from elibrary.booklist where \"bl_Id\" = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		this.desc = new BookDescriptor(logger, meta);
		this.dedicatedDesc = new BookDescriptor(logger, meta);
		this.mgr = new BooksDescriptorMgr(logger, this.desc, gen, conn);
	}
	
	@Override
	public BooksDescriptorMgr getInstanceManager() {
		return mgr;
	}

	@Override
	public FormManager<Long, BookDescriptor> getFormManager() {
		return desc;
	}

	@Override
	public FormManager<Long, BookDescriptor> getDedicatedFormManager() {
		return dedicatedDesc;
	}
	
	@Override
	public ResultSet getListResultSet() {
		return rsList;
	}

	@Override
	public ResultSet getRecordResultSet(final Long key) throws SQLException {
		stmtRec.setLong(1, key);
		return stmtRec.executeQuery();
	}
	
	@Override
	public void close() throws SQLException {
		mgr.close();
		rsList.close();
		stmtList.close();
		stmtRec.close();
	}
}
