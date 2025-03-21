package chav1961.elibrary;

import java.awt.Desktop;
import java.awt.Dimension;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import chav1961.elibrary.admin.db.ORMInterface;
import chav1961.elibrary.admin.entities.Settings;
import chav1961.purelib.basic.ArgParser;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.SimpleInitialContextFactory;
import chav1961.purelib.basic.SubstitutableProperties;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.CommandLineParametersException;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.interfaces.LoggerFacadeOwner;
import chav1961.purelib.fsys.interfaces.FileSystemInterface;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.i18n.interfaces.SupportedLanguages;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;
import chav1961.purelib.ui.swing.useful.JSystemTray;

public class Application implements Closeable, LoggerFacadeOwner {
	public static final UUID	APPLICATION_UUID = UUID.fromString("28d63a54-3d1a-478e-bb2c-610bd5ab6259");
	public static final String	ARG_HELP_PORT = "helpPort";
	public static final String	ARG_PROPFILE_LOCATION = "prop";
	
	public static final String	APP_NAME = "application.name";
	public static final String	APP_TOOLTIP = "application.tooltip";
	public static final String	APP_HELP = "application.help";
	public static final String	APP_NOTE_STARTED = "application.note.started";
	public static final String	HELP_TITLE = "application.help.title";
	public static final String	HELP_CONTENT = "application.help.content";
	public static final String	MSG_NO_PROP_FILE_TITLE = "application.noPropFile.title";
	public static final String	MSG_NO_PROP_FILE_MESSAGE = "application.noPropFile.message";

	public static final String	CONTENT_PATH = "/content";
	
	private static int						portNumber = 0;
	
	private final ContentMetadataInterface	xda;
	private final Localizer					localizer;
	private final File						propFileLocation;
	private final CountDownLatch			latch;
	private final JSystemTray				tray;
	private final SubstitutableProperties	settings;
	private final LocaleChangeListener		lcl;
	private boolean							settingsChanged = false;
	
	public Application(final ContentMetadataInterface xda, final Localizer parentLocalizer, final File propFileLocation, final CountDownLatch latch) throws EnvironmentException, CommandLineParametersException, IOException {
		if (xda == null) {
			throw new NullPointerException("Application descriptor can't be null");
		}
		else if (parentLocalizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (propFileLocation == null) {
			throw new NullPointerException("Properties file location can't be null");
		}
		else if (latch == null) {
			throw new NullPointerException("CountDownLatch can't be null");
		}
		else {
			final JPopupMenu	trayMenu = SwingUtils.toJComponent(xda.byUIPath(URI.create("ui:/model/navigation.top.traymenu")),JPopupMenu.class);
			
			this.xda = xda;
			this.localizer = LocalizerFactory.getLocalizer(xda.getRoot().getLocalizerAssociated());
			this.propFileLocation = propFileLocation;
			this.latch = latch;
			if (!this.propFileLocation.exists()) {
				if (new JLocalizedOptionPane(this.localizer).confirm(null, MSG_NO_PROP_FILE_MESSAGE, MSG_NO_PROP_FILE_TITLE, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
					this.settings = Settings.getDefaultSettings();
				}
				else {
					throw new CommandLineParametersException("Configuration file ["+this.propFileLocation.getAbsolutePath()+"] not typed, not found or not accessible for you");
				}
			}
			else {
				this.settings = SubstitutableProperties.of(propFileLocation); 
			}
			
			settings.addPropertyChangeListener((e)->{
				settingsChanged = true;
				PureLibSettings.PURELIB_LOCALIZER.setCurrentLocale(settings.getProperty(Settings.PROP_DEFAULT_LANG, SupportedLanguages.class).getLocale());
			});
			
			SwingUtils.assignActionListeners(trayMenu, (e)->callTray(e.getActionCommand()));
			
			try{this.tray = new JSystemTray(localizer, APP_NAME, this.getClass().getResource("tray.png").toURI(), APP_TOOLTIP, trayMenu, false);
				
				this.tray.addActionListener((e)->showConsole(portNumber));
				this.lcl = (oldLocale,newLocale)->tray.localeChanged(oldLocale, newLocale);
				PureLibSettings.PURELIB_LOCALIZER.addLocaleChangeListener(lcl);
			} catch (URISyntaxException exc) {
				throw new EnvironmentException(exc);
			}
			PureLibSettings.PURELIB_LOCALIZER.setCurrentLocale(settings.getProperty(Settings.PROP_DEFAULT_LANG, SupportedLanguages.class, Locale.getDefault().getLanguage()).getLocale());
		}
	}

	@Override
	public void close() throws IOException {
		if (settingsChanged) {
			try(final OutputStream	fos = new FileOutputStream(propFileLocation)) {
				
				settings.store(fos, "");
			} catch (IOException e) {
			}
		}
		localizer.removeLocaleChangeListener(lcl);
		tray.close();
	}

	@Override
	public LoggerFacade getLogger() {
		return tray;
	}

	public Localizer getLocalizer() {
		return localizer;
	}

	public SubstitutableProperties getSettings() {
		return settings;
	}
	
	private void callTray(final String action) {
		switch (action) {
			case "action:/tray.site" :
				showSite();
				break;
			case "action:/tray.show" :
				showConsole(portNumber);
				break;
			case "action:/tray.about" :
				showAbout();
				break;
			case "action:/tray.quit" :
				latch.countDown();
				break;
			default : throw new UnsupportedOperationException("Action string [" + action + "] is not supported yet");
		}
	}

	private void showSite() {
		if (Desktop.isDesktopSupported()) {
			try{Desktop.getDesktop().browse(URI.create("http://localhost:"+portNumber+"/static/index.html"));
			} catch (IOException e) {
				tray.message(Severity.error, e, e.getLocalizedMessage());
			}
		}
		else {
			tray.message(Severity.error, "Desktop is not supported");
		}
	}

	private void showConsole(final int sitePort) {
	}
	
	private void showAbout() {
	}
	
	public static void main(String[] args) {
		System.setProperty("java.naming.factory.initial", SimpleInitialContextFactory.class.getName());
		
		try{final ArgParser						parser = new ApplicationArgParser().parse(args);
			try(final InputStream				is = Application.class.getResourceAsStream("application.xml")) {
				final ContentMetadataInterface	xda = ContentModelFactory.forXmlDescription(is);
				final CountDownLatch			latch = new CountDownLatch(1);
				final Localizer					localizer = LocalizerFactory.getLocalizer(xda.getRoot().getLocalizerAssociated());
				final Map<Class<?>,ORMInterface<?,?>> orms = new HashMap<>();

				PureLibSettings.PURELIB_LOCALIZER.push(localizer);
				
				try(final Application			app = new Application(xda, localizer, parser.getValue(ARG_PROPFILE_LOCATION, File.class), latch)) {
					app.getLogger().message(Severity.info, app.getLocalizer().getValue(APP_NOTE_STARTED), portNumber);
					
					app.showConsole(portNumber);
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (IOException | ContentException | EnvironmentException e) {
				e.printStackTrace();
			}
		} catch (CommandLineParametersException exc) {
//			exc.printStackTrace();
			System.exit(128);
		}
	}

	private static class ApplicationArgParser extends ArgParser {
		private static final ArgParser.AbstractArg[]	KEYS = {
			new IntegerArg(ARG_HELP_PORT, true, "Help port to use for help browser", 0),
			new FileArg(ARG_PROPFILE_LOCATION, false, "Property file location", "./.elibrary.properties")
		};
		
		private ApplicationArgParser() {
			super(KEYS);
		}
	}
}
