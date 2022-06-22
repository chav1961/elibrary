package chav1961.elibrary.admin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import chav1961.elibrary.admin.db.BooksORMInterface;
import chav1961.elibrary.admin.db.ORMInterface;
import chav1961.elibrary.admin.db.SeriesORMInterface;
import chav1961.elibrary.admin.entities.AuthorsDescriptor;
import chav1961.elibrary.admin.entities.BookDescriptor;
import chav1961.elibrary.admin.entities.SeriesDescriptor;
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
	
			final BooksORMInterface		boi = (BooksORMInterface) orms.get(BookDescriptor.class);
			
			this.books = new JDataBaseTableWithMeta<Long, BookDescriptor>(meta.byApplicationPath(URI.create(URI_BOOKS))[0], localizer);
			
			this.books.assignResultSetAndManagers(boi.getResultSet(), boi.getFormManager(), boi.getInstanceManager());
			this.booksScroll = new JCloseableScrollPane(this.books);
			assignResizer(this.booksScroll, this.books);
			assignFocusManager(this.booksScroll, this.books);
			
			this.form = new AutoBuiltForm<BookDescriptor,Long>(ContentModelFactory.forAnnotatedClass(BookDescriptor.class), localizer, PureLibSettings.INTERNAL_LOADER, (BookDescriptor)boi.getFormManager(), boi.getFormManager());
			
			SwingUtils.assignActionKey(this.books, SwingUtils.KS_ACCEPT, (e)->{
				if (!books.getSelectionModel().isSelectionEmpty()) {
					try{
						edit(boi, this.books.getSelectedRow(), (BookDescriptor)boi.getFormManager(), this.form);
					} catch (ContentException exc) {
						SwingUtils.getNearestLogger(books).message(Severity.error, exc, exc.getLocalizedMessage());
					}
				}
			}, SwingUtils.ACTION_ACCEPT);
			SwingUtils.assignActionKey(this.form, JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, SwingUtils.KS_SOFT_EXIT, (e)->{
				save(boi, (BookDescriptor)boi.getFormManager(), this.books.getSelectedRow());
			}, SwingUtils.ACTION_SOFT_EXIT);
			
			this.books.getSelectionModel().addListSelectionListener((e)->{
				if (!books.getSelectionModel().isSelectionEmpty()) {
					try{load(boi, books.getSelectedRow(), (BookDescriptor)boi.getFormManager());
						toScreen((BookDescriptor)boi.getFormManager(), this.form);
						this.form.setEnabled(true);
					} catch (ContentException exc) {
						SwingUtils.getNearestLogger(books).message(Severity.error, exc, exc.getLocalizedMessage());
					}
				}
				else {
					this.form.setEnabled(false);
				}
			});
			
			setLeftComponent(this.booksScroll);
			setRightComponent(this.form);

			final Dimension dim = books.getPreferredScrollableViewportSize();
			dim.height += 20;
			books.setPreferredScrollableViewportSize(dim);
			books.setPreferredSize(dim);
			SwingUtilities.invokeLater(()->books.requestFocusInWindow());
		}
	}
	

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		// TODO Auto-generated method stub
		fillLocalizedStrings();
		SwingUtils.refreshLocale(books, oldLocale, newLocale);
		SwingUtils.refreshLocale(form, oldLocale, newLocale);
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

	private void load(final BooksORMInterface boi, final int selectedRow, final BookDescriptor desc) {
		final ResultSet	rs = boi.getResultSet();
		
		try{rs.absolute(selectedRow + 1);
			boi.getInstanceManager().loadInstance(rs, desc);
		} catch (SQLException e) {
			getLogger().message(Severity.error, e, e.getLocalizedMessage());
		}
	}

	private void toScreen(final BookDescriptor desc, final AutoBuiltForm<BookDescriptor,Long> form) throws ContentException {
		SwingUtils.putToScreen(form.getContentModel().getRoot(), desc, form);
		form.revalidate();
		form.repaint();
	}
	
	private void edit(final BooksORMInterface boi, final int selectedRow, final BookDescriptor desc, final AutoBuiltForm<BookDescriptor,Long> form) throws ContentException {
		load(boi, selectedRow, desc);
		toScreen(desc, form);
		form.requestFocusInWindow();
	}

	private void save(final BooksORMInterface boi, final BookDescriptor desc, final int selectedRow) {
		final ResultSet	rs = boi.getResultSet();
		
		try{rs.absolute(selectedRow + 1);
			boi.getInstanceManager().storeInstance(rs, desc, true);
			rs.updateRow();
			books.refresh();
			books.requestFocusInWindow();
			getLogger().message(Severity.info, "Saved");
		} catch (SQLException e) {
			getLogger().message(Severity.error, e, e.getLocalizedMessage());
		}
	}
	
	private void fillLocalizedStrings() {
		// TODO Auto-generated method stub
		
	}
}
