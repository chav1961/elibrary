package chav1961.elibrary.admin.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import chav1961.elibrary.admin.dialogs.SeriesDescriptor;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.sql.interfaces.InstanceManager;

public class SeriesDescriptorMgr implements InstanceManager<Long, SeriesDescriptor> {
	private final LoggerFacade		logger;
	private final UniqueIdGenerator	uig;
	
	public SeriesDescriptorMgr(final LoggerFacade logger, final UniqueIdGenerator uig) {
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
	public SeriesDescriptor newInstance() throws SQLException {
		return new SeriesDescriptor(logger);
	}

	@Override
	public Long newKey() throws SQLException {
		return uig.getId();
	}

	@Override
	public Long extractKey(final SeriesDescriptor inst) throws SQLException {
		return inst.id;
	}

	@Override
	public SeriesDescriptor clone(final SeriesDescriptor inst) throws SQLException {
		try{final SeriesDescriptor	clone = inst.clone();

			clone.id = uig.getId();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new SQLException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void loadInstance(final ResultSet rs, final SeriesDescriptor inst) throws SQLException {
		inst.id = rs.getLong("bs_Id");
		inst.parent = rs.getLong("bs_Parent");
		inst.seriesName = rs.getString("bs_Name");
		if (rs.wasNull()) {
			inst.seriesName = "";
		}
		inst.seriesComment = rs.getString("bs_Comment");
		if (rs.wasNull()) {
			inst.seriesComment = "";
		}
	}

	@Override
	public void storeInstance(final ResultSet rs, final SeriesDescriptor inst, final boolean update) throws SQLException {
		if (!update) {
			rs.updateLong("bs_Id", inst.id);
		}
		rs.updateLong("bs_Parent", inst.parent);
		rs.updateString("bs_Name", inst.seriesName);
		if (inst.seriesComment.trim().isEmpty()) {
			rs.updateNull("bs_Comment");
		}
		else {
			rs.updateString("bs_Comment", inst.seriesComment);
		}
	}

	@Override
	public <T> T get(final SeriesDescriptor inst, final String name) throws SQLException {
		switch (name) {
			case "bs_Id" 		: return (T) Long.valueOf(inst.id);
			case "bs_Parent" 	: return (T) Long.valueOf(inst.parent);
			case "bs_Name" 		: return (T) inst.seriesName;
			case "bs_Comment"	: return (T) inst.seriesComment;
			default : throw new SQLException("Name ["+name+"] is missing in the instance");
		}
	}

	@Override
	public <T> InstanceManager<Long, SeriesDescriptor> set(final SeriesDescriptor inst, final String name, final T value) throws SQLException {
			switch (name) {
				case "bs_Id" 		: 
					inst.id = (Long)value;
					break;
				case "bs_Parent" 	:
					inst.parent = (Long)value;
					break;
				case "bs_Name" 		:
					inst.seriesName = (String)value;
					break;
				case "bs_Comment"	:
					inst.seriesComment = (String)value;
					break;
				default : throw new SQLException("Name ["+name+"] is missing in the instance");
			}
		return this;
	}

	@Override
	public void close() throws SQLException {
	}
}
