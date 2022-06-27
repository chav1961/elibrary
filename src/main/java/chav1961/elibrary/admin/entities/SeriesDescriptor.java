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

@LocaleResourceLocation("i18n:xml:root://chav1961.elibrary.admin.entities.SeriesDescriptor/chav1961/elibrary/i18n/i18n.xml")
@LocaleResource(value="elibrary.bookseries",tooltip="elibrary.bookseries.tt",help="help.aboutApplication")
public class SeriesDescriptor implements Cloneable, FormManager<Long, SeriesDescriptor>, ModuleAccessor {
	private final LoggerFacade	logger;
	
	public long		id;
	
	public long		parent;
	
	@LocaleResource(value="elibrary.bookseries.bs_Name",tooltip="elibrary.bookseries.bs_Name.tt")
	@Format("9.2msL")
	public String	seriesName = "";
	
	@LocaleResource(value="elibrary.bookseries.bs_Comment",tooltip="elibrary.bookseries.bs_Comment.tt")
	@Format("9.2msl")
	public String	seriesComment = "";

	public SeriesDescriptor(final LoggerFacade logger) {
		this.logger = logger;
	}
	
	@Override
	public SeriesDescriptor clone() throws CloneNotSupportedException {
		return (SeriesDescriptor) super.clone();
	}

	@Override
	public RefreshMode onField(SeriesDescriptor inst, Long id, String fieldName, Object oldValue, boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public RefreshMode onRecord(final RecordAction action, final SeriesDescriptor oldRecord, final Long oldId, final SeriesDescriptor newRecord, final Long newId) throws FlowException, LocalizationException {
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
				newRecord.seriesName = "New series";
				newRecord.seriesComment = "";
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
		return "SeriesDescriptor [id=" + id + ", parent=" + parent + ", seriesName=" + seriesName + ", seriesComment=" + seriesComment + "]";
	}
}
