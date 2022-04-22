package chav1961.elibrary;

import java.awt.Dimension;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import chav1961.elibrary.admin.AdminConsole;
import chav1961.elibrary.admin.dialogs.Settings;
import chav1961.purelib.basic.ArgParser;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.SubstitutableProperties;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.CommandLineParametersException;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.interfaces.LoggerFacadeOwner;
import chav1961.purelib.i18n.LocalizerFactory;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.i18n.interfaces.SupportedLanguages;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.nanoservice.NanoServiceFactory;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.JSystemTray;

public class Application implements Closeable, LoggerFacadeOwner {
	public static final String	ARG_HELP_PORT = "helpPort";
	public static final String	ARG_PROPFILE_LOCATION = "prop";
	
	public static final String	APP_NAME = "application.name";
	public static final String	APP_TOOLTIP = "application.tooltip";
	public static final String	APP_HELP = "application.help";
	public static final String	APP_NOTE_STARTED = "application.note.started";
	public static final String	HELP_TITLE = "application.help.title";
	public static final String	HELP_CONTENT = "application.help.content";

	private final ContentMetadataInterface	xda;
	private final Localizer					localizer;
	private final File						propFileLocation;
	private final CountDownLatch			latch;
	private final JSystemTray				tray;
	private final SubstitutableProperties	settings = new SubstitutableProperties();
	private final LocaleChangeListener		lcl;
	private boolean							settingsChanged = false;
	private volatile AdminConsole			console = null;
	
	public Application(final ContentMetadataInterface xda, final Localizer parentLocalizer, final File propFileLocation, final CountDownLatch latch) throws EnvironmentException, CommandLineParametersException {
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
			this.localizer =  LocalizerFactory.getLocalizer(xda.getRoot().getLocalizerAssociated());
			this.propFileLocation = propFileLocation;
			this.latch = latch;
			
			if (propFileLocation.exists() && propFileLocation.isFile() && propFileLocation.canRead()) {
				try(final InputStream	fis = new FileInputStream(propFileLocation)) {
					
					settings.load(fis);
				} catch (IOException e) {
					throw new CommandLineParametersException("Property file ["+propFileLocation.getAbsolutePath()+"] - I/O error while reading: "+e.getLocalizedMessage());
				}
			}
			settings.addPropertyChangeListener((e)->{
				settingsChanged = true;
				localizer.setCurrentLocale(settings.getProperty(Settings.PROP_DEFAULT_LANG, SupportedLanguages.class).getLocale());
			});
			
			SwingUtils.assignActionListeners(trayMenu, (e)->callTray(e.getActionCommand()));
			
			try{this.tray = new JSystemTray(localizer, APP_NAME, this.getClass().getResource("tray.png").toURI(), APP_TOOLTIP, trayMenu, false);
				
				this.tray.addActionListener((e)->showConsole());
				this.lcl = (oldLocale,newLocale)->tray.localeChanged(oldLocale, newLocale);
				localizer.addLocaleChangeListener(lcl);
			} catch (URISyntaxException exc) {
				throw new EnvironmentException(exc);
			}
			localizer.setCurrentLocale(settings.getProperty(Settings.PROP_DEFAULT_LANG, SupportedLanguages.class, Locale.getDefault().getLanguage()).getLocale());
		}
	}

	@Override
	public void close() throws IOException {
		if (console != null) {
			console.close();
		}
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
	
	private void callTray(final String action) {
		switch (action) {
			case "action:/tray.show" :
				showConsole();
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

	private void showConsole() {
		if (console != null) {
			console.toFront();
		}
		else {
			try{console = new AdminConsole(xda, localizer, settings, (cons)->SwingUtilities.invokeLater(()->{cons.close(); console = null;}));
				console.setVisible(true);
			} catch (IOException e) {
				tray.message(Severity.error, e.getLocalizedMessage());
			}
		}
	}
	
	private void showAbout() {
		SwingUtils.showAboutScreen(console, localizer, HELP_TITLE, HELP_CONTENT, URI.create("root://chav1961.elibrary.Application/chav1961/elibrary/avatar.jpg"), new Dimension(300,300));
	}
	
	public static void main(String[] args) {
		try{final ArgParser						parser = new ApplicationArgParser().parse(args);
			final SubstitutableProperties		props = new SubstitutableProperties(Utils.mkProps(
													 NanoServiceFactory.NANOSERVICE_PORT, parser.getValue(ARG_HELP_PORT,String.class)
													,NanoServiceFactory.NANOSERVICE_ROOT, "fsys:xmlReadOnly:root://chav1961.elibrary.Application/chav1961/elibrary/helptree.xml"
													,NanoServiceFactory.NANOSERVICE_CREOLE_PROLOGUE_URI, Application.class.getResource("prolog.cre").toString() 
													,NanoServiceFactory.NANOSERVICE_CREOLE_EPILOGUE_URI, Application.class.getResource("epilog.cre").toString() 
												));
		
			try(final InputStream				is = Application.class.getResourceAsStream("application.xml")) {
				final ContentMetadataInterface	xda = ContentModelFactory.forXmlDescription(is);
				final CountDownLatch			latch = new CountDownLatch(1);
				final Localizer					localizer = LocalizerFactory.getLocalizer(xda.getRoot().getLocalizerAssociated());

				PureLibSettings.PURELIB_LOCALIZER.push(localizer);
				
				try(final Application			app = new Application(xda, localizer, parser.getValue(ARG_PROPFILE_LOCATION, File.class), latch);
					final NanoServiceFactory	service = new NanoServiceFactory(PureLibSettings.CURRENT_LOGGER, props)) {

					service.start();
					app.getLogger().message(Severity.info, app.getLocalizer().getValue(APP_NOTE_STARTED));
					app.showConsole();
					latch.await();
					service.stop();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (IOException | ContentException | EnvironmentException e) {
				e.printStackTrace();
			}
			//System.exit(0);
		} catch (CommandLineParametersException exc) {
			exc.printStackTrace();
			//System.exit(128);
		}
	}

	private static class ApplicationArgParser extends ArgParser {
		private static final ArgParser.AbstractArg[]	KEYS = {
			new IntegerArg(ARG_HELP_PORT, true, "Help port to use for help browser", 20000),
			new FileArg(ARG_PROPFILE_LOCATION, false, "Property file location", "./.elibrary.properties")
		};
		
		private ApplicationArgParser() {
			super(KEYS);
		}
	}

}
