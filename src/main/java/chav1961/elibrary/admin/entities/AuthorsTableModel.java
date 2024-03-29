package chav1961.elibrary.admin.entities;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;

public class AuthorsTableModel extends RefTableModel {
	private static final long 			serialVersionUID = 6136391534170874644L;
	
	private static final String			KEY_2_VALUE = "select \"ba_Name\" from \"elibrary\".\"bookauthors\" where \"ba_Id\" = ?";
	private static final String			CONTENT = "select \"ba_Id\", \"ba_Name\", \"ba_Comment\" from \"elibrary\".\"bookauthors\" where \"ba_Name\" like ? order by 1";

	private final Connection			conn;
	private final ContentNodeMetadata	root;
	private final PreparedStatement		key2value;
	private final PreparedStatement		content;
	private ResultSet					rs = null;
	
	public AuthorsTableModel(final Connection conn, final ContentNodeMetadata node) throws SQLException {
		if (conn == null) {
			throw new NullPointerException("Connection can't be null"); 
		}
		else if (node == null) {
			throw new NullPointerException("Metadata interface can't be null"); 
		}
		else {
			this.conn = conn;
			this.root = node.getOwner().byUIPath(URI.create("ui:/model/elibrary.bookauthors"));
			this.key2value = conn.prepareStatement(KEY_2_VALUE);
			this.content = conn.prepareStatement(CONTENT, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		}
	}
	
	@Override
	protected void refresh(final String template) throws SQLException {
		if (rs != null) {
			rs.close();
		}
		content.setString(1, template+"%");
		rs = content.executeQuery();
		fireTableDataChanged();
	}

	@Override
	protected String key2Value(final long key) {
		try{key2value.setLong(1, key);
			try(final ResultSet	rs = key2value.executeQuery()) {
				
				if (rs.next()) {
					return rs.getString(1);
				}
				else {
					return "???";
				}
			}
		} catch (SQLException e) {
			return "???";
		}
	}

	@Override
	public void close() throws SQLException {
		if (rs != null) {
			rs.close();
		}
		key2value.close();
		content.close();
	}

	@Override
	public int getRowCount() {
		if (conn == null) {
			return 0;
		}
		else {
			try{if (rs == null) {
					refresh("");
				}
				rs.last();
				return rs.getRow();
			} catch (SQLException e) {
				e.printStackTrace();
				return 0;
			}
		}
	}

	@Override
	public int getColumnCount() {
		try {
			return rs.getMetaData().getColumnCount();
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public String getColumnName(final int columnIndex) {
		try {
			return rs.getMetaData().getColumnName(columnIndex + 1);
		} catch (SQLException e) {
			e.printStackTrace();
			return "???";
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		try {
			return Class.forName(rs.getMetaData().getColumnClassName(columnIndex + 1));
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return String.class;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		try{rs.absolute(rowIndex + 1);
			return rs.getObject(columnIndex + 1);
		} catch (SQLException e) {
			e.printStackTrace();
			return "???";
		}
	}
}
