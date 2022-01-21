package chav1961.elibrary.admin.dialogs;

import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;

@LocaleResourceLocation("i18n:xml:root://chav1961.elibrary.admin.dialogs.SeriesDescriptor/chav1961/elibrary/i18n/i18n.xml")
@LocaleResource(value="nsi.seriesdescriptor",tooltip="nsi.seriesdescriptor.tt",help="help.aboutApplication")
public class SeriesDescriptor implements Cloneable, FormManager<Long, SeriesDescriptor> {
	private final LoggerFacade	logger;
	
	public long		id;
	
	public long		parent;
	
	@LocaleResource(value="nsi.seriesdescriptor.seriesname",tooltip="nsi.seriesdescriptor.seriesname.tt")
	@Format("9.2msL")
	public String	seriesName;
	
	@LocaleResource(value="nsi.seriesdescriptor.seriescomment",tooltip="nsi.seriesdescriptor.seriescomment.tt")
	@Format("9.2msl")
	public String	seriesComment;

	@LocaleResource(value="nsi.seriesdescriptor.isregular",tooltip="nsi.seriesdescriptor.isregular.tt")
	@Format("9.2msl")
	public boolean	isRegular;
	
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
		return getLogger();
	}

	@Override
	public String toString() {
		return "SeriesDescriptor [id=" + id + ", parent=" + parent + ", seriesName=" + seriesName + ", seriesComment=" + seriesComment + "]";
	}
}
