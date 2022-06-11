package chav1961.elibrary.admin.dialogs;

import java.awt.Image;

import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.ReferenceAndComment;
import chav1961.purelib.ui.interfaces.RefreshMode;

@LocaleResourceLocation("i18n:xml:root://chav1961.elibrary.admin.dialogs.BookDescriptor/chav1961/elibrary/i18n/i18n.xml")
@LocaleResource(value="elibrary.booklist",tooltip="elibrary.booklist.tt",help="help.aboutApplication")
public class BookDescriptor implements Cloneable, FormManager<Long, BookDescriptor>, ModuleAccessor {
	private final LoggerFacade	logger;

	public long			id;
	
	@LocaleResource(value="elibrary.booklist.bl_Code",tooltip="elibrary.booklist.bl_Code.tt")
	@Format("9.2ms")
	public int			code;

	@LocaleResource(value="elibrary.booklist.bs_Id",tooltip="elibrary.booklist.bs_Id.tt")
	@Format("9.2ms")
	public long			seriesNumber;

	@LocaleResource(value="elibrary.booklist.bl_Title",tooltip="elibrary.booklist.bl_Title.tt")
	@Format("9.2msL")
	public String		title;
	
	@LocaleResource(value="elibrary.booklist.bl_Year",tooltip="elibrary.booklist.bl_Year.tt")
	@Format("9ms")
	public int			year;

	@LocaleResource(value="elibrary.booklist.bp_Id",tooltip="elibrary.booklist.bp_Id.tt")
	@Format("9.2ms")
	public long			publisher;
	
//	@LocaleResource(value="elibrary.booklist.ba_Name",tooltip="elibrary.booklist.ba_Name.tt")
//	@Format("9.2msL")
//	public ReferenceAndComment[]	authors;

	@LocaleResource(value="elibrary.booklist.bl_Comment",tooltip="elibrary.booklist.bl_Comment.tt")
	@Format("9.2msl")
	public String		annotation;

	@LocaleResource(value="elibrary.booklist.bl_Tags",tooltip="elibrary.booklist.bl_Tags.tt")
	@Format("10*5ms")
	public ReferenceAndComment[]	tags;

	@LocaleResource(value="elibrary.booklist.bl_Image",tooltip="elibrary.booklist.bl_Image.tt")
	@Format("200*200ms")
	public Image		image;

	
	public BookDescriptor(final LoggerFacade logger) {
		this.logger = logger;
	}

	@Override
	public BookDescriptor clone() throws CloneNotSupportedException {
		return (BookDescriptor) super.clone();
	}

	
	@Override
	public RefreshMode onField(BookDescriptor inst, Long id, String fieldName, Object oldValue, boolean beforeCommit) throws FlowException, LocalizationException {
		// TODO Auto-generated method stub
		return RefreshMode.DEFAULT;
	}
	
	@Override
	public RefreshMode onRecord(final RecordAction action, final BookDescriptor oldRecord, final Long oldId, final BookDescriptor newRecord, final Long newId) throws FlowException, LocalizationException {
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

	
	
}
