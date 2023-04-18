package chav1961.elibrary.admin.entities;

import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;

@LocaleResourceLocation("i18n:xml:root://chav1961.elibrary.admin.entities.PublishersDescriptor/chav1961/elibrary/i18n/i18n.xml")
@LocaleResource(value="elibrary.bookpublishers",tooltip="elibrary.bookpublishers.tt",help="help.aboutApplication")
public class PublishersDescriptor implements Cloneable, FormManager<Long, PublishersDescriptor>, ModuleAccessor {
	private final LoggerFacade	logger;
	
	public long		id;

	@LocaleResource(value="elibrary.bookpublishers.bp_Name",tooltip="elibrary.bookpublishers.bp_Name.tt")
	@Format("9.2msL")
	public String	name= "";
	
	@LocaleResource(value="elibrary.bookpublishers.bp_Comment",tooltip="elibrary.bookpublishers.bp_Comment.tt")
	@Format("9.2msl")
	public String	comment = "";
	
	public PublishersDescriptor(final LoggerFacade logger) {
		this.logger = logger;
	}
	
	@Override
	public PublishersDescriptor clone() throws CloneNotSupportedException {
		return (PublishersDescriptor) super.clone();
	}

	@Override
	public RefreshMode onField(PublishersDescriptor inst, Long id, String fieldName, Object oldValue, boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onRecord(final RecordAction action, final PublishersDescriptor oldRecord, final Long oldId, final PublishersDescriptor newRecord, final Long newId) throws FlowException, LocalizationException {
		switch (action) {
			case UPDATE		:
				break;
			case CHECK		:
				break;
			case DELETE		:
				break;
			case DUPLICATE	:
				break;
			case INSERT		:
				newRecord.id = newId;
				newRecord.name = "New publisher";
				newRecord.comment = "";
				break;
			default:
				throw new UnsupportedOperationException("Action ["+action+"] is not supported yet");
		}
		return RefreshMode.RECORD_ONLY;
	}
	
	@Override
	public LoggerFacade getLogger() {
		return logger;
	}

	@Override
	public void allowUnnamedModuleAccess(final Module... unnamedModules) {
		for (Module item : unnamedModules) {
			this.getClass().getModule().addExports(this.getClass().getPackageName(),item);
		}
	}

	@Override
	public String toString() {
		return "PublishersDescriptor [name=" + name + ", comment=" + comment + "]";
	}
}
