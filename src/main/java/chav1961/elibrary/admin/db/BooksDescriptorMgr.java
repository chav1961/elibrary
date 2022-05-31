package chav1961.elibrary.admin.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import chav1961.elibrary.admin.dialogs.AuthorsDescriptor;
import chav1961.elibrary.admin.dialogs.BookDescriptor;
import chav1961.elibrary.admin.dialogs.SeriesDescriptor;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.sql.interfaces.InstanceManager;
import chav1961.purelib.sql.interfaces.UniqueIdGenerator;

public class BooksDescriptorMgr implements InstanceManager<Long, BookDescriptor> {
	private final LoggerFacade		logger;
	private final UniqueIdGenerator	uig;
	
	public BooksDescriptorMgr(final LoggerFacade logger, final UniqueIdGenerator uig) {
		this.logger = logger;
		this.uig = uig;
	}
	
	@Override
	public Class<?> getInstanceType() {
		return SeriesDescriptor.class;
	}

	@Override
	public Class<?> getKeyType() {
		return Long.class;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public BookDescriptor newInstance() throws SQLException {
		return new BookDescriptor(logger);
	}

	@Override
	public Long newKey() throws SQLException {
		return uig.getId();
	}

	@Override
	public Long extractKey(final BookDescriptor inst) throws SQLException {
		return inst.id;
	}

	@Override
	public BookDescriptor clone(final BookDescriptor inst) throws SQLException {
		try{final BookDescriptor	clone = inst.clone();

			clone.id = uig.getId();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new SQLException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void loadInstance(final ResultSet rs, final BookDescriptor inst) throws SQLException {
		inst.id = rs.getLong("ba_Id");
//		inst.name = rs.getString("ba_Name");
//		inst.comment = rs.getString("ba_Comment");
	}

	@Override
	public void storeInstance(final ResultSet rs, final BookDescriptor inst, final boolean update) throws SQLException {
		if (!update) {
			rs.updateLong("ba_Id", inst.id);
		}
//		rs.updateString("ba_Name", inst.name);
//		rs.updateString("ba_Comment", inst.comment);
	}

	@Override
	public <T> T get(final BookDescriptor inst, final String name) throws SQLException {
		switch (name) {
			case "ba_Id" 		: return (T) Long.valueOf(inst.id);
//			case "ba_Name" 		: return (T) inst.name;
//			case "ba_Comment"	: return (T) inst.comment;
			default : throw new SQLException("Name ["+name+"] is missing in the instance");
		}
	}

	@Override
	public <T> InstanceManager<Long, BookDescriptor> set(final BookDescriptor inst, final String name, final T value) throws SQLException {
		switch (name) {
			case "ba_Id" 		: 
				inst.id = (Long)value;
				break;
//			case "ba_Name" 		:
//				inst.name = (String)value;
//				break;
//			case "ba_Comment"	:
//				inst.comment = (String)value;
//				break;
			default :
				throw new SQLException("Name ["+name+"] is missing in the instance");
		}
		return this;
	}

	@Override
	public void close() throws SQLException {
	}

	@Override
	public void storeInstance(PreparedStatement ps, BookDescriptor inst, boolean update) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}
