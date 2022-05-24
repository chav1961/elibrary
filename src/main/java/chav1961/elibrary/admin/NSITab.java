package chav1961.elibrary.admin;

import java.awt.Color;
import java.awt.GridLayout;
import java.net.URI;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import chav1961.elibrary.admin.db.AuthorsORMInterface;
import chav1961.elibrary.admin.db.ORMInterface;
import chav1961.elibrary.admin.db.PublishersORMInterface;
import chav1961.elibrary.admin.db.SeriesORMInterface;
import chav1961.elibrary.admin.dialogs.AuthorsDescriptor;
import chav1961.elibrary.admin.dialogs.PublishersDescriptor;
import chav1961.elibrary.admin.dialogs.SeriesDescriptor;
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
			
			this.series = new JDataBaseTableWithMeta<Long, SeriesDescriptor>(meta.byApplicationPath(URI.create(URI_SERIES))[0], localizer);
			this.series.assignResultSetAndManagers(soi.getResultSet(), soi.getFormManager(), soi.getInstanceManager());
			this.seriesScroll = new JCloseableScrollPane(this.series);

			final AuthorsORMInterface		aoi = (AuthorsORMInterface) orms.get(AuthorsDescriptor.class);
			
			this.authors = new JDataBaseTableWithMeta<Long, AuthorsDescriptor>(meta.byApplicationPath(URI.create(URI_AUTHORS))[0], localizer);
			this.authors.assignResultSetAndManagers(aoi.getResultSet(), aoi.getFormManager(), aoi.getInstanceManager());
			this.authorsScroll = new JCloseableScrollPane(this.authors);

			final PublishersORMInterface	poi = (PublishersORMInterface) orms.get(PublishersDescriptor.class);
			
			this.publishers = new JDataBaseTableWithMeta<Long, PublishersDescriptor>(meta.byApplicationPath(URI.create(URI_PUBLISHERS))[0], localizer);
			this.publishers.assignResultSetAndManagers(poi.getResultSet(), poi.getFormManager(), poi.getInstanceManager());
			this.publishersScroll = new JCloseableScrollPane(this.publishers);
			
			fillLocalizedStrings();

			setLayout(new GridLayout(3,1,10,10));			
			add(this.seriesScroll);
			add(this.authorsScroll);
			add(this.publishersScroll);
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

	private void fillLocalizedStrings() {
		seriesScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK), localizer.getValue(meta.byApplicationPath(URI.create(URI_SERIES))[0].getLabelId())));
		authorsScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK), localizer.getValue(meta.byApplicationPath(URI.create(URI_AUTHORS))[0].getLabelId())));
		publishersScroll.setBorder(new TitledBorder(new LineBorder(Color.BLACK), localizer.getValue(meta.byApplicationPath(URI.create(URI_PUBLISHERS))[0].getLabelId())));
	}
}
