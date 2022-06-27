package chav1961.elibrary.admin.entities;


import java.io.File;
import java.net.URI;
import java.util.Locale;

import chav1961.elibrary.admin.AdminConsole;
import chav1961.purelib.basic.SubstitutableProperties;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.SupportedLanguages;
import chav1961.purelib.sql.JDBCUtils;
import chav1961.purelib.ui.interfaces.Action;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.swing.JButtonWithMeta;

@LocaleResourceLocation("i18n:xml:root://chav1961.elibrary.admin.entities.Settings/chav1961/elibrary/i18n/i18n.xml")
@LocaleResource(value="settings.title",tooltip="settings.title.tt",help="settings.title.help")
@Action(resource=@LocaleResource(value="settings.test",tooltip="settings.test.tt"),actionString="test")
public class Settings implements FormManager<Object,Settings>, ModuleAccessor {
	public static final String	PROP_DRIVER = "driver";	
	public static final String	PROP_CONN_STRING = "connString";	
	public static final String	PROP_ADMIN_USER = "adminUser";	
	public static final String	PROP_SEARCH_USER = "searchUser";	
	public static final String	PROP_SEARCH_PASSWORD = "searchPassword";	
	public static final String	PROP_DEFAULT_LANG = "defaultLanguage";	

	@LocaleResource(value="settings.jdbcdriver",tooltip="settings.jdbcdriver.tt")
	@Format("30ms")
	public File		jdbcDriver = new File("./current.jar");
	
	@LocaleResource(value="settings.connstring",tooltip="settings.connstring.tt")
	@Format("30ms")
	public URI		connectionString = URI.create("jdbc:postgresql://localhost:5432/postgres");

	@LocaleResource(value="settings.adminuser",tooltip="settings.adminuser.tt")
	@Format("30ms")
	public String	adminUser = "admin";

	@LocaleResource(value="settings.searchuser",tooltip="settings.searchuser.tt")
	@Format("30ms")
	public String	searchUser = "user";

	@LocaleResource(value="settings.searchpassword",tooltip="settings.searchpassword.tt")
	@Format("30ms")
	public char[]	searchPassword = "password".toCharArray();

	@LocaleResource(value="settings.defaultlang",tooltip="settings.defaultlang.tt")
	@Format("30ms")
	public SupportedLanguages	defaultLang = SupportedLanguages.valueOf(Locale.getDefault().getLanguage());
	
	private final LoggerFacade	logger;
	private final AdminConsole	console;
	
	public Settings(final LoggerFacade 	logger, final AdminConsole console) {
		this.logger = logger;
		this.console = console;
	}
	
	@Override
	public RefreshMode onField(final Settings inst, final Object id, final String fieldName, final Object oldValue, final boolean beforeCommit) throws FlowException, LocalizationException {
		switch (fieldName) {
			case "jdbcDriver" 		:
				return JDBCUtils.isJDBCDriverValid(jdbcDriver, getLogger()) ? RefreshMode.DEFAULT : RefreshMode.REJECT; 
			case "connectionString" :
				return JDBCUtils.isConnectionStringValid(connectionString, getLogger()) ? RefreshMode.DEFAULT : RefreshMode.REJECT; 
			default :
				return RefreshMode.DEFAULT;
		}
	}

	@Override
	public RefreshMode onAction(final Settings inst, final Object id, final String actionName, final Object... parameter) throws FlowException, LocalizationException {
		switch(actionName) {
			case "app:action:/Settings.test" :
				final JButtonWithMeta	button = (JButtonWithMeta)parameter[0];
				
				if (JDBCUtils.testConnection(jdbcDriver, connectionString, searchUser, searchPassword, logger)) {
					button.markOK(true);
				}
				else {
					button.markOK(false);
				}
				return RefreshMode.DEFAULT;
			default :
				return RefreshMode.REJECT;
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

	public void load(final SubstitutableProperties props) throws NullPointerException {
		if (props == null) {
			throw new NullPointerException("Properties to load content from can't be null"); 
		}
		else {
			jdbcDriver = props.getProperty(PROP_DRIVER, File.class, "./current.jar");
			connectionString = props.getProperty(PROP_CONN_STRING, URI.class, "jdbc:postgres://localhost:5432");
			adminUser = props.getProperty(PROP_ADMIN_USER, String.class, "admin");
			searchUser = props.getProperty(PROP_SEARCH_USER, String.class, "user");
			searchPassword = props.getProperty(PROP_SEARCH_PASSWORD, char[].class, "password");
			defaultLang = props.getProperty(PROP_DEFAULT_LANG, SupportedLanguages.class, Locale.getDefault().getLanguage());
		}
	}
	
	public void save(final SubstitutableProperties props) throws NullPointerException {
		if (props == null) {
			throw new NullPointerException("Properties to load content from can't be null"); 
		}
		else {
			props.setProperty(PROP_DRIVER, jdbcDriver.getAbsolutePath());
			props.setProperty(PROP_CONN_STRING, connectionString.toString());
			props.setProperty(PROP_ADMIN_USER, adminUser);
			props.setProperty(PROP_SEARCH_USER, searchUser);
			props.setProperty(PROP_SEARCH_PASSWORD, new String(searchPassword));
			props.setProperty(PROP_DEFAULT_LANG, defaultLang.name());
		}
	}
}
