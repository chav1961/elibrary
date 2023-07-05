package chav1961.elibrary.admin;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.URI;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import chav1961.elibrary.admin.db.AuthorsORMInterface;
import chav1961.elibrary.admin.db.ORMInterface;
import chav1961.elibrary.admin.db.PublishersORMInterface;
import chav1961.elibrary.admin.db.SeriesORMInterface;
import chav1961.elibrary.admin.entities.AuthorsDescriptor;
import chav1961.elibrary.admin.entities.PublishersDescriptor;
import chav1961.elibrary.admin.entities.SeriesDescriptor;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacadeOwner;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;
import chav1961.purelib.ui.swing.useful.JDataBaseTableWithMeta;

public class NSITab extends JPanel implements AutoCloseable, LoggerFacadeOwner, NodeMetadataOwner, LocaleChangeListener {
	private static final long 				serialVersionUID = 1L;
	private static final String				URI_SERIES = "app:table:/elibrary.bookseries";
	private static final String				URI_AUTHORS = "app:table:/elibrary.bookauthors";
	private static final String				URI_PUBLISHERS = "app:table:/elibrary.bookpublishers";

	private final Localizer							localizer;
	private final LoggerFacade						logger;
	private final ContentMetadataInterface			meta;
	private final Map<Class<?>,ORMInterface<?,?>>	orms;
	
	private final JDataBaseTableWithMeta<Long, SeriesDescriptor>		series;
	private final JCloseableScrollPane									seriesScroll;
	private final JDataBaseTableWithMeta<Long, AuthorsDescriptor>		authors;
	private final JCloseableScrollPane									authorsScroll;
	private final JDataBaseTableWithMeta<Long, PublishersDescriptor>	publishers;
	private final JCloseableScrollPane									publishersScroll;
	
	public NSITab(final Localizer localizer, final LoggerFacade logger, final ContentMetadataInterface meta, final Map<Class<?>,ORMInterface<?,?>> orms) throws NullPointerException, IllegalArgumentException, SQLException {
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
			
			final SeriesORMInterface		soi = (SeriesORMInterface) orms.get(SeriesDescriptor.class);
			
			this.series = new JDataBaseTableWithMeta<Long, SeriesDescriptor>(meta.byApplicationPath(URI.create(URI_SERIES))[0], localizer, true, true);
			this.series.assignResultSetAndManagers(soi.getListResultSet(), soi.getFormManager(), soi.getInstanceManager());
			this.series.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			this.seriesScroll = new JCloseableScrollPane(this.series);
			assignResizer(this.seriesScroll, this.series);
			assignFocusManager(this.seriesScroll, this.series);

			final AuthorsORMInterface		aoi = (AuthorsORMInterface) orms.get(AuthorsDescriptor.class);
			
			this.authors = new JDataBaseTableWithMeta<Long, AuthorsDescriptor>(meta.byApplicationPath(URI.create(URI_AUTHORS))[0], localizer, true, true);
			this.authors.assignResultSetAndManagers(aoi.getListResultSet(), aoi.getFormManager(), aoi.getInstanceManager());
			this.authors.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			this.authorsScroll = new JCloseableScrollPane(this.authors);
			assignResizer(this.authorsScroll, this.authors);
			assignFocusManager(this.authorsScroll, this.authors);

			final PublishersORMInterface	poi = (PublishersORMInterface) orms.get(PublishersDescriptor.class);
			
			this.publishers = new JDataBaseTableWithMeta<Long, PublishersDescriptor>(meta.byApplicationPath(URI.create(URI_PUBLISHERS))[0], localizer, true, true);
			this.publishers.assignResultSetAndManagers(poi.getListResultSet(), poi.getFormManager(), poi.getInstanceManager());
			this.publishers.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			this.publishersScroll = new JCloseableScrollPane(this.publishers);
			assignResizer(this.publishersScroll, this.publishers);
			assignFocusManager(this.publishersScroll, this.publishers);
			
			fillLocalizedStrings();

			setLayout(new GridLayout(3,3,10,10));			
			add(this.seriesScroll);
			add(this.authorsScroll);
			add(this.publishersScroll);
			add(new JLabel(" "));
			add(new JLabel(" "));
			add(new JLabel(" "));
			add(new JLabel(" "));
			add(new JLabel(" "));
			add(new JLabel(" "));
			SwingUtilities.invokeLater(()->this.series.requestFocusInWindow());
		}
	}

	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
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
	public void close() throws RuntimeException {
		seriesScroll.close();
		authorsScroll.close();
		publishersScroll.close();
	}

	private void assignResizer(final JScrollPane owner, final JDataBaseTableWithMeta<?, ?> table) {
		
		owner.addComponentListener(new ComponentListener() {
			@Override public void componentMoved(ComponentEvent e) {}
			@Override public void componentHidden(ComponentEvent e) {}
			
			@Override
			public void componentShown(ComponentEvent e) {
				table.resizeColumns(owner.getViewport().getWidth());
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				table.resizeColumns(owner.getViewport().getWidth());
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
		seriesScroll.setBorder(new TitledBorder(getBorder(series), localizer.getValue(meta.byApplicationPath(URI.create(URI_SERIES))[0].getLabelId())));
		authorsScroll.setBorder(new TitledBorder(getBorder(authors), localizer.getValue(meta.byApplicationPath(URI.create(URI_AUTHORS))[0].getLabelId())));
		publishersScroll.setBorder(new TitledBorder(getBorder(publishers), localizer.getValue(meta.byApplicationPath(URI.create(URI_PUBLISHERS))[0].getLabelId())));
	}
	
	private LineBorder getBorder(final JDataBaseTableWithMeta<?, ?> table) {
		if (table.hasFocus()) {
			return new LineBorder(Color.BLUE, 3);
		}
		else {
			return new LineBorder(Color.BLACK);
		}
	}
}
