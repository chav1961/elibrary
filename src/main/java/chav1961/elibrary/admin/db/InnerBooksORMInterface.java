package chav1961.elibrary.admin.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;

import chav1961.elibrary.admin.entities.BookDescriptor;
import chav1961.elibrary.admin.entities.InnerBookDescriptor;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.sql.interfaces.UniqueIdGenerator;
import chav1961.purelib.ui.interfaces.FormManager;

public class InnerBooksORMInterface implements DedicatedORMInterface<InnerBookDescriptor, InnerBookDescriptorMgr> {
	private final PreparedStatement			stmtList;
	private final PreparedStatement			stmtRec;
	private final InnerBookDescriptorMgr	mgr;
	private final InnerBookDescriptor		desc;
	private final InnerBookDescriptor		dedicatedDesc;
	
	public InnerBooksORMInterface(final LoggerFacade logger, final Connection conn, final UniqueIdGenerator gen, final ContentNodeMetadata meta) throws SQLException, NamingException {
		this.stmtList = conn.prepareStatement("select * from \"elibrary\".\"booklist\" where \"bl_Parent\" = ? order by \"bl_Id\"", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		this.stmtRec = conn.prepareStatement("select * from \"elibrary\".\"booklist\" where \"bl_Id\" = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		this.desc = new InnerBookDescriptor(logger, meta);
		this.dedicatedDesc = new InnerBookDescriptor(logger, meta);
		this.mgr = new InnerBookDescriptorMgr(logger, this.desc, gen, conn);
	}
	
	@Override
	public InnerBookDescriptorMgr getInstanceManager() {
		return mgr;
	}

	@Override
	public FormManager<Long, InnerBookDescriptor> getFormManager() {
		return desc;
	}

	@Override
	public FormManager<Long, InnerBookDescriptor> getDedicatedFormManager() {
		return dedicatedDesc;
	}
	
	@Override
	public ResultSet getListResultSet(final Long... keys) throws SQLException {
		stmtList.setLong(1, keys[0]);
		return stmtList.executeQuery();
	}

	@Override
	public ResultSet getRecordResultSet(final Long key) throws SQLException {
		stmtRec.setLong(1, key);
		return stmtRec.executeQuery();
	}
	
	@Override
	public void close() throws SQLException {
		mgr.close();
		stmtList.close();
		stmtRec.close();
	}
}
