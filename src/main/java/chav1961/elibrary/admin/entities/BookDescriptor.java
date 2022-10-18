package chav1961.elibrary.admin.entities;

import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.swing.JFrame;
import javax.swing.border.LineBorder;

import chav1961.elibrary.Application;
import chav1961.elibrary.admin.InnerBookList;
import chav1961.elibrary.admin.db.ORMInterface;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.SimpleURLClassLoader;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.ImageKeeperImpl;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.interfaces.Action;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.LongItemAndReference;
import chav1961.purelib.ui.interfaces.MimeBasedContent;
import chav1961.purelib.ui.interfaces.ReferenceAndComment;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.ui.swing.useful.JDialogContainer;

@LocaleResourceLocation("i18n:xml:root://chav1961.elibrary.admin.entities.BookDescriptor/chav1961/elibrary/i18n/i18n.xml")
@LocaleResource(value="elibrary.booklist",tooltip="elibrary.booklist.tt",help="help.aboutApplication")
@Action(resource=@LocaleResource(value="elibrary.booklist.content",tooltip="elibrary.booklist.content.tt"),actionString="content")
public class BookDescriptor implements Cloneable, FormManager<Long, BookDescriptor>, ModuleAccessor {
	private final Localizer				localizer;
	private final LoggerFacade			logger;
	private final ContentNodeMetadata	root;
	private Map<Class<?>,ORMInterface<?,?>>		orms = new HashMap<>();

	public long			id;
	
	@LocaleResource(value="elibrary.booklist.bl_Code",tooltip="elibrary.booklist.bl_Code.tt")
	@Format("20ms")
	public String		code = "";

	@LocaleResource(value="elibrary.booklist.bs_Id",tooltip="elibrary.booklist.bs_Id.tt")
	@Format("20ms")
	public AnyRefDescriptor	seriesNumber;

	@LocaleResource(value="elibrary.booklist.bs_Seq",tooltip="elibrary.booklist.bs_Seq.tt")
	@Format("20ms")
	public String		seriesSeq;
	
	@LocaleResource(value="elibrary.booklist.bl_Title",tooltip="elibrary.booklist.bl_Title.tt")
	@Format("9.2msL")
	public String		title = "";
	
	@LocaleResource(value="elibrary.booklist.bl_Year",tooltip="elibrary.booklist.bl_Year.tt")
	@Format("4ms")
	public int			year = 1900;

	@LocaleResource(value="elibrary.booklist.bp_Id",tooltip="elibrary.booklist.bp_Id.tt")
	@Format("20ms")
	public AnyRefDescriptor	publisher;
	
	@LocaleResource(value="elibrary.booklist.ba_Name",tooltip="elibrary.booklist.ba_Name.tt")
	@Format("10*5ms")
	public LongItemAndReference<String>[]	authors;

	@LocaleResource(value="elibrary.booklist.bl_Comment",tooltip="elibrary.booklist.bl_Comment.tt")
	@Format("20msl")
	public String		annotation = "";

	@LocaleResource(value="elibrary.booklist.bl_Tags",tooltip="elibrary.booklist.bl_Tags.tt")
	@Format("10*5ms")
	public ReferenceAndComment[]	tags = new ReferenceAndComment[0];

	@LocaleResource(value="elibrary.booklist.bl_Image",tooltip="elibrary.booklist.bl_Image.tt")
	@Format("200*200ms")
	public ImageKeeperImpl	image;

	@LocaleResource(value="elibrary.booklist.bl_Content",tooltip="elibrary.booklist.bl_Content.tt")
	@Format("30m")
	public MimeBasedContent	content;

	public BookDescriptor(final Localizer localizer, final LoggerFacade logger, final ContentNodeMetadata root, final Map<Class<?>,ORMInterface<?,?>> orms) throws NamingException {
		this.localizer = localizer;
		this.logger = logger;
		this.root = root;
		this.orms = orms;
		this.seriesNumber = new AnyRefDescriptor(root.getChild("bs_Id"));
		this.publisher = new AnyRefDescriptor(root.getChild("bp_Id"));
		this.authors = new LongItemAndReference[] {new AnyRefDescriptor(root.getParent().getChild("book2authors").getChild("ba_Id"))};
		this.content = new LazyMimeBasedContentImpl();
		this.image = new LazyImageKeeperImpl();
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
	public RefreshMode onAction(final BookDescriptor inst, final Long id, final String actionName, final Object... parameter) throws FlowException {
		if ("app:action:/BookDescriptor.content".equals(actionName)) {
			try (final SimpleURLClassLoader		loader = new SimpleURLClassLoader(new URL[0])) {
				final ContentMetadataInterface	dbModel = ContentModelFactory.forJsonDescription(new InputStreamReader(ORMInterface.class.getResourceAsStream("model.json"), PureLibSettings.DEFAULT_CONTENT_ENCODING));
				final ContentMetadataInterface	mdiParent = ContentModelFactory.forXmlDescription(Application.class.getResourceAsStream("application.xml"));
				final InnerBookList				ibl = new InnerBookList(this.id, localizer, logger, dbModel, mdiParent, orms);
				final JDialogContainer			container = new JDialogContainer(localizer, (JFrame)null, root.getLabelId(), ibl, ModalityType.DOCUMENT_MODAL);
				
				ibl.setDividerLocation(400);
				ibl.setPreferredSize(new Dimension(1200,700));
				
				container.showDialog(JDialogContainer.Option.DONT_USE_ENTER_AS_OK);
				return RefreshMode.DEFAULT;
			} catch (NamingException | IOException | ContentException | SQLException | EnvironmentException exc) {
				throw new FlowException(exc);
			}
		}
		else {
			return FormManager.super.onAction(inst, id, actionName, parameter);
		}
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
