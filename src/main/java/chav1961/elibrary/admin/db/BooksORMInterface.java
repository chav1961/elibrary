package chav1961.elibrary.admin.db;

import java.sql.Connection;
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
	private final Statement				stmt;
	private final ResultSet				rs;
	private final BooksDescriptorMgr	mgr;
	private final BookDescriptor		desc;
	private final BookDescriptor		dedicatedDesc;
	
	public BooksORMInterface(final LoggerFacade logger, final Connection conn, final UniqueIdGenerator gen, final ContentNodeMetadata meta) throws SQLException, NamingException {
		this.stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		this.rs = stmt.executeQuery("select * from elibrary.booklist order by \"bl_Id\"");
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
