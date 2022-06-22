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

@LocaleResourceLocation("i18n:xml:root://chav1961.elibrary.admin.dialogs.BookAuthorsDescriptor/chav1961/elibrary/i18n/i18n.xml")
@LocaleResource(value="elibrary.book2authors",tooltip="elibrary.book2authors.tt",help="help.aboutApplication")
public class BookAuthorsDescriptor implements Cloneable, FormManager<Long, BookAuthorsDescriptor>, ModuleAccessor {
	private final LoggerFacade	logger;
	
	public long		id;

	@LocaleResource(value="elibrary.book2authors.bl_Id",tooltip="elibrary.book2authors.bl_Id.tt")
	@Format("9.2msL")
	public long		book;
	
	@LocaleResource(value="elibrary.book2authors.ba_Id",tooltip="elibrary.book2authors.ba_Id.tt")
	@Format("9.2msl")
	public long		author;
	
	public BookAuthorsDescriptor(final LoggerFacade logger) {
		this.logger = logger;
	}
	
	@Override
	public BookAuthorsDescriptor clone() throws CloneNotSupportedException {
		return (BookAuthorsDescriptor) super.clone();
	}

	@Override
	public RefreshMode onField(BookAuthorsDescriptor inst, Long id, String fieldName, Object oldValue, boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onRecord(final RecordAction action, final BookAuthorsDescriptor oldRecord, final Long oldId, final BookAuthorsDescriptor newRecord, final Long newId) throws FlowException, LocalizationException {
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
		return "BookAuthorsDescriptor [name=" + book + ", comment=" + author + "]";
	}
}
