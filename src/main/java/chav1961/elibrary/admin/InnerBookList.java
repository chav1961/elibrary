package chav1961.elibrary.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import javax.naming.NamingException;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import chav1961.elibrary.admin.db.BooksORMInterface;
import chav1961.elibrary.admin.db.InnerBooksORMInterface;
import chav1961.elibrary.admin.db.ORMInterface;
import chav1961.elibrary.admin.entities.BookDescriptor;
import chav1961.elibrary.admin.entities.InnerBookDescriptor;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacadeOwner;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.enumerations.ContinueMode;
import chav1961.purelib.enumerations.NodeEnterMode;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.interfaces.OnAction;
import chav1961.purelib.ui.swing.useful.JDataBaseTableWithMeta;
import chav1961.purelib.ui.swing.useful.JDataBaseTableWithMeta.ContentChangedListener.ChangeType;

public class InnerBookList extends JSplitPane implements AutoCloseable, LoggerFacadeOwner, NodeMetadataOwner, LocaleChangeListener {
	private static final long serialVersionUID = -8075407533330137127L;
	private static final String				URI_BOOKS = "app:table:/elibrary.booklist";
	private static final URI				TOOLBAR_MENU_ROOT = URI.create("ui:/model/navigation.top.booklistmenu");	
	private static final URI				BOTTOM_TOOLBAR_MENU_ROOT = URI.create("ui:/model/navigation.top.bottomtoolbar");	

	private final long									parentId;
	private final Localizer								localizer;
	private final LoggerFacade							logger;
	private final ContentMetadataInterface				meta;
	private final Map<Class<?>,ORMInterface<?,?>>		orms;
	private final JToolBar								toolbar, bottomToolbar;
	private final JPopupMenu							popupMenu;
	private final JDataBaseTableWithMeta<Long, InnerBookDescriptor>		books;
	private final JCloseableScrollPane					booksScroll;
	private final InnerBookDescriptor					editDescriptor;
	private final AutoBuiltForm<InnerBookDescriptor,Long>	form;
	private final InnerBooksORMInterface				boi;
	private final ResultSet								listRs;
	
	public InnerBookList(final long parentId, final Localizer localizer, final LoggerFacade logger, final ContentMetadataInterface meta, final ContentMetadataInterface metaParent, final Map<Class<?>,ORMInterface<?,?>> orms) throws NullPointerException, IllegalArgumentException, SQLException, SyntaxException, LocalizationException, ContentException, NamingException {
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
			this.parentId = parentId;
			this.localizer = localizer;
			this.logger = logger;
			this.meta = meta;
			this.orms = orms;
			
			this.toolbar = SwingUtils.toJComponent(metaParent.byUIPath(TOOLBAR_MENU_ROOT), JToolBar.class);
			this.bottomToolbar = SwingUtils.toJComponent(metaParent.byUIPath(BOTTOM_TOOLBAR_MENU_ROOT), JToolBar.class);
			this.popupMenu = SwingUtils.toJComponent(metaParent.byUIPath(TOOLBAR_MENU_ROOT), JPopupMenu.class);

			this.toolbar.setFloatable(false);
			this.bottomToolbar.setFloatable(false);
			SwingUtils.assignActionListeners(this.toolbar, this);
			SwingUtils.assignActionListeners(this.bottomToolbar, this);
			SwingUtils.assignActionListeners(this.popupMenu, this);
	
			this.boi = (InnerBooksORMInterface) orms.get(InnerBookDescriptor.class);
			this.listRs = boi.getListResultSet(parentId);
			boi.getInstanceManager().currentParent = parentId;
			
			this.books = new JDataBaseTableWithMeta<Long, InnerBookDescriptor>(meta.byApplicationPath(URI.create(URI_BOOKS))[0], localizer, true, false);
			this.books.assignResultSetAndManagers(listRs, boi.getFormManager(), boi.getInstanceManager());
			this.booksScroll = new JCloseableScrollPane(this.books);
			assignResizer(this.booksScroll, this.books);
			assignFocusManager(this.booksScroll, this.books);
			this.books.addContentChangedListener((table, ct, key, field)->{
				contentChanged(ct,key);
			});
			
			this.editDescriptor = new InnerBookDescriptor(logger, meta.byApplicationPath(URI.create(URI_BOOKS))[0]);
			this.editDescriptor.parentRef = parentId;
			this.form = new AutoBuiltForm<InnerBookDescriptor,Long>(ContentModelFactory.forAnnotatedClass(InnerBookDescriptor.class), localizer, getLogger(), PureLibSettings.INTERNAL_LOADER, (InnerBookDescriptor)this.editDescriptor, this.editDescriptor);
			enableForm(false);
			
			SwingUtils.assignActionKey(this.books, SwingUtils.KS_ACCEPT, (e)->{
				if (!books.getSelectionModel().isSelectionEmpty()) {
					try{
						edit(boi, this.books.getSelectedKey(), this.editDescriptor, this.form);
					} catch (SQLException exc) {
						logger.message(Severity.error, exc, exc.getLocalizedMessage());
					}
				}
			}, SwingUtils.ACTION_ACCEPT);
			SwingUtils.assignActionKey(this.books, SwingUtils.KS_CONTEXTMENU, (e)->{
				final Rectangle	rect = !books.getSelectionModel().isSelectionEmpty() ? books.getCellRect(books.getSelectedRow(), 0, false) : books.getBounds(); 

				enableMenuItems(false);
				popupMenu.show(books, (int)rect.getCenterX(), (int)rect.getCenterY());
			}, SwingUtils.ACTION_CONTEXTMENU);
			
			SwingUtils.assignActionKey(this.form, JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, SwingUtils.KS_SOFT_ACCEPT, (e)->{
				try{
					save(boi, this.editDescriptor, this.books.getSelectedKey());
				} catch (SQLException exc) {
					logger.message(Severity.error, exc, exc.getLocalizedMessage());
				}
			}, SwingUtils.ACTION_SOFT_ACCEPT);
			SwingUtils.assignActionKey(this.form, JPanel.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, SwingUtils.KS_SOFT_EXIT, (e)->cancel(), SwingUtils.ACTION_SOFT_EXIT);
			
			this.books.getSelectionModel().addListSelectionListener((e)->{
				try{if (!books.getSelectionModel().isSelectionEmpty()) {
						fill(boi, this.books.getSelectedKey(), (InnerBookDescriptor)boi.getFormManager(), this.form);
					}
					enableMenuItems(false);
				} catch (SQLException exc) {
					logger.message(Severity.error, exc, exc.getLocalizedMessage());
				}
			});
			
			final JPanel	leftPanel = new JPanel(new BorderLayout());
			final JPanel	rightPanel = new JPanel(new BorderLayout());
			
			leftPanel.add(this.toolbar, BorderLayout.NORTH);
			leftPanel.add(this.booksScroll, BorderLayout.CENTER);
			
			rightPanel.add(this.form, BorderLayout.CENTER);
			rightPanel.add(this.bottomToolbar, BorderLayout.SOUTH);
			
			setLeftComponent(leftPanel);
			setRightComponent(rightPanel);
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
		this.listRs.close();
	}

	@OnAction("action:/booklist.copy")
	private void copy() {
		if (books.getSelectedRow() >= 0) {
			
		}
	}
	
	@OnAction("action:/booklist.paste")
	private void paste() {
		
	}
	
	@OnAction("action:/booklist.insert")
	private void insert() {
		books.processAction(SwingUtils.ACTION_INSERT);
	}
	
	@OnAction("action:/booklist.duplicate")
	private void duplicate() {
		if (books.getSelectedRow() >= 0) {
			books.processAction(SwingUtils.ACTION_DUPLICATE);
		}
	}
	
	@OnAction("action:/booklist.edit")
	private void edit() throws SQLException {
		if (books.getSelectedRow() >= 0) {
			edit(boi, this.books.getSelectedKey(), (InnerBookDescriptor)boi.getFormManager(), this.form);
		}
	}
	
	@OnAction("action:/booklist.delete")
	private void delete() {
		if (books.getSelectedRow() >= 0) {
			books.processAction(SwingUtils.ACTION_DELETE);
		}
	}

	@OnAction("action:/bottomtoolbar.save")
	private void save() throws SQLException {
		save(boi, editDescriptor, books.getSelectedKey());	
	}

	@OnAction("action:/bottomtoolbar.cancel")
	private void cancel() {
		enableForm(false);
	}
	
	@OnAction("action:/bottomtoolbar.help")
	private void help() {
		
	}

	private void contentChanged(final ChangeType ct, final Long key) {
		switch (ct) {
			case DELETED	:
				break;
			case DUPLICATED	: case INSERTED :
				try{final int	row = locateResultSet(listRs, key);
				
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

	private void load(final InnerBooksORMInterface boi, final Long key, final InnerBookDescriptor desc) {
		try(final ResultSet	rs = boi.getRecordResultSet(key)) {
			
			if (rs.next()) {
				boi.getInstanceManager().loadInstance(rs, desc);
			}
			else {
				getLogger().message(Severity.error, "Key ["+key+"] not found");
			}
		} catch (SQLException e) {
			getLogger().message(Severity.error, e, e.getLocalizedMessage());
		}
	}

	private void toScreen(final InnerBookDescriptor desc, final AutoBuiltForm<InnerBookDescriptor,Long> form) throws ContentException {
		SwingUtils.putToScreen(form.getContentModel().getRoot(), desc, form);
		form.revalidate();
		form.repaint();
	}

	private void fill(final InnerBooksORMInterface boi, final Long key, final InnerBookDescriptor desc, final AutoBuiltForm<InnerBookDescriptor,Long> form) {
		try{load(boi, key, desc);
			toScreen(desc, form);
		} catch (ContentException e) {
			getLogger().message(Severity.error, e, e.getLocalizedMessage());
		}
	}
	
	private void edit(final InnerBooksORMInterface boi, final Long key, final InnerBookDescriptor desc, final AutoBuiltForm<InnerBookDescriptor,Long> form) {
		try{fill(boi, key, desc, form);
		} finally {
			enableForm(true);
		}
	}

	private void enableForm(final boolean enabled) {
		SwingUtils.walkDown(bottomToolbar, (mode, node)->{
			if ((mode == NodeEnterMode.ENTER) && (node instanceof AbstractButton)) {
				((AbstractButton)node).setEnabled(enabled);
			}
			return ContinueMode.CONTINUE;
		});
		books.setEnabled(!enabled);
		form.setEnabled(enabled);
		if (enabled) {
			form.requestFocusInWindow();
		}
		else {
			books.requestFocusInWindow();
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
	
	
	private void save(final InnerBooksORMInterface boi, final InnerBookDescriptor desc, final Long key) {
		try(final ResultSet	rs = boi.getRecordResultSet(key)) {
			
			rs.absolute(1);
			boi.getInstanceManager().storeInstance(rs, desc, true);
			rs.updateRow();
			books.refresh();
			getLogger().message(Severity.info, "Saved");
		} catch (SQLException e) {
			getLogger().message(Severity.error, e, e.getLocalizedMessage());
		} finally {
			cancel();
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
