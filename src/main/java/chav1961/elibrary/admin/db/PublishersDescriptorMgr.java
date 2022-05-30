package chav1961.elibrary.admin.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import chav1961.elibrary.admin.dialogs.PublishersDescriptor;
import chav1961.elibrary.admin.dialogs.SeriesDescriptor;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.sql.interfaces.InstanceManager;
import chav1961.purelib.sql.interfaces.UniqueIdGenerator;

public class PublishersDescriptorMgr implements InstanceManager<Long, PublishersDescriptor> {
	private final LoggerFacade		logger;
	private final UniqueIdGenerator	uig;
	
	public PublishersDescriptorMgr(final LoggerFacade logger, final UniqueIdGenerator uig) {
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
	public PublishersDescriptor newInstance() throws SQLException {
		return new PublishersDescriptor(logger);
	}

	@Override
	public Long newKey() throws SQLException {
		return uig.getId();
	}

	@Override
	public Long extractKey(final PublishersDescriptor inst) throws SQLException {
		return inst.id;
	}

	@Override
	public PublishersDescriptor clone(final PublishersDescriptor inst) throws SQLException {
		try{final PublishersDescriptor	clone = inst.clone();

			clone.id = uig.getId();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new SQLException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void loadInstance(final ResultSet rs, final PublishersDescriptor inst) throws SQLException {
		inst.id = rs.getLong("bs_Id");
		inst.name = rs.getString("bs_Name");
		inst.comment = rs.getString("bs_Comment");
	}

	@Override
	public void storeInstance(final ResultSet rs, final PublishersDescriptor inst, final boolean update) throws SQLException {
		if (!update) {
			rs.updateLong("bs_Id", inst.id);
		}
		rs.updateString("bs_Name", inst.name);
		rs.updateString("bs_Comment", inst.comment);
	}

	@Override
	public <T> T get(final PublishersDescriptor inst, final String name) throws SQLException {
		switch (name) {
			case "bs_Id" 		: return (T) Long.valueOf(inst.id);
			case "bs_Name" 		: return (T) inst.name;
			case "bs_Comment"	: return (T) inst.comment;
			default : throw new SQLException("Name ["+name+"] is missing in the instance");
		}
	}

	@Override
	public <T> InstanceManager<Long, PublishersDescriptor> set(final PublishersDescriptor inst, final String name, final T value) throws SQLException {
			switch (name) {
				case "bs_Id" 		: 
					inst.id = (Long)value;
					break;
				case "bs_Name" 		:
					inst.name = (String)value;
					break;
				case "bs_Comment"	:
					inst.comment = (String)value;
					break;
				default : throw new SQLException("Name ["+name+"] is missing in the instance");
			}
		return this;
	}

	@Override
	public void close() throws SQLException {
	}

	@Override
	public void storeInstance(PreparedStatement ps, PublishersDescriptor inst, boolean update) throws SQLException {
		// TODO Auto-generated method stub
		
	}
}
