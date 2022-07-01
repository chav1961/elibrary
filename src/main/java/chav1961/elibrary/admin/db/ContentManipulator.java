package chav1961.elibrary.admin.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContentManipulator implements AutoCloseable  {
	private static final byte[]		EMPTY = new byte[0];
	
	private final PreparedStatement	psImageLoad;
	private final PreparedStatement	psContentLoad;
	private final PreparedStatement	psImageStore;
	private final PreparedStatement	psContentStore;
	
	public ContentManipulator(final Connection conn) throws SQLException {
		if (conn == null) {
			throw new NullPointerException("Connection can't be null"); 
		}
		else {
			this.psImageLoad = conn.prepareStatement("");
			this.psContentLoad = conn.prepareStatement("");
			this.psImageStore = conn.prepareStatement("");
			this.psContentStore = conn.prepareStatement("");
		}
	}
	
	public byte[] loadImage(final long key) throws SQLException {
		psImageLoad.setLong(1, key);
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
		try(final ResultSet	rs = psContentLoad.executeQuery()) {
			if (rs.next()) {
				return rs.getBytes(1);
			}
			else {
				return EMPTY;
			}
		}
	}

	public void storeImage(final long key, final byte[] content) throws SQLException {
		psImageStore.setBytes(1, content);
		psImageStore.setLong(2, key);
		psImageStore.executeUpdate();
	}
	
	public void storeContent(final long key, final byte[] content) throws SQLException {
		psContentStore.setBytes(1, content);
		psContentStore.setLong(2, key);
		psContentStore.executeUpdate();
	}
	
	@Override 
	public void close() throws SQLException {
		
	}
	
}
