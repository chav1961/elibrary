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

@LocaleResourceLocation("i18n:xml:root://chav1961.elibrary.admin.entities.AuthorsDescriptor/chav1961/elibrary/i18n/i18n.xml")
@LocaleResource(value="elibrary.bookauthors",tooltip="elibrary.bookauthors.tt",help="help.aboutApplication")
public class AuthorsDescriptor implements Cloneable, FormManager<Long, AuthorsDescriptor>, ModuleAccessor {
	private final LoggerFacade	logger;
	
	public long		id;

	@LocaleResource(value="elibrary.bookauthors.ba_Name",tooltip="elibrary.bookauthors.ba_Name.tt")
	@Format("9.2msL")
	public String	name = "";
	
	@LocaleResource(value="elibrary.bookauthors.ba_Comment",tooltip="elibrary.bookauthors.ba_Comment.tt")
	@Format("9.2msl")
	public String	comment = "";
	
	public AuthorsDescriptor(final LoggerFacade logger) {
		this.logger = logger;
	}
	
	@Override
	public AuthorsDescriptor clone() throws CloneNotSupportedException {
		return (AuthorsDescriptor) super.clone();
	}

	@Override
	public RefreshMode onField(AuthorsDescriptor inst, Long id, String fieldName, Object oldValue, boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onRecord(final RecordAction action, final AuthorsDescriptor oldRecord, final Long oldId, final AuthorsDescriptor newRecord, final Long newId) throws FlowException, LocalizationException {
		switch (action) {
			case CHANGE		:
				break;
			case CHECK		:
				break;
			case DELETE		:
				break;
			case DUPLICATE	:
				break;
			case INSERT		:
				newRecord.id = newId;
				newRecord.name = "New author";
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
		return "AuthorsDescriptor [name=" + name + ", comment=" + comment + "]";
	}
}
