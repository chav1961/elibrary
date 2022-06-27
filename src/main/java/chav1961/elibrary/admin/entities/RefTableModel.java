package chav1961.elibrary.admin.entities;

import java.sql.SQLException;

import javax.swing.table.DefaultTableModel;

abstract class RefTableModel extends DefaultTableModel implements AutoCloseable {
	private static final long serialVersionUID = -7625704536077864457L;

	protected RefTableModel() {
	}
	
	@Override public abstract void close() throws SQLException;
	@Override public abstract int getRowCount();
	@Override public abstract int getColumnCount();
	@Override public abstract String getColumnName(int columnIndex);
	@Override public abstract Class<?> getColumnClass(int columnIndex);
	@Override public abstract Object getValueAt(int rowIndex, int columnIndex);
	protected abstract void refresh(final String template) throws SQLException;
	protected abstract String key2Value(final long key) throws SQLException;
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}
}
