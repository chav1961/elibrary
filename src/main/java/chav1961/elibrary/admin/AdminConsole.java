package chav1961.elibrary.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import chav1961.elibrary.Application;
import chav1961.elibrary.admin.db.AuthorsORMInterface;
import chav1961.elibrary.admin.db.ORMInterface;
import chav1961.elibrary.admin.db.PublishersORMInterface;
import chav1961.elibrary.admin.db.SeriesORMInterface;
import chav1961.elibrary.admin.dialogs.AskPassword;
import chav1961.elibrary.admin.dialogs.AuthorsDescriptor;
import chav1961.elibrary.admin.dialogs.PublishersDescriptor;
import chav1961.elibrary.admin.dialogs.SeriesDescriptor;
import chav1961.elibrary.admin.dialogs.Settings;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.SimpleURLClassLoader;
import chav1961.purelib.basic.SubstitutableProperties;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.CloseCallback;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.interfaces.LoggerFacadeOwner;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.fsys.FileSystemFactory;
import chav1961.purelib.fsys.interfaces.FileSystemInterface;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.i18n.interfaces.SupportedLanguages;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.ContentNodeFilter;
import chav1961.purelib.model.SchemaContainer;
import chav1961.purelib.model.TableContainer;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;
import chav1961.purelib.sql.JDBCUtils;
import chav1961.purelib.sql.model.SQLModelUtils;
import chav1961.purelib.sql.model.SQLModelUtils.ConnectionGetter;
import chav1961.purelib.sql.model.interfaces.DatabaseManagement;
import chav1961.purelib.sql.model.interfaces.DatabaseModelManagement;
import chav1961.purelib.sql.model.SimpleDatabaseManager;
import chav1961.purelib.sql.model.SimpleDatabaseModelManagement;
import chav1961.purelib.sql.model.SimpleDottedVersion;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.JCloseableTab;
import chav1961.purelib.ui.swing.useful.JCloseableTabbedPane;
import chav1961.purelib.ui.swing.useful.JDataBaseTableWithMeta;
import chav1961.purelib.ui.swing.useful.JFileSelectionDialog;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;
import chav1961.purelib.ui.swing.useful.JStateString;

public class AdminConsole extends JFrame implements AutoCloseable, LoggerFacadeOwner, LocaleChangeListener, NodeMetadataOwner {
	private static final long serialVersionUID = 1L;

	public static final String		CONSOLE_TITLE = "console.title";
	public static final String		CONSOLE_HELP = "console.help";
	public static final String		MESSAGEBOX_TITLE = "messagebox.title";
	public static final String		MESSAGEBOX_CONFIRM_CREATION = "messagebox.confirm.creation";
	public static final String		MESSAGEBOX_CONFIRM_UPGRADE = "messagebox.confirm.upgrade";
	
	
	public static final String		MSG_READY = "console.msg.ready";
	public static final String		MSG_CONNECTED = "console.msg.connected";
	public static final String		MSG_DISCONNECTED = "console.msg.disconnected";
	public static final String		MSG_JDBC_DRIVER_NOT_SET = "console.msg.jdbc.driver.not.set";
	
	public static final String		TAB_NSI = "console.tab.nsi";
	
	private final ContentMetadataInterface		mdi;
	private final Localizer						localizer;
	private final SubstitutableProperties		settings;
	private final CloseCallback<AdminConsole>	closeCallback;
	private final JMenuBar						menu;
	private final JCloseableTabbedPane			content;
	private final JStateString					state;
	private final ContentMetadataInterface		dbModel;
	private CallableStatement					unique = null;
	private SimpleURLClassLoader				loader = null;
	private Driver								driver = null;
	private Connection							conn = null;
	private ConnectionGetter					connGetter = null;
	private DatabaseModelManagement<SimpleDottedVersion>	dbMgmt;
	private SimpleDatabaseManager<SimpleDottedVersion>		mgr = null;
	private Map<Class<?>,ORMInterface<?,?>>		orms = new HashMap<>();
	
	public AdminConsole(final ContentMetadataInterface mdi, final Localizer localizer, final SubstitutableProperties settings, final CloseCallback<AdminConsole> closeCallback) throws IOException {
		if (mdi == null) {
			throw new NullPointerException("Metadata interface can't be null");
		}
		else if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (settings == null) {
			throw new NullPointerException("Settings can't be null");
		}
		else if (closeCallback == null) {
			throw new NullPointerException("Close callback can't be null");
		}
		else {
			this.mdi = mdi;
			this.localizer = localizer;
			this.settings = settings;
			this.closeCallback = closeCallback;
			this.menu = SwingUtils.toJComponent(mdi.byUIPath(URI.create("ui:/model/navigation.top.mainmenu")),JMenuBar.class); 
			this.content = new JCloseableTabbedPane();
			this.state = new JStateString(localizer);
			
			localizer.addLocaleChangeListener(this);
			
			getContentPane().add(this.menu, BorderLayout.NORTH);
			getContentPane().add(this.content, BorderLayout.CENTER);
			getContentPane().add(this.state, BorderLayout.SOUTH);

			SwingUtils.assignActionListeners(this.menu,this);
			SwingUtils.assignExitMethod4MainWindow(this,()->exitApplication());
			SwingUtils.centerMainWindow(this,0.75f);
			localizer.addLocaleChangeListener(this);
			fillLocalizedStrings();
			disableMenuOnDisconnect();
			getLogger().message(Severity.info, localizer.getValue(MSG_READY));
			pack();

			try(final InputStream	is = ORMInterface.class.getResourceAsStream("model.json");
				final Reader		rdr = new InputStreamReader(is)) {
				
				this.dbModel = ContentModelFactory.forJsonDescription(rdr);
			}
		}
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) {
		fillLocalizedStrings();
		SwingUtils.refreshLocale(menu, oldLocale, newLocale);
		SwingUtils.refreshLocale(content, oldLocale, newLocale);
	}
	
	@Override
	public LoggerFacade getLogger() {
		return state;
	}

	@Override
	public ContentNodeMetadata getNodeMetadata() {
		return mdi.getRoot();
	}
	
	@Override
	public void close() throws RuntimeException {
		if (conn != null) {
			disconnect();
		}
		localizer.removeLocaleChangeListener(this);
		dispose();
	}

	@OnAction("action:/main.file.connect")
	private void connect() {
		final AskPassword	ap = new AskPassword(state);
		
		if (!settings.containsKey(Settings.PROP_DRIVER)) {
			getLogger().message(Severity.error, localizer.getValue(MSG_JDBC_DRIVER_NOT_SET));
		}
		else if (ask(ap,250,50)) {
			try{this.loader = new SimpleURLClassLoader(new URL[0]);
				this.driver = JDBCUtils.loadJdbcDriver(loader, settings.getProperty(Settings.PROP_DRIVER, File.class));
				this.connGetter = ()-> JDBCUtils.getConnection(driver, 
										settings.getProperty(Settings.PROP_CONN_STRING, URI.class), 
										settings.getProperty(Settings.PROP_SEARCH_USER, String.class), 
										ap.password);
				this.dbMgmt = new SimpleDatabaseModelManagement(state, SeriesORMInterface.class.getResource("model.json").toURI());
				
				final DatabaseManagement<SimpleDottedVersion>	mgmt = new DatabaseManagement<SimpleDottedVersion>() {
					@Override public void onOpen(final Connection conn, final ContentNodeMetadata model) throws SQLException {}
					@Override public void onClose(final Connection conn, final ContentNodeMetadata model) throws SQLException {}
					@Override public void onDowngrade(final Connection conn, final SimpleDottedVersion version, final ContentNodeMetadata model, final SimpleDottedVersion oldVersion, final ContentNodeMetadata oldModel) throws SQLException {}

					@Override
					public SimpleDottedVersion getInitialVersion() throws SQLException {
						return new SimpleDottedVersion("0.0");
					}
					
					@Override
					public SimpleDottedVersion getVersion(final ContentNodeMetadata model) throws SQLException {
						return SQLModelUtils.extractVersionFromModel(model, getInitialVersion());
					}

					@Override
					public SimpleDottedVersion getDatabaseVersion(final Connection conn) throws SQLException {
						return new SimpleDottedVersion("0.0");
					}
					@Override
					public ContentNodeMetadata getDatabaseModel(Connection conn) throws SQLException {
						return null;
					}
					
					@Override
					public void onCreate(final Connection conn, final ContentNodeMetadata model) throws SQLException {
						if (new JLocalizedOptionPane(localizer).confirm(AdminConsole.this, MESSAGEBOX_CONFIRM_CREATION, MESSAGEBOX_TITLE, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
							createDatabase(conn, model);
						}
					}

					@Override
					public void onUpgrade(final Connection conn, final SimpleDottedVersion version, final ContentNodeMetadata model, final SimpleDottedVersion oldVersion, final ContentNodeMetadata oldModel) throws SQLException {
						if (new JLocalizedOptionPane(localizer).confirm(AdminConsole.this, MESSAGEBOX_CONFIRM_UPGRADE, MESSAGEBOX_TITLE, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
							upgradeDatabase(conn, version, model, oldVersion, oldModel);
						}
					}
				};
				this.mgr = new SimpleDatabaseManager<>(state, dbMgmt, connGetter, (c)->mgmt);
				this.conn = this.connGetter.getConnection(); 
				conn.setAutoCommit(true);
				this.unique = conn.prepareCall("{?= call nextval('elibrary.systemseq')}");
				this.unique.registerOutParameter(1, Types.BIGINT);
				
				orms.put(SeriesDescriptor.class, new SeriesORMInterface(getLogger(), conn, ()->getUnique()));
				orms.put(AuthorsDescriptor.class, new AuthorsORMInterface(getLogger(), conn, ()->getUnique()));
				orms.put(PublishersDescriptor.class, new PublishersORMInterface(getLogger(), conn, ()->getUnique()));
				
				((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.connect")).setEnabled(false);
				((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.disconnect")).setEnabled(true);
				((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.nsi")).setEnabled(true);
				enableMenuOnConnect();
				getLogger().message(Severity.info, localizer.getValue(MSG_CONNECTED));
			} catch (ContentException e) {
				getLogger().message(Severity.error, e.getLocalizedMessage());
				this.driver = null;
				if (loader != null) {
					try{loader.close();
					} catch (IOException e1) {
					}
				}
				loader = null;
			} catch (SQLException e) {
				if (e.getCause() != null) {
					getLogger().message(Severity.error, e.getCause().getLocalizedMessage());
				}
				else {
					getLogger().message(Severity.error, e.getLocalizedMessage());
				}
				this.driver = null;
				if (loader != null) {
					try{loader.close();
					} catch (IOException e1) {
					}
				}
				loader = null;
			} catch (EnvironmentException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@OnAction("action:/main.file.disconnect")
	private void disconnect() {
		if (conn != null) {
			content.close();
			try{unique.close();
				orms.remove(SeriesDescriptor.class).close();
				orms.remove(AuthorsDescriptor.class).close();
				orms.remove(PublishersDescriptor.class).close();
				conn.close();
			} catch (SQLException e) {
			} finally {
				conn = null;
			}
			driver = null;
			try{loader.close();
			} catch (IOException e) {
			} finally {
				loader = null;
			}
			disableMenuOnDisconnect();
		}
		getLogger().message(Severity.info, localizer.getValue(MSG_DISCONNECTED));
	}
	
	@OnAction("action:/main.file.nsi")
	private void showNSI() throws ContentException, SQLException {
		final NSITab	tab = new NSITab(localizer, state, dbModel, orms);
		
		JCloseableTab.placeComponentIntoTab(content, TAB_NSI, tab, new JCloseableTab(localizer, TAB_NSI));
	}
	
	
	@OnAction("action:/main.file.quit")
	private void exitApplication() {
		setVisible(false);
		closeCallback.close(this);
	}

	@OnAction("action:/main.tools.database.create")
	private void createDatabase() {
		try {
			createDatabase(conn, dbModel.getRoot());
		} catch (SQLException e) {
			state.message(Severity.error, e, e.getLocalizedMessage());
		}
	}

	@OnAction("action:/main.tools.database.upgrade")
	private void upgradeDatabase() {
		try {
			final ContentNodeMetadata 	meta = mgr.getCurrentDatabaseModel();
			upgradeDatabase(conn, mgr.getManagement().getVersion(dbModel.getRoot()), dbModel.getRoot(), mgr.getManagement().getVersion(meta), meta);
		} catch (SQLException e) {
			state.message(Severity.error, e, e.getLocalizedMessage());
		}
	}

	@OnAction("action:/main.tools.database.backup")
	private void backupDatabase() {
		try(final FileSystemInterface	fsi = FileSystemFactory.createFileSystem(URI.create("fsi:file://./"))) {
			final JFileSelectionDialog	fsd = new JFileSelectionDialog(localizer);
			
			fsd.select(fsi, JFileSelectionDialog.OPTIONS_ALLOW_MKDIR | JFileSelectionDialog.OPTIONS_CAN_SELECT_FILE | JFileSelectionDialog.OPTIONS_CONFIRM_REPLACEMENT| JFileSelectionDialog.OPTIONS_FOR_SAVE, (owner,accepted)->{});
			for (String item : fsd.getSelection()) {
				try(final OutputStream		os = fsi.open(item).create().write();
					final ZipOutputStream	zos = new ZipOutputStream(os)) {
					
					mgr.backup(zos, (i)->true, state);
				}
				break;
			}
		} catch (SQLException | IOException e) {
			state.message(Severity.error, e, e.getLocalizedMessage());
		}
	}
	
	@OnAction("action:/main.tools.database.restore")
	private void restoreDatabase() {
		try(final FileSystemInterface	fsi = FileSystemFactory.createFileSystem(URI.create("fsi:file://./"))) {
			final JFileSelectionDialog	fsd = new JFileSelectionDialog(localizer);
			
			fsd.select(fsi, JFileSelectionDialog.OPTIONS_CAN_SELECT_FILE | JFileSelectionDialog.OPTIONS_FOR_OPEN | JFileSelectionDialog.OPTIONS_FILE_MUST_EXISTS, (owner,accepted)->{});
			for (String item : fsd.getSelection()) {
				try(final InputStream		is = fsi.open(item).read();
					final ZipInputStream	zis = new ZipInputStream(is)) {
					
					mgr.restore(zis, (i)->true, state);
				}
				break;
			}
		} catch (IOException e) {
			state.message(Severity.error, e, e.getLocalizedMessage());
		}
	}
	
	@OnAction("action:/main.tools.settings")
	private void settings() {
		final Settings	s = new Settings(getLogger(), this);
	
		s.load(settings);
		if (ask(s, 400,200)) {
			s.save(settings);
		}
	}

	@OnAction("action:builtin:/builtin.languages")
	private void changeLang (final Hashtable<String,String[]> langs) throws LocalizationException {
		localizer.setCurrentLocale(SupportedLanguages.valueOf(langs.get("lang")[0]).getLocale());
	}
	
	@OnAction("action:/main.help.about")
	private void showAbout() {
		SwingUtils.showAboutScreen(this, localizer, Application.HELP_TITLE, Application.HELP_CONTENT, URI.create("root://chav1961.elibrary.Application/chav1961/elibrary/avatar.jpg"), new Dimension(300,300));
	}
	
	private <T> boolean ask(final T instance, final int width, final int height) {
		try{final ContentMetadataInterface	mdi = ContentModelFactory.forAnnotatedClass(instance.getClass());
		
			try(final AutoBuiltForm<T,?>	abf = new AutoBuiltForm<>(mdi, localizer, PureLibSettings.INTERNAL_LOADER, instance, (FormManager<Object,T>)instance)) {
				
				((ModuleAccessor)instance).allowUnnamedModuleAccess(abf.getUnnamedModules());
				abf.setPreferredSize(new Dimension(width,height));
				return AutoBuiltForm.ask(this,localizer,abf);
			}
		} catch (ContentException e) {
			getLogger().message(Severity.error,e.getLocalizedMessage());
			return false;
		} 
	}

	private void createDatabase(final Connection conn, final ContentNodeMetadata model) throws SQLException {
		SQLModelUtils.createDatabaseByModel(conn, model);
	}
	
	private void upgradeDatabase(final Connection conn, final SimpleDottedVersion newVersion, final ContentNodeMetadata newModel, final SimpleDottedVersion oldVersion, final ContentNodeMetadata oldModel) {
		// TODO Auto-generated method stub
	}
	
	private boolean filterModel(final ContentNodeMetadata meta, final Set<String> fields2Exclude) {
		return meta.getType() == TableContainer.class || !fields2Exclude.contains(meta.getName().toUpperCase());
	}

	private long getUnique() throws SQLException {
		unique.executeUpdate();
		return unique.getLong(1);
	}
	
	private void fillLocalizedStrings() {
		setTitle(localizer.getValue(CONSOLE_TITLE));
	}

	private void enableMenuOnConnect() {
		((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.connect")).setEnabled(false);
		((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.disconnect")).setEnabled(true);
		((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.nsi")).setEnabled(true);
		((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.tools.database")).setEnabled(true);
	}

	private void disableMenuOnDisconnect() {
		((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.connect")).setEnabled(true);
		((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.disconnect")).setEnabled(false);
		((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.nsi")).setEnabled(false);
		((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.tools.database")).setEnabled(false);
	}
}


