package chav1961.elibrary.admin.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContentManipulator implements AutoCloseable  {
	private static final byte[]		EMPTY = new byte[0];
	
	private final PreparedStatement	psImageLoad;
	private final PreparedStatement	psContentLoad;
	
	public ContentManipulator(final Connection conn) throws SQLException {
		if (conn == null) {
			throw new NullPointerException("Connection can't be null"); 
		}
		else {
			this.psImageLoad = conn.prepareStatement("select \"bl_Image\" from \"elibrary\".\"booklist\" where \"bl_Id\" = ? and \"bl_Image\" is not null union all select \"bl_Image\" from \"elibrary\".\"booklist\" where \"bl_Id\" in (select \"bl_Parent\" from \"elibrary\".\"booklist\" where \"bl_Id\" = ?)");
			this.psContentLoad = conn.prepareStatement("select \"bl_Mime\",\"bl_Content\" from \"elibrary\".\"booklist\" where \"bl_Id\" = ? and \"bl_Content\" is not null union all select \"bl_Mime\",\"bl_Content\" from \"elibrary\".\"booklist\" where \"bl_Id\" in (select \"bl_Parent\" from \"elibrary\".\"booklist\" where \"bl_Id\" = ?)");
		}
	}
	
	public byte[] loadImage(final long key) throws SQLException {
		psImageLoad.setLong(1, key);
		psImageLoad.setLong(2, key);
		try(final ResultSet	rs = psImageLoad.executeQuery()) {
			if (rs.next()) {
				return rs.getBytes(1);
			}
			else {
				return EMPTY;
			}
		}
	}

	public byte[] loadContent(final long key) throws SQLException {
		psContentLoad.setLong(1, key);
		psContentLoad.setLong(2, key);
		try(final ResultSet	rs = psContentLoad.executeQuery()) {
			if (rs.next()) {
				return rs.getBytes(1);
			}
			else {
				return EMPTY;
			}
		}
	}

	@Override 
	public void close() throws SQLException {
		psImageLoad.close();
		psContentLoad.close();
	}
}
