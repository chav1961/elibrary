package chav1961.elibrary.admin.entities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReferenceRepo implements AutoCloseable {
	private final Connection			conn;
	private final PreparedStatement[][]	stmts; 
	
	public ReferenceRepo(final Connection conn) throws SQLException {
		if (conn == null) {
			throw new NullPointerException("Connection can't be null");
		}
		else {
			this.conn = conn;
			this.stmts = new PreparedStatement[][] {
								new PreparedStatement[] {
									conn.prepareStatement(""),
									conn.prepareStatement("")
								},
								new PreparedStatement[] {
										conn.prepareStatement(""),
										conn.prepareStatement("")
								},
								new PreparedStatement[] {
										conn.prepareStatement(""),
										conn.prepareStatement("")
								},
								new PreparedStatement[] {
										conn.prepareStatement(""),
										conn.prepareStatement("")
								}
							};
		}
	}

	@Override
	public void close() throws SQLException {
		for (PreparedStatement[] list : stmts) {
			for (PreparedStatement item : list) {
				item.close();
			}
		}
	}
	
	public String getPresentation(final String keyName, final long value) throws SQLException {
		if (keyName == null || keyName.isEmpty()) {
			throw new IllegalArgumentException("Key name can't be null or empty string");
		}
		else {
			switch (keyName) {
				case "bs_Id"	: return extract(stmts[0][1], value);
				case "bl_Id"	: return extract(stmts[1][1], value);
				default : throw new UnsupportedOperationException("Key name ["+keyName+"] is not supported yet"); 
			}
		}
	}
	
	public ResultSet getResultSet(final String keyName, final String filter) throws SQLException {
		if (keyName == null || keyName.isEmpty()) {
			throw new IllegalArgumentException("Key name can't be null or empty string");
		}
		else if (filter == null) {
			throw new NullPointerException("Filter string can't be null");
		}
		else {
			switch (keyName) {
				case "bs_Id"	: return select(stmts[0][1], filter);
				case "bl_Id"	: return select(stmts[1][1], filter);
				default : throw new UnsupportedOperationException("Key name ["+keyName+"] is not supported yet"); 
			}
		}
	}
	
	private String extract(final PreparedStatement ps, final long value) throws SQLException {
		ps.setLong(1, value);
		try(final ResultSet	rs = ps.executeQuery()) {
			if (rs.next()) {
				return rs.getString(1);
			}
			else {
				return "<not found>";
			}
		}
	}

	private ResultSet select(final PreparedStatement ps, final String value) throws SQLException {
		ps.setString(1, value);
		return ps.executeQuery();
	}
}
