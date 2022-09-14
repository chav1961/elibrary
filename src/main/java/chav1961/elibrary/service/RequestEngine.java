package chav1961.elibrary.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

import chav1961.elibrary.admin.db.BooksDescriptorMgr;
import chav1961.elibrary.admin.entities.BookDescriptor;
import chav1961.elibrary.admin.entities.Settings;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.SimpleURLClassLoader;
import chav1961.purelib.basic.SubstitutableProperties;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacadeOwner;
import chav1961.purelib.basic.interfaces.ModuleAccessor;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.LocalizerOwner;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.model.interfaces.ContentMetadataInterface.ContentNodeMetadata;
import chav1961.purelib.model.interfaces.NodeMetadataOwner;
import chav1961.purelib.nanoservice.interfaces.FromQuery;
import chav1961.purelib.nanoservice.interfaces.Path;
import chav1961.purelib.nanoservice.interfaces.RootPath;
import chav1961.purelib.nanoservice.interfaces.ToBody;
import chav1961.purelib.nanoservice.interfaces.ToHeader;
import chav1961.purelib.sql.JDBCUtils;

@RootPath("/content")
public class RequestEngine implements ModuleAccessor, AutoCloseable, LoggerFacadeOwner, LocalizerOwner, NodeMetadataOwner {
	private final SubstitutableProperties	props = new SubstitutableProperties();
	private final ContentMetadataInterface	mdi;
	private final Localizer					localizer;
	private final SimpleURLClassLoader		loader;
	private final Driver					driver;
	private final Connection				conn;
	private final BookDescriptor			desc;
	private final BooksDescriptorMgr		mgr;
	
	public RequestEngine(final  Localizer localizer, final File properties) throws IOException, ContentException, SQLException {
		try(final InputStream	is = new FileInputStream(properties)) {
			props.load(is);
		}
		try{
			try(final InputStream	is = URI.create("root://"+this.getClass().getCanonicalName()+"/chav1961/elibrary/admin/db/model.json").toURL().openStream();
				final Reader		rdr = new InputStreamReader(is, PureLibSettings.DEFAULT_CONTENT_ENCODING)) {
				
				this.mdi = ContentModelFactory.forJsonDescription(rdr);
			}
			this.localizer = localizer;
			this.loader = new SimpleURLClassLoader(new URL[0]);
			this.driver = JDBCUtils.loadJdbcDriver(loader, props.getProperty(Settings.PROP_DRIVER, File.class));
			this.conn = JDBCUtils.getConnection(driver, 
								props.getProperty(Settings.PROP_CONN_STRING, URI.class), 
								props.getProperty(Settings.PROP_SEARCH_USER, String.class), 
								props.getProperty(Settings.PROP_SEARCH_PASSWORD, char[].class));
			this.desc = new BookDescriptor(getLogger(), mdi.getRoot().getChild("booklist"));
			this.mgr = new BooksDescriptorMgr(getLogger(), desc, ()->0, conn);
		} catch (NamingException exc) {
			throw new ContentException(exc); 
		}
	}

	@Path("/show")
	public int show(@ToBody(mimeType="text/html") final Writer wr) throws IOException {
		try(final PreparedStatement	stmt = conn.prepareStatement("select * from \"elibrary\".\"booklist\" order by \"bl_Year\", \"bl_Title\"", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			final ResultSet			rs = stmt.executeQuery()) {
			
			boolean		theSameFirst = true;

			printStartPage(wr);
			while (rs.next()) {
				mgr.loadInstance(rs, desc);
				if (!theSameFirst) {
					wr.write("<hr/>\n");
				}
				wr.write(ResponseFormatter.buildSearchSnippet(getLocalizer(), desc, conn));
				theSameFirst = false;
			}
			printEndPage(wr);
			wr.flush();
			return 200;
		} catch (SQLException e) {
			wr.write(("error: "+e.getLocalizedMessage()));
			wr.flush();
			return 500;
		}
	}

	@Path("/getimage")
	public int getImage(@FromQuery("dummy") String dummy, @FromQuery("id") String id, @ToBody(mimeType="image/*") final OutputStream os) throws IOException {
		try(final PreparedStatement	stmt = conn.prepareStatement("select \"bl_Image\" from \"elibrary\".\"booklist\" where \"bl_Id\" = ?::bigint", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {

			stmt.setString(1, id);
			try(final ResultSet		rs = stmt.executeQuery()) {
				
				if (rs.next()) {
					try(final InputStream	is = rs.getBinaryStream(1)) {
						Utils.copyStream(is, os);
					}
					return 200;
				}
				else {
					os.flush();
					return 200;
				}
			}
		} catch (SQLException e) {
			os.write(("error: "+e.getLocalizedMessage()).getBytes());
			os.flush();
			return 500;
		}
	}
	
	@Path("/getcontent")
	public int getContent(@FromQuery("dummy") String dummy, @FromQuery("id") String id, @ToHeader("Content-Type") final StringBuilder sb, @ToHeader("Content-Disposition") final StringBuilder sbD, @ToBody(mimeType="application/*,image/*") final OutputStream os) throws IOException {
		try(final PreparedStatement	stmt = conn.prepareStatement("select \"bl_Content\" from \"elibrary\".\"booklist\" where \"bl_Id\" = ?::bigint", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {

			sb.append("image/vnd");
			sbD.append("inline; filename=x.djvu");
			stmt.setString(1, id);
			try(final ResultSet		rs = stmt.executeQuery()) {
				
				if (rs.next()) {
					try(final InputStream	is = rs.getBinaryStream(1)) {
						Utils.copyStream(is, os);
					}
					return 200;
				}
				else {
					os.flush();
					return 200;
				}
			}
		} catch (SQLException e) {
			os.write(("error: "+e.getLocalizedMessage()).getBytes());
			os.flush();
			return 500;
		}
	}

	@Path("/gettotalserieslist")
	public int getTotalSeriesList(@FromQuery("dummy") String dummy, @FromQuery("id") String id, @ToBody(mimeType="text/html") final Writer wr) throws IOException {
		final String	seriesTitle = ResponseFormatter.extractReference(conn, "select \"bs_Name\" from \"elibrary\".\"bookseries\" where \"bs_Id\" = ?", Long.valueOf(id));
		
		try(final PreparedStatement	stmt = conn.prepareStatement("select * from \"elibrary\".\"booklist\" where \"bs_Id\" = ?::bigint order by \"bl_Year\"", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			stmt.setString(1, id);
			try(final ResultSet		rs = stmt.executeQuery()) {
				
				printStartPage(wr);
				wr.write(ResponseFormatter.buildListCaption(localizer, ResponseFormatter.SNIPPET_SERIES_LABEL, seriesTitle));
				wr.write("<table>\n");
				while (rs.next()) {
					mgr.loadInstance(rs, desc);
					wr.write(ResponseFormatter.buildItemSnippet(getLocalizer(), desc, conn));
				}
				wr.write("</table>\n");
				printEndPage(wr);
				wr.flush();
				return 200;
			}
		} catch (SQLException e) {
			wr.write(("error: "+e.getLocalizedMessage()));
			wr.flush();
			return 500;
		}
	}

	@Path("/gettotalauthorslist")
	public int getTotalAuthorsList(@FromQuery("dummy") String dummy, @FromQuery("id") String id, @ToBody(mimeType="text/html") final Writer wr) throws IOException {
		final String	authorsTitle = ResponseFormatter.extractReference(conn, "select \"ba_Name\" from \"elibrary\".\"bookauthors\" where \"ba_Id\" = ?", Long.valueOf(id));
		
		try(final PreparedStatement	stmt = conn.prepareStatement("select * from \"elibrary\".\"booklist\" where \"bl_Id\" in (select \"bl_Id\" from \"elibrary\".\"book2authors\" where \"ba_Id\" = ?::bigint) order by \"bl_Year\", \"bl_Title\"", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			stmt.setString(1, id);
			try(final ResultSet		rs = stmt.executeQuery()) {
				
				printStartPage(wr);
				wr.write(ResponseFormatter.buildListCaption(localizer, ResponseFormatter.SNIPPET_AUTHORS_LABEL, authorsTitle));
				wr.write("<table>\n");
				while (rs.next()) {
					mgr.loadInstance(rs, desc);
					wr.write(ResponseFormatter.buildItemSnippet(getLocalizer(), desc, conn));
				}
				wr.write("</table>\n");
				printEndPage(wr);
				wr.flush();
				return 200;
			}
		} catch (SQLException e) {
			wr.write(("error: "+e.getLocalizedMessage()));
			wr.flush();
			return 500;
		}
	}

	@Path("/gettotalpublisherslist")
	public int getTotalPublishersList(@FromQuery("dummy") String dummy, @FromQuery("id") String id, @ToBody(mimeType="text/html") final Writer wr) throws IOException {
		final String	publishersTitle = ResponseFormatter.extractReference(conn, "select \"bp_Name\" from \"elibrary\".\"bookpublishers\" where \"bp_Id\" = ?", Long.valueOf(id));
		
		try(final PreparedStatement	stmt = conn.prepareStatement("select * from \"elibrary\".\"booklist\" where \"bp_Id\" = ?::bigint order by \"bl_Year\", \"bl_Title\"", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			stmt.setString(1, id);
			try(final ResultSet		rs = stmt.executeQuery()) {
				
				printStartPage(wr);
				wr.write(ResponseFormatter.buildListCaption(localizer, ResponseFormatter.SNIPPET_PUBLISHER_LABEL, publishersTitle));
				wr.write("<table>\n");
				while (rs.next()) {
					mgr.loadInstance(rs, desc);
					wr.write(ResponseFormatter.buildItemSnippet(getLocalizer(), desc, conn));
				}
				wr.write("</table>\n");
				printEndPage(wr);
				wr.flush();
				return 200;
			}
		} catch (SQLException e) {
			wr.write(("error: "+e.getLocalizedMessage()));
			wr.flush();
			return 500;
		}
	}

	@Path("/gettotalyearlist")
	public int getTotalYearList(@FromQuery("dummy") String dummy, @FromQuery("id") String year, @ToBody(mimeType="text/html") final Writer wr) throws IOException {
		try(final PreparedStatement	stmt = conn.prepareStatement("select * from \"elibrary\".\"booklist\" where \"bl_Year\" = ?::bigint order by \"bl_Title\"", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			stmt.setString(1, year);
			try(final ResultSet		rs = stmt.executeQuery()) {
				
				printStartPage(wr);
				wr.write(ResponseFormatter.buildListCaption(localizer, ResponseFormatter.SNIPPET_YEAR_LABEL, year));
				wr.write("<table>\n");
				while (rs.next()) {
					mgr.loadInstance(rs, desc);
					wr.write(ResponseFormatter.buildItemSnippet(getLocalizer(), desc, conn));
				}
				wr.write("</table>\n");
				printEndPage(wr);
				wr.flush();
				return 200;
			}
		} catch (SQLException e) {
			wr.write(("error: "+e.getLocalizedMessage()));
			wr.flush();
			return 500;
		}
	}
	
	@Override
	public void allowUnnamedModuleAccess(final Module... unnamedModules) {
		for (Module item : unnamedModules) {
			this.getClass().getModule().addExports(this.getClass().getPackageName(),item);
		}
	}

	@Override
	public void close() throws SQLException, IOException {
		mgr.close();
		conn.close();
		loader.close();
	}

	@Override
	public LoggerFacade getLogger() {
		return PureLibSettings.CURRENT_LOGGER;
	}

	@Override
	public Localizer getLocalizer() {
		return localizer;
	}

	@Override
	public ContentNodeMetadata getNodeMetadata() {
		return mdi.getRoot();
	}

	private void printStartPage(final Writer os) throws IOException {
		os.write("<!DOCTYPE html>\n");
		os.write("<html><body>\n");
	}

	private void printEndPage(final Writer os) throws IOException {
		os.write("</body></html>\n");
	}
}


