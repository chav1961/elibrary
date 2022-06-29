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

@LocaleResourceLocation("i18n:xml:root://chav1961.elibrary.admin.entities.Query/chav1961/elibrary/i18n/i18n.xml")
@LocaleResource(value="query.title",tooltip="query.title.tt",help="settings.title.help")
public class Query implements FormManager<Object, Query>, ModuleAccessor {
	private final LoggerFacade	logger;

	@LocaleResource(value="query.anywhere",tooltip="query.anywhere.tt")
	@Format("30s")
	public String	anywhere = "";

	@LocaleResource(value="query.series",tooltip="query.series.tt")
	@Format("30s")
	public String	series = "";

	@LocaleResource(value="query.code",tooltip="query.code.tt")
	@Format("30s")
	public String	code = "";
	
	@LocaleResource(value="query.title",tooltip="query.title.tt")
	@Format("30s")
	public String	title = "";

	@LocaleResource(value="query.authors",tooltip="query.authors.tt")
	@Format("30s")
	public String	authors = "";

	@LocaleResource(value="query.publisher",tooltip="query.publisher.tt")
	@Format("30s")
	public String	publisher = "";
	
	@LocaleResource(value="query.comment",tooltip="query.comment.tt")
	@Format("30s")
	public String	comment = "";

	@LocaleResource(value="query.tags",tooltip="query.tags.tt")
	@Format("30s")
	public String	tags = "";
	
	public Query(final LoggerFacade logger) {
		this.logger = logger;
	}

	@Override
	public LoggerFacade getLogger() {
		return logger;
	}

	@Override
	public RefreshMode onField(Query inst, Object id, String fieldName, Object oldValue, boolean beforeCommit) throws FlowException, LocalizationException {
		return RefreshMode.DEFAULT;
	}

	@Override
	public void allowUnnamedModuleAccess(Module... unnamedModules) {
		for (Module item : unnamedModules) {
			this.getClass().getModule().addExports(this.getClass().getPackageName(),item);
		}
	}
	
	public String getQueryString() {
		return anywhere;
	}
}
