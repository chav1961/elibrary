package chav1961.elibrary.admin;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.ui.swing.SwingUtils;

public class JCloseableScrollPane extends JScrollPane implements AutoCloseable{
	private static final long serialVersionUID = -67567144871870852L;

	public JCloseableScrollPane() {
		super();
	}

	public JCloseableScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
		super(view, vsbPolicy, hsbPolicy);
	}

	public JCloseableScrollPane(Component view) {
		super(view);
	}

	public JCloseableScrollPane(int vsbPolicy, int hsbPolicy) {
		super(vsbPolicy, hsbPolicy);
	}

	@Override
	public void close() throws RuntimeException {
		final JViewport viewport = getViewport(); 
		final Component	component = viewport.getView();
		
		if (component instanceof AutoCloseable) {
			try{((AutoCloseable)component).close();
			} catch (Exception e) {
				SwingUtils.getNearestLogger(this).message(Severity.error, e, e.getLocalizedMessage());
			}
		}
	}
}
