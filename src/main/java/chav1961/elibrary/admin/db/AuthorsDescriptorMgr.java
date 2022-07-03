package chav1961.elibrary.admin.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import chav1961.elibrary.admin.entities.AuthorsDescriptor;
import chav1961.elibrary.admin.entities.BookDescriptor;
import chav1961.elibrary.admin.entities.SeriesDescriptor;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.sql.interfaces.InstanceManager;
import chav1961.purelib.sql.interfaces.UniqueIdGenerator;
import chav1961.purelib.ui.interfaces.RecordFormManager.RecordAction;

public class AuthorsDescriptorMgr implements InstanceManager<Long, AuthorsDescriptor> {
	private final LoggerFacade		logger;
	private final AuthorsDescriptor	desc;
	private final UniqueIdGenerator	uig;
	
	public AuthorsDescriptorMgr(final LoggerFacade logger, final UniqueIdGenerator uig) {
		this.logger = logger;
		this.desc = new AuthorsDescriptor(logger);
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
	public AuthorsDescriptor newInstance() throws SQLException {
		try{final Long				key = newKey();
			final AuthorsDescriptor	newInst = desc.clone();
		
			assignKey(newInst, key);
			newInst.onRecord(RecordAction.INSERT, null, null, newInst, key);
			return newInst;
		} catch (FlowException | CloneNotSupportedException e) {
			throw new SQLException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Long newKey() throws SQLException {
		return uig.getId();
	}

	@Override
	public Long extractKey(final AuthorsDescriptor inst) throws SQLException {
		return inst.id;
	}

	@Override
	public void assignKey(final AuthorsDescriptor inst, final Long key) throws SQLException {
		inst.id = key;
	}
	
	@Override
	public AuthorsDescriptor clone(final AuthorsDescriptor inst) throws SQLException {
		try{final AuthorsDescriptor	clone = inst.clone();

			clone.id = uig.getId();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new SQLException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void loadInstance(final ResultSet rs, final AuthorsDescriptor inst) throws SQLException {
		inst.id = rs.getLong("ba_Id");
		inst.name = rs.getString("ba_Name");
		inst.comment = rs.getString("ba_Comment");
	}

	@Override
	public void storeInstance(final ResultSet rs, final AuthorsDescriptor inst, final boolean update) throws SQLException {
		rs.updateString("ba_Name", inst.name);
		rs.updateString("ba_Comment", inst.comment);
		if (!update) {
			rs.updateLong("ba_Id", inst.id);
		}
	}

	@Override
	public <T> T get(final AuthorsDescriptor inst, final String name) throws SQLException {
		switch (name) {
			case "ba_Id" 		: return (T) Long.valueOf(inst.id);
			case "ba_Name" 		: return (T) inst.name;
			case "ba_Comment"	: return (T) inst.comment;
			default : throw new SQLException("Name ["+name+"] is missing in the instance");
		}
	}

	@Override
	public <T> InstanceManager<Long, AuthorsDescriptor> set(final AuthorsDescriptor inst, final String name, final T value) throws SQLException {
		switch (name) {
			case "ba_Id" 		: 
				inst.id = (Long)value;
				break;
			case "ba_Name" 		:
				inst.name = (String)value;
				break;
			case "ba_Comment"	:
				inst.comment = (String)value;
				break;
			default :
				throw new SQLException("Name ["+name+"] is missing in the instance");
		}
		return this;
	}

	@Override
	public void close() throws SQLException {
	}

	@Override
	public void storeInstance(PreparedStatement ps, AuthorsDescriptor inst, boolean update) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}
