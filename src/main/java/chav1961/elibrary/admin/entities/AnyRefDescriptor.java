package chav1961.elibrary.admin.entities;

import javax.swing.table.TableModel;

import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.table.DefaultTableModel;

import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.interfaces.LongItemAndReference;

public class AnyRefDescriptor implements LongItemAndReference<String> {
	private final Context				modelContext;
	private final ContentNodeMetadata	metadata;
	private final String				keyName;
	private long						key = 0;
	private String						presentation = "";
	private String						filter = "";
	
	protected AnyRefDescriptor(final ContentNodeMetadata metadata) throws NamingException {
		if (metadata == null) {
			throw new NullPointerException("Metadata can't be null"); 
		}
		else {
			this.metadata = metadata;
			this.keyName = metadata.getName();
			this.modelContext = new InitialContext();
		}
	}

	@Override
	public String getKeyName() {
		return keyName;
	}

	@Override
	public Class<?> getKeyClass() {
		return long.class;
	}

	@Override
	public Class<String> getPresentationClass() {
		return String.class;
	}

	@Override 
	public String getPresentation() {
		return presentation;
	}
	
	@Override
	public TableModel getModel() {
		try{return (TableModel)modelContext.lookup("models/"+getKeyName());
		} catch (NamingException e) {
			throw new RuntimeException(e); 
		}
	}

	@Override
	public String getModelFilter() {
		return filter;
	}

	@Override
	public void setModelFilter(final String filter) {
		if (filter == null) {
			throw new NullPointerException("Filter can't be null"); 
		}
		else {
			this.filter = filter;
			refresh();
		}
	}

	@Override
	public void setModel(final TableModel model) {
		throw new UnsupportedOperationException("Not implemented"); 
	}

	@Override
	public ContentNodeMetadata getNodeMetadata() {
		return metadata;
	}

	@Override
	public long getValue() {
		return key;
	}

	@Override
	public long getValue(final int position) {
		final TableModel	model = getModel();
		
		if (position < 0 || position >= model.getRowCount()) {
			throw new IllegalArgumentException("Position ["+position+"] out of range 0.."+(model.getRowCount()-1));
		}
		for (int index = 0, maxIndex = model.getColumnCount(); index < maxIndex; index++) {
			if (model.getColumnName(maxIndex).equals(getKeyName())) {
				return ((Long)model.getValueAt(position, index)).longValue();
			}
		}
		throw new IllegalArgumentException("Key name ["+getKeyName()+"] not found in the table model");
	}
	
	@Override
	public void setValue(final long value) {
		this.key = value;
		this.presentation = key2presentation(value);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	protected void refresh() {
		final TableModel	model = getModel();
		
		if (model instanceof RefTableModel) {
			try{((RefTableModel)model).refresh(getModelFilter());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else if (model != null) {
			((DefaultTableModel)getModel()).fireTableStructureChanged();
		}
	}
	
	protected String key2presentation(long value) {
		final TableModel	model = getModel();
		
		if (model instanceof RefTableModel) {
			try{
				return ((RefTableModel)model).key2Value(value);
			} catch (SQLException e) {
				return "??????";
			}
		}
		else {
			return "";
		}
	}
}
