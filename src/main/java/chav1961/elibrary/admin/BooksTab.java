package chav1961.elibrary.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import chav1961.elibrary.admin.db.BooksORMInterface;
import chav1961.elibrary.admin.db.ORMInterface;
import chav1961.elibrary.admin.entities.BookDescriptor;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.basic.interfaces.LoggerFacadeOwner;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.testing.SwingTestingUtils;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.JDataBaseTableWithMeta;
import chav1961.purelib.ui.swing.useful.JDataBaseTableWithMeta.ContentChangedListener.ChangeType;

public class BooksTab extends JSplitPane implements AutoCloseable, LoggerFacadeOwner, NodeMetadataOwner, LocaleChangeListener {
	private static final long serialVersionUID = -8075407533330137127L;
	private static final String				URI_BOOKS = "app:table:/elibrary.booklist";
	private static final URI				TOOLBAR_MENU_ROOT = URI.create("ui:/model/navigation.top.booklistmenu");	

	private final Localizer								localizer;
	private final LoggerFacade							logger;
	private final ContentMetadataInterface				meta;
	private final Map<Class<?>,ORMInterface<?,?>>		orms;
	private final JToolBar								toolbar;
	private final JPopupMenu							popupMenu;
	private final JDataBaseTableWithMeta<Long, BookDescriptor>		books;
	private final JCloseableScrollPane					booksScroll;
	private final AutoBuiltForm<BookDescriptor,Long>	form;
	private final BooksORMInterface						boi; 
	
	public BooksTab(final Localizer localizer, final LoggerFacade logger, final ContentMetadataInterface meta, final ContentMetadataInterface metaParent, final Map<Class<?>,ORMInterface<?,?>> orms) throws NullPointerException, IllegalArgumentException, SQLException, SyntaxException, LocalizationException, ContentException {
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
			
			this.toolbar = SwingUtils.toJComponent(metaParent.byUIPath(TOOLBAR_MENU_ROOT), JToolBar.class);
			this.popupMenu = SwingUtils.toJComponent(metaParent.byUIPath(TOOLBAR_MENU_ROOT), JPopupMenu.class);
			this.toolbar.setFloatable(false);
			SwingUtils.assignActionListeners(this.toolbar,this);
			SwingUtils.assignActionListeners(this.popupMenu,this);
	
			this.boi = (BooksORMInterface) orms.get(BookDescriptor.class);
			
			this.books = new JDataBaseTableWithMeta<Long, BookDescriptor>(meta.byApplicationPath(URI.create(URI_BOOKS))[0], localizer, true, false);
			this.books.assignResultSetAndManagers(boi.getResultSet(), boi.getFormManager(), boi.getInstanceManager());
			this.booksScroll = new JCloseableScrollPane(this.books);
			assignResizer(this.booksScroll, this.books);
			assignFocusManager(this.booksScroll, this.books);
			this.books.addContentChangedListener((table, ct, key, field)->{
				contentChanged(ct,key);
			});
			
			this.form = new AutoBuiltForm<BookDescriptor,Long>(ContentModelFactory.forAnnotatedClass(BookDescriptor.class), localizer, PureLibSettings.INTERNAL_LOADER, (BookDescriptor)boi.getFormManager(), boi.getFormManager());
			this.form.setEnabled(false);
			
			SwingUtils.assignActionKey(this.books, SwingUtils.KS_ACCEPT, (e)->{
				if (!books.getSelectionModel().isSelectionEmpty()) {
					edit(boi, this.books.getSelectedRow(), (BookDescriptor)boi.getFormManager(), this.form);
				}
			}, SwingUtils.ACTION_ACCEPT);
			SwingUtils.assignActionKey(this.books, SwingUtils.KS_CONTEXTMENU, (e)->{
				final Rectangle	rect = !books.getSelectionModel().isSelectionEmpty() ? books.getCellRect(books.getSelectedRow(), 0, false) : books.getBounds(); 

				enableMenuItems(false);
				popupMenu.show(books, (int)rect.getCenterX(), (int)rect.getCenterY());
			}, SwingUtils.ACTION_ACCEPT);
			SwingUtils.assignActionKey(this.form, JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, SwingUtils.KS_SOFT_EXIT, (e)->{
				save(boi, (BookDescriptor)boi.getFormManager(), this.books.getSelectedRow());
			}, SwingUtils.ACTION_SOFT_EXIT);
			
			this.books.getSelectionModel().addListSelectionListener((e)->{
				if (!books.getSelectionModel().isSelectionEmpty()) {
					fill(boi, this.books.getSelectedRow(), (BookDescriptor)boi.getFormManager(), this.form);
				}
				enableMenuItems(false);
			});
			
			final JPanel	panel = new JPanel(new BorderLayout());
			
			panel.add(this.toolbar, BorderLayout.NORTH);
			panel.add(this.booksScroll, BorderLayout.CENTER);
			
			setLeftComponent(panel);
			setRightComponent(this.form);
			enableMenuItems(false);
			Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener((e)->enableMenuItems(true));
			final Dimension dim = books.getPreferredScrollableViewportSize();
			dim.height += 20;
			books.setPreferredScrollableViewportSize(dim);
			books.setPreferredSize(dim);
			SwingUtilities.invokeLater(()->books.requestFocusInWindow());
		}
	}
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
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
	}

	@OnAction("menu.booklist.copy")
	private void copy() {
		if (books.getSelectedRow() >= 0) {
			
		}
	}
	
	@OnAction("booklist.paste")
	private void paste() {
		
	}
	
	@OnAction("booklist.insert")
	private void insert() {
		books.processAction(SwingUtils.ACTION_INSERT);
	}
	
	@OnAction("booklist.duplicate")
	private void duplicate() {
		if (books.getSelectedRow() >= 0) {
			books.processAction(SwingUtils.ACTION_DUPLICATE);
		}
	}
	
	@OnAction("booklist.edit")
	private void edit() {
		if (books.getSelectedRow() >= 0) {
			edit(boi, this.books.getSelectedRow(), (BookDescriptor)boi.getFormManager(), this.form);
		}
	}
	
	@OnAction("booklist.delete")
	private void delete() {
		if (books.getSelectedRow() >= 0) {
			books.processAction(SwingUtils.ACTION_DELETE);
		}
	}

	private void contentChanged(final ChangeType ct, final Long key) {
		switch (ct) {
			case DELETED	:
				break;
			case DUPLICATED	: case INSERTED :
				try{final int	row = locateResultSet(boi.getResultSet(), key);
				
					books.changeSelection(row, 0, false, false);
					edit();
				} catch (SQLException e) {
					getLogger().message(Severity.error, e, e.getLocalizedMessage());
				}
				break;
			default:
				throw new UnsupportedOperationException("Change type ["+ct+"] is not supported yet");
		}
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

	private void fill(final BooksORMInterface boi, final int selectedRow, final BookDescriptor desc, final AutoBuiltForm<BookDescriptor,Long> form) {
		try{load(boi, books.getSelectedRow(), (BookDescriptor)boi.getFormManager());
			toScreen((BookDescriptor)boi.getFormManager(), form);
		} catch (ContentException e) {
			getLogger().message(Severity.error, e, e.getLocalizedMessage());
		}
	}
	
	private void edit(final BooksORMInterface boi, final int selectedRow, final BookDescriptor desc, final AutoBuiltForm<BookDescriptor,Long> form) {
		try{fill(boi, selectedRow, desc, form);
		} finally {
			books.setEnabled(false);
			form.setEnabled(true);
			form.requestFocusInWindow();
		}
	}

	private int locateResultSet(final ResultSet rs, final long key) throws SQLException {
		rs.last();
		
        int low = 0, high = rs.getRow() - 1;

        while (low <= high) {
            int 	mid = (low + high) >>> 1;
            
            rs.absolute(mid + 1);
            
            long 	midVal = rs.getLong("bl_Id");

            if (midVal < key) {
                low = mid + 1;
            }
            else if (midVal > key) {
                high = mid - 1;
            }
            else {
            	return mid;
            }
        }
        rs.beforeFirst();
        while (rs.next()) {
        	System.err.println("Key="+rs.getLong("bl_Id"));
        }
        throw new IllegalArgumentException("Key ["+key+"] not found in the resultset"); 
	}
	
	
	private void save(final BooksORMInterface boi, final BookDescriptor desc, final int selectedRow) {
		final ResultSet	rs = boi.getResultSet();
		
		try{final int		lastPos = rs.getRow();
			
			rs.absolute(selectedRow + 1);
			if (rs.getLong("bl_Id") != desc.id) {	// unsynchronized result set, use binary search to find id required
				locateResultSet(rs, desc.id);
			}
			boi.getInstanceManager().storeInstance(rs, desc, true);
			rs.updateRow();
			rs.absolute(lastPos);
			books.refresh();
			getLogger().message(Severity.info, "Saved");
		} catch (SQLException e) {
			getLogger().message(Severity.error, e, e.getLocalizedMessage());
		} finally {
			form.setEnabled(false);
			books.setEnabled(true);
			books.requestFocusInWindow();
		}
	}

	private void enableMenuItems(final boolean refreshPaste) {
		if (books.getSelectionModel().isSelectionEmpty()) {
			((JMenuItem)SwingUtils.findComponentByName(popupMenu, "menu.booklist.copy")).setEnabled(false);
			((JMenuItem)SwingUtils.findComponentByName(popupMenu, "menu.booklist.duplicate")).setEnabled(false);
			((JMenuItem)SwingUtils.findComponentByName(popupMenu, "menu.booklist.edit")).setEnabled(false);
			((JMenuItem)SwingUtils.findComponentByName(popupMenu, "menu.booklist.delete")).setEnabled(false);
			((JButton)SwingUtils.findComponentByName(toolbar, "menu.booklist.copy")).setEnabled(false);
			((JButton)SwingUtils.findComponentByName(toolbar, "menu.booklist.duplicate")).setEnabled(false);
			((JButton)SwingUtils.findComponentByName(toolbar, "menu.booklist.edit")).setEnabled(false);
			((JButton)SwingUtils.findComponentByName(toolbar, "menu.booklist.delete")).setEnabled(false);
		}
		else {
			((JMenuItem)SwingUtils.findComponentByName(popupMenu, "menu.booklist.copy")).setEnabled(true);
			((JMenuItem)SwingUtils.findComponentByName(popupMenu, "menu.booklist.duplicate")).setEnabled(true);
			((JMenuItem)SwingUtils.findComponentByName(popupMenu, "menu.booklist.edit")).setEnabled(true);
			((JMenuItem)SwingUtils.findComponentByName(popupMenu, "menu.booklist.delete")).setEnabled(true);
			((JButton)SwingUtils.findComponentByName(toolbar, "menu.booklist.copy")).setEnabled(true);
			((JButton)SwingUtils.findComponentByName(toolbar, "menu.booklist.duplicate")).setEnabled(true);
			((JButton)SwingUtils.findComponentByName(toolbar, "menu.booklist.edit")).setEnabled(true);
			((JButton)SwingUtils.findComponentByName(toolbar, "menu.booklist.delete")).setEnabled(true);
		}
		if (refreshPaste) {
			if (Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable(DataFlavor.stringFlavor)) {
				((JMenuItem)SwingUtils.findComponentByName(popupMenu, "menu.booklist.paste")).setEnabled(true);
			}
			else {
				((JMenuItem)SwingUtils.findComponentByName(popupMenu, "menu.booklist.paste")).setEnabled(false);
			}
		}
	}
	
	private void fillLocalizedStrings() {
		// TODO Auto-generated method stub
		
	}
}
