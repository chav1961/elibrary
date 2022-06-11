package chav1961.elibrary.admin.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import chav1961.elibrary.admin.dialogs.AuthorsDescriptor;
import chav1961.elibrary.admin.dialogs.BookDescriptor;
import chav1961.elibrary.admin.dialogs.SeriesDescriptor;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.sql.interfaces.UniqueIdGenerator;
import chav1961.purelib.ui.interfaces.FormManager;

public class BooksORMInterface implements ORMInterface<BookDescriptor, BooksDescriptorMgr> {
	private final Statement				stmt;
	private final ResultSet				rs;
	private final BooksDescriptorMgr	mgr;
	private final BookDescriptor		desc;
	
	public BooksORMInterface(final LoggerFacade logger, final Connection conn, final UniqueIdGenerator gen) throws SQLException {
		this.stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		this.rs = stmt.executeQuery("select * from elibrary.booklist order by \"bl_Id\"");
		this.desc = new BookDescriptor(logger);
		this.mgr = new BooksDescriptorMgr(logger, this.desc, gen);
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
