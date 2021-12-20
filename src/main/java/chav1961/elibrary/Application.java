package chav1961.elibrary;

import chav1961.purelib.basic.ArgParser;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.swing.useful.JSystemTray;

public class Application {
	public static final String	ARG_HELP_PORT = "helpPort";
	public static final String	ARG_NAME = "application.name";

	private final Localizer		localizer;
	private final JSystemTray	tray;
	
	public Application(final Localizer localizer) {
		if (localizer != null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else {
			this.localizer = localizer;
			this.tray = null;// new JSystemTray(localizer, ARG_NAME, null, ARG_HELP_PORT, null);
		}
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private static class ApplicationArgParser extends ArgParser {
		private static final ArgParser.AbstractArg[]	KEYS = {
			new IntegerArg(ARG_HELP_PORT, true, "Help port to use for help browser", 0)
		};
		
		ApplicationArgParser() {
			super(KEYS);
		}
	}
}
