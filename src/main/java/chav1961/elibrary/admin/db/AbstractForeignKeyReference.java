package chav1961.elibrary.admin.db;

import javax.swing.table.TableModel;

import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.interfaces.LongItemAndReference;

abstract class AbstractForeignKeyReference<P> implements LongItemAndReference<P> {
	private final String				keyName;
	private final ContentNodeMetadata	metadata;
	private TableModel					model = null;
	private long						currentVal = 0;
	
	protected AbstractForeignKeyReference(final ContentNodeMetadata metadata, final String keyName, final String... fields) {
		this.metadata = metadata;
		this.keyName = keyName;
	}

	@Override public abstract Class<P> getPresentationClass();
	@Override public abstract P getPresentation();
	@Override public abstract String getModelFilter();
	@Override public abstract void setModelFilter(String filter);
	
	@Override
	public String getKeyName() {
		return keyName;
	}

	@Override
	public Class<?> getKeyClass() {
		return long.class;
	}

	@Override
	public long getValue() {
		return currentVal;
	}

	@Override
	public void setValue(final long value) {
		this.currentVal = value;
	}

	@Override
	public TableModel getModel() {
		return model;
	}

	@Override
	public ContentNodeMetadata getNodeMetadata() {
		return metadata;
	}

	@Override
	public void setModel(final TableModel model) {
		this.model = model;
	}

	@Override
	public LongItemAndReference<String> clone() throws CloneNotSupportedException {
		return (LongItemAndReference<String>) super.clone();
	}
}
