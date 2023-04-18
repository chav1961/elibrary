package chav1961.elibrary.admin.entities;

import javax.naming.NamingException;

import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.model.ImageKeeperImpl;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.LongItemAndReference;
import chav1961.purelib.ui.interfaces.MimeBasedContent;
import chav1961.purelib.ui.interfaces.ReferenceAndComment;
import chav1961.purelib.ui.interfaces.RefreshMode;

@LocaleResourceLocation("i18n:xml:root://chav1961.elibrary.admin.entities.InnerBookDescriptor/chav1961/elibrary/i18n/i18n.xml")
@LocaleResource(value="elibrary.booklist",tooltip="elibrary.booklist.tt",help="help.aboutApplication")
public class InnerBookDescriptor implements Cloneable, FormManager<Long, InnerBookDescriptor>, ModuleAccessor {
	private final LoggerFacade	logger;
	
	public long			parentRef;
	public long			id;
	
	@LocaleResource(value="elibrary.booklist.bl_Title",tooltip="elibrary.booklist.bl_Title.tt")
	@Format("9.2msL")
	public String		title = "";

	@LocaleResource(value="elibrary.booklist.bl_Page",tooltip="elibrary.booklist.bl_Page.tt")
	@Format("9ms")
	public int			page;
	
	@LocaleResource(value="elibrary.booklist.ba_Name",tooltip="elibrary.booklist.ba_Name.tt")
	@Format("10*5ms")
	public LongItemAndReference<String>[]	authors;

	@LocaleResource(value="elibrary.booklist.bl_Comment",tooltip="elibrary.booklist.bl_Comment.tt")
	@Format("20msl")
	public String		annotation = "";

	@LocaleResource(value="elibrary.booklist.bl_Tags",tooltip="elibrary.booklist.bl_Tags.tt")
	@Format("10*5ms")
	public ReferenceAndComment[]	tags = new ReferenceAndComment[0];

	@LocaleResource(value="elibrary.booklist.bl_Image.inner",tooltip="elibrary.booklist.bl_Image.inner.tt")
	@Format("200*200ms")
	public ImageKeeperImpl	image;

	
	public InnerBookDescriptor(final LoggerFacade logger, final ContentNodeMetadata root) throws NamingException {
		this.logger = logger;
		this.authors = new LongItemAndReference[] {new AnyRefDescriptor(root.getParent().getChild("book2authors").getChild("ba_Id"))};
		this.image = new LazyImageKeeperImpl();
	}

	@Override
	public InnerBookDescriptor clone() throws CloneNotSupportedException {
		return (InnerBookDescriptor) super.clone();
	}
	
	@Override
	public RefreshMode onField(final InnerBookDescriptor inst, final Long id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		// TODO Auto-generated method stub
		return RefreshMode.DEFAULT;
	}
	
	@Override
	public RefreshMode onRecord(final RecordAction action, final InnerBookDescriptor oldRecord, final Long oldId, final InnerBookDescriptor newRecord, final Long newId) throws FlowException, LocalizationException {
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
