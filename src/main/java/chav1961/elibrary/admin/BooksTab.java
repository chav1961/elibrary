package chav1961.elibrary.admin;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.URI;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import chav1961.elibrary.admin.db.BooksORMInterface;
import chav1961.elibrary.admin.db.ORMInterface;
import chav1961.elibrary.admin.db.SeriesORMInterface;
import chav1961.elibrary.admin.dialogs.AuthorsDescriptor;
import chav1961.elibrary.admin.dialogs.BookDescriptor;
import chav1961.elibrary.admin.dialogs.SeriesDescriptor;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.interfaces.LoggerFacadeOwner;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.interfaces.FormManager;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.JDataBaseTableWithMeta;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;

public class BooksTab extends JSplitPane implements AutoCloseable, LoggerFacadeOwner, NodeMetadataOwner, LocaleChangeListener {
	private static final long serialVersionUID = -8075407533330137127L;
	private static final String				URI_BOOKS = "app:table:/elibrary.booklist";

	private final Localizer								localizer;
	private final LoggerFacade							logger;
	private final ContentMetadataInterface				meta;
	private final Map<Class<?>,ORMInterface<?,?>>		orms;
	private final JDataBaseTableWithMeta<Long, BookDescriptor>		books;
	private final JCloseableScrollPane					booksScroll;
	private final AutoBuiltForm<BookDescriptor,Long>	form;
	
	public BooksTab(final Localizer localizer, final LoggerFacade logger, final ContentMetadataInterface meta, final Map<Class<?>,ORMInterface<?,?>> orms) throws NullPointerException, IllegalArgumentException, SQLException, SyntaxException, LocalizationException, ContentException {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (logger == null) {
			throw new NullPointerException("Logger facade can't be null");
		}
		else if (meta == null) {
			throw new NullPointerException("Content node metadata can't be null");
		}
		else if (orms == null) {
			throw new NullPointerException("ORM inteface map can't be null");
		}
		else {
			this.localizer = localizer;
			this.logger = logger;
			this.meta = meta;
			this.orms = orms;
	
			final BooksORMInterface		boi = (BooksORMInterface) orms.get(BooksORMInterface.class);
			
			this.books = new JDataBaseTableWithMeta<Long, BookDescriptor>(meta.byApplicationPath(URI.create(URI_BOOKS))[0], localizer);
			this.books.assignResultSetAndManagers(boi.getResultSet(), boi.getFormManager(), boi.getInstanceManager());
			this.booksScroll = new JCloseableScrollPane(this.books);
			assignResizer(this.booksScroll, this.books);
			assignFocusManager(this.booksScroll, this.books);
			
			this.form = new AutoBuiltForm<BookDescriptor,Long>(ContentModelFactory.forAnnotatedClass(BookDescriptor.class), localizer, PureLibSettings.INTERNAL_LOADER, (BookDescriptor)boi.getFormManager(), boi.getFormManager());
			this.books.getSelectionModel().addListSelectionListener((e)->{
				try {
					SwingUtils.putToScreen(this.form.getContentModel().getRoot(), (BookDescriptor)boi.getFormManager(), this.form);
				} catch (ContentException exc) {
					SwingUtils.getNearestLogger(books).message(Severity.error, exc, exc.getLocalizedMessage());
				}
			});
			
			setLeftComponent(this.booksScroll);
			setRightComponent(this.form);
		}
	}
	
	@Override
	public void localeChanged(Locale oldLocale, Locale newLocale) throws LocalizationException {
		// TODO Auto-generated method stub
		fillLocalizedStrings();
	}

	@Override
	public ContentNodeMetadata getNodeMetadata() {
		return meta.getRoot();
	}

	@Override
	public LoggerFacade getLogger() {
		return logger;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

	private void assignResizer(final JScrollPane owner, final JDataBaseTableWithMeta<?, ?> table) {
		owner.addComponentListener(new ComponentListener() {
			@Override public void componentMoved(ComponentEvent e) {}
			@Override public void componentHidden(ComponentEvent e) {}
			
			@Override
			public void componentShown(ComponentEvent e) {
				table.resizeColumns();
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				table.resizeColumns();
			}
		});
	}
	
	private void assignFocusManager(final JScrollPane pane, final JDataBaseTableWithMeta<?, ?> table) {
		table.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				fillLocalizedStrings();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				fillLocalizedStrings();
			}
		});
	}
	
	private void fillLocalizedStrings() {
		// TODO Auto-generated method stub
		
	}
}
