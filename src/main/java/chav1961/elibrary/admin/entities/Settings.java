package chav1961.elibrary.admin.entities;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import javax.swing.JOptionPane;

import chav1961.elibrary.admin.AdminConsole;
import chav1961.elibrary.admin.indexer.LuceneIndexer;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.SimpleURLClassLoader;
import chav1961.purelib.basic.SubstitutableProperties;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.interfaces.LocaleResource;
import chav1961.purelib.i18n.interfaces.LocaleResourceLocation;
import chav1961.purelib.i18n.interfaces.LocalizerOwner;
import chav1961.purelib.i18n.interfaces.SupportedLanguages;
import chav1961.purelib.sql.JDBCUtils;
import chav1961.purelib.sql.model.SimpleDatabaseManager;
import chav1961.purelib.ui.interfaces.Action;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.interfaces.Format;
import chav1961.purelib.ui.interfaces.RefreshMode;
import chav1961.purelib.ui.swing.JButtonWithMeta;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;

@LocaleResourceLocation("i18n:xml:root://chav1961.elibrary.admin.entities.Settings/chav1961/elibrary/i18n/i18n.xml")
@LocaleResource(value="settings.title",tooltip="settings.title.tt",help="settings.title.help")
@Action(resource=@LocaleResource(value="settings.test",tooltip="settings.test.tt"),actionString="test")
public class Settings implements FormManager<Object,Settings>, ModuleAccessor {
	public static final String	PROP_DRIVER = "driver";	
	public static final String	PROP_DRIVER_DEFAULT = "./current.jar";	
	public static final String	PROP_CONN_STRING = "connString";	
	public static final String	PROP_CONN_STRING_DEFAULT = "jdbc:postgresql://localhost:5432/postgres";	
	public static final String	PROP_ADMIN_USER = "adminUser";	
	public static final String	PROP_ADMIN_USER_DEFAULT = "admin";	
	public static final String	PROP_SEARCH_USER = "searchUser";	
	public static final String	PROP_SEARCH_USER_DEFAULT = "user";	
	public static final String	PROP_SEARCH_PASSWORD = "searchPassword";	
	public static final char[]	PROP_SEARCH_PASSWORD_DEFAULT = "password".toCharArray();	
	public static final String	PROP_DEFAULT_LANG = "defaultLanguage";	
	public static final String	PROP_DEFAULT_LANG_DEFAULT = Locale.getDefault().getLanguage();
	public static final String	PROP_INDEXER_DIR = "indexerDir";	
	public static final String	PROP_INDEXER_DIR_DEFAULT = LuceneIndexer.LUCENE_DEFAULT_INDEXING_DIR;	

	public static final String	KEY_VERSIONING_MISSING = "settings.versioning.missing";	
	public static final String	KEY_VERSIONING_MISSING_TITLE = "settings.versioning.missing.title";	
	
	@LocaleResource(value="settings.jdbcdriver",tooltip="settings.jdbcdriver.tt")
	@Format("30ms")
	public File		jdbcDriver = new File(PROP_DRIVER_DEFAULT);
	
	@LocaleResource(value="settings.connstring",tooltip="settings.connstring.tt")
	@Format("30ms")
	public URI		connectionString = URI.create(PROP_CONN_STRING_DEFAULT);

	@LocaleResource(value="settings.adminuser",tooltip="settings.adminuser.tt")
	@Format("30ms")
	public String	adminUser = PROP_ADMIN_USER_DEFAULT;

	@LocaleResource(value="settings.searchuser",tooltip="settings.searchuser.tt")
	@Format("30ms")
	public String	searchUser = PROP_SEARCH_USER_DEFAULT;

	@LocaleResource(value="settings.searchpassword",tooltip="settings.searchpassword.tt")
	@Format("30ms")
	public char[]	searchPassword = PROP_SEARCH_PASSWORD_DEFAULT;

	@LocaleResource(value="settings.defaultlang",tooltip="settings.defaultlang.tt")
	@Format("30ms")
	public SupportedLanguages	defaultLang = SupportedLanguages.valueOf(PROP_DEFAULT_LANG_DEFAULT);

	@LocaleResource(value="settings.defaultlang",tooltip="settings.defaultlang.tt")
	@Format("30ms")
	public File		indexerDir = new File(LuceneIndexer.LUCENE_DEFAULT_INDEXING_DIR);
	
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
					try(final SimpleURLClassLoader	loader = new SimpleURLClassLoader(new URL[0]);
						final Connection			conn = JDBCUtils.getConnection(JDBCUtils.loadJdbcDriver(loader, jdbcDriver), connectionString, searchUser, searchPassword)) {

						if (!SimpleDatabaseManager.isDatabasePrepared4Versioning(conn, conn.getSchema())) {
							new JLocalizedOptionPane(SwingUtils.getNearestOwner(console, LocalizerOwner.class).getLocalizer()).message(null, KEY_VERSIONING_MISSING, KEY_VERSIONING_MISSING_TITLE, JOptionPane.INFORMATION_MESSAGE);
						}
						button.markOK(true);
					} catch (ContentException | SQLException | IOException e) {
						button.markOK(false);
					}
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
			indexerDir = props.getProperty(PROP_INDEXER_DIR, File.class, LuceneIndexer.LUCENE_DEFAULT_INDEXING_DIR);
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
			props.setProperty(PROP_INDEXER_DIR, indexerDir.getAbsolutePath());
		}
	}
	
	public static SubstitutableProperties getDefaultSettings() {
		final SubstitutableProperties	result = new SubstitutableProperties();

		result.setProperty(PROP_DRIVER, PROP_DRIVER_DEFAULT);	
		result.setProperty(PROP_CONN_STRING, PROP_CONN_STRING_DEFAULT);	
		result.setProperty(PROP_ADMIN_USER, PROP_ADMIN_USER_DEFAULT);	
		result.setProperty(PROP_SEARCH_USER, PROP_SEARCH_USER_DEFAULT);	
		result.setProperty(PROP_SEARCH_PASSWORD, new String(PROP_SEARCH_PASSWORD_DEFAULT));	
		result.setProperty(PROP_DEFAULT_LANG, PROP_DEFAULT_LANG_DEFAULT);
		result.setProperty(PROP_INDEXER_DIR, PROP_INDEXER_DIR_DEFAULT);
		return result;
	}
}
