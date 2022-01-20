package chav1961.elibrary.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import chav1961.elibrary.Application;
import chav1961.elibrary.admin.db.DbManager;
import chav1961.elibrary.admin.db.ORMInterface;
import chav1961.elibrary.admin.db.SeriesDescriptorMgr;
import chav1961.elibrary.admin.db.SeriesORMInterface;
import chav1961.elibrary.admin.dialogs.AskPassword;
import chav1961.elibrary.admin.dialogs.Series;
import chav1961.elibrary.admin.dialogs.SeriesDescriptor;
import chav1961.elibrary.admin.dialogs.Settings;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.SimpleURLClassLoader;
import chav1961.purelib.basic.SubstitutableProperties;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.CloseCallback;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.interfaces.LoggerFacadeKeeper;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.SupportedLanguages;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.ContentNodeFilter;
import chav1961.purelib.model.TableContainer;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.sql.JDBCUtils;
import chav1961.purelib.sql.interfaces.InstanceManager;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.JDataBaseTableWithMeta;
import chav1961.purelib.ui.swing.useful.JStateString;

public class AdminConsole extends JFrame implements AutoCloseable, LoggerFacadeKeeper, LocaleChangeListener {
	private static final long serialVersionUID = 1L;

	public static final String		CONSOLE_TITLE = "console.title";
	public static final String		CONSOLE_HELP = "console.help";
	public static final String		MSG_READY = "console.msg.ready";
	public static final String		MSG_CONNECTED = "console.msg.connected";
	public static final String		MSG_DISCONNECTED = "console.msg.disconnected";
	
	public static final String		TAB_BOOK_SERIES = "tab.bookSeries";
	
	private final ContentMetadataInterface		mdi;
	private final Localizer						localizer;
	private final SubstitutableProperties		settings;
	private final CloseCallback<AdminConsole>	closeCallback;
	private final JMenuBar						menu;
	private final JTabbedPane					content;
	private final JStateString					state;
	private final ContentMetadataInterface		dbModel;
	private CallableStatement					unique = null;
	private SimpleURLClassLoader				loader = null;
	private Driver								driver = null;
	private Connection							conn = null;
	private DbManager							mgr = null;
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
			this.content = new JTabbedPane();
			this.state = new JStateString(localizer);
			
			localizer.addLocaleChangeListener(this);
			
			getContentPane().add(this.menu, BorderLayout.NORTH);
			getContentPane().add(this.content, BorderLayout.CENTER);
			getContentPane().add(this.state, BorderLayout.SOUTH);

			((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.disconnect")).setEnabled(false);
			((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.nsi")).setEnabled(false);
			
			SwingUtils.assignActionListeners(this.menu,this);
			SwingUtils.assignExitMethod4MainWindow(this,()->exitApplication());
			SwingUtils.centerMainWindow(this,0.75f);
			localizer.addLocaleChangeListener(this);
			fillLocalizedStrings();
			getLogger().message(Severity.info, localizer.getValue(MSG_READY));
			pack();

			try(final InputStream	is = DbManager.class.getResourceAsStream("model.json");
				final Reader		rdr = new InputStreamReader(is)) {
				
				this.dbModel = ContentModelFactory.forJsonDescription(rdr);
			}
		}
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) {
		fillLocalizedStrings();
		SwingUtils.refreshLocale(menu, oldLocale, newLocale);
	}
	
	@Override
	public LoggerFacade getLogger() {
		return state;
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
		
		if (ask(ap,250,50)) {
			try{this.loader = new SimpleURLClassLoader(new URL[0]);
				this.driver = JDBCUtils.loadJdbcDriver(loader, settings.getProperty(Settings.PROP_DRIVER, File.class));
				this.conn = JDBCUtils.getConnection(driver, 
									settings.getProperty(Settings.PROP_CONN_STRING, URI.class), 
									settings.getProperty(Settings.PROP_SEARCH_USER, String.class), 
									ap.password);
				conn.setAutoCommit(true);
				this.unique = conn.prepareCall("{?= call nextval('elibrary.systemseq')}");
				this.unique.registerOutParameter(1, Types.BIGINT);
				
				((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.connect")).setEnabled(false);
				((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.disconnect")).setEnabled(true);
				((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.nsi")).setEnabled(true);
				getLogger().message(Severity.info, localizer.getValue(MSG_CONNECTED));
			} catch (ContentException | SQLException e) {
				getLogger().message(Severity.error, e.getLocalizedMessage());
				this.driver = null;
				if (loader != null) {
					try{loader.close();
					} catch (IOException e1) {
					}
				}
				loader = null;
			}
		}
	}
	
	@OnAction("action:/main.file.disconnect")
	private void disconnect() {
		if (conn != null) {
			try{unique.close();
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
		}
		((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.connect")).setEnabled(true);
		((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.disconnect")).setEnabled(false);
		((JMenuItem)SwingUtils.findComponentByName(menu, "menu.main.file.nsi")).setEnabled(false);
		getLogger().message(Severity.info, localizer.getValue(MSG_DISCONNECTED));
	}
	
	@OnAction("action:/main.file.nsi.series")
	private void supportBookSeries() throws ContentException, SQLException {
		final ContentNodeMetadata		dbMd = dbModel.byApplicationPath(URI.create("app:table:/elibrary.bookseries"))[0];
		final Set<String>				fieldsFiltered = new HashSet<>(Arrays.asList("BS_ID","BS_PARENT"));
		final ContentNodeMetadata		dbMdFiltered = new ContentNodeFilter(dbMd, (item)->filterModel(item, fieldsFiltered));
		final JDataBaseTableWithMeta<Long, SeriesDescriptor>	table = new JDataBaseTableWithMeta<>(dbMdFiltered, localizer);
		final JScrollPane				pane = new JScrollPane(table);
		
		content.addTab(TAB_BOOK_SERIES, pane);
		if (!orms.containsKey(SeriesDescriptor.class)) {
			orms.put(SeriesDescriptor.class, new SeriesORMInterface(state, conn, ()->getUnique()));
		}
		final SeriesORMInterface		soi = (SeriesORMInterface) orms.get(SeriesDescriptor.class);
		table.assignResultSetAndManagers(soi.getResultSet(), soi.getFormManager(), soi.getInstanceManager());
		table.requestFocusInWindow();
		System.err.println("Sderies");
	}
	
	
	@OnAction("action:/main.file.quit")
	private void exitApplication() {
		setVisible(false);
		closeCallback.close(this);
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
		
			try(final AutoBuiltForm<T>		abf = new AutoBuiltForm<T>(mdi, localizer, PureLibSettings.INTERNAL_LOADER, instance, (FormManager<Object,T>)instance)) {
				
				((ModuleAccessor)instance).allowUnnamedModuleAccess(abf.getUnnamedModules());
				abf.setPreferredSize(new Dimension(width,height));
				return AutoBuiltForm.ask(this,localizer,abf);
			}
		} catch (ContentException e) {
			getLogger().message(Severity.error,e.getLocalizedMessage());
			return false;
		} 
	}
	
	private DbManager getDbManager() {
		return null;
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
}
