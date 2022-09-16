package chav1961.elibrary.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

import chav1961.elibrary.admin.entities.BookDescriptor;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.interfaces.LongItemAndReference;
import chav1961.purelib.ui.interfaces.ReferenceAndComment;

public class ResponseFormatter {
	public static final String	SNIPPET_DIV_CLASS = "snippetDiv";
	public static final String	SNIPPET_TABLE_CLASS = "snippetTable";
	public static final String	SNIPPET_TAGS_CLASS = "snippetTags";
	public static final String	SNIPPET_SEARCH_FORM_CLASS = "searchForm";

	public static final String	SNIPPET_SERIES_LABEL = "html.snippet.series.label";
	public static final String	SNIPPET_AUTHORS_LABEL = "html.snippet.authors.label";
	public static final String	SNIPPET_PUBLISHER_LABEL = "html.snippet.publisher.label";
	public static final String	SNIPPET_YEAR_LABEL = "html.snippet.year.label";
	public static final String	SNIPPET_PUBLISHED_IN_LABEL = "html.snippet.publishedIn.label";
	public static final String	SNIPPET_PAGE_LABEL = "html.snippet.page.label";
	public static final String	SNIPPET_TAGS_LABEL = "html.snippet.tags.label";
	public static final String	SNIPPET_QUERY_LABEL = "html.snippet.query.label";
	public static final String	SNIPPET_QUERY_SEARCH = "html.snippet.query.search";
	public static final String	SNIPPET_QUERY_PLACEHOLDER = "html.snippet.query.placehlder";
	public static final String	SNIPPET_QUERY_LUCENE_HELP = "html.snippet.query.lucenehelp";
	
	public static enum ContentPath {
		SNIPPET_IMAGE("/content/getimage"),
		SNIPPET_CONTENT("/content/getcontent"),
		SNIPPET_ITEM("/content/getitem"),
		SNIPPET_TOTAL_SERIES_LIST("/content/gettotalserieslist"),
		SNIPPET_TOTAL_AUTHORS_LIST("/content/gettotalauthorslist"),
		SNIPPET_TOTAL_PUBLISHERS_LIST("/content/gettotalpublisherslist"),
		SNIPPET_TOTAL_YEAR_LIST("/content/gettotalyearlist"),
		;
		
		private final String	contentPath;
		
		private ContentPath(final String contentPath) {
			this.contentPath = contentPath;
		}
		
		public String getContenPath() {
			return contentPath;
		}
	}
	
	public static String buildItemSnippet(final Localizer localizer, final BookDescriptor desc, final Connection conn) {
		final StringBuilder	sb = new StringBuilder();
		
		sb.append("<tr><td><img src=\"").append(buildHRef(ContentPath.SNIPPET_IMAGE,desc.id)).append("\" alt=\"").append("\" width=70").append(" height=100").append("></td>");
		sb.append("<td><h3><a href=\"").append(buildHRef(ContentPath.SNIPPET_CONTENT,desc.id)).append("\" type=\"").append(desc.content.getMimeType()).append("\">").append(desc.title).append("</a></h3>\n");
		sb.append("<p>").append(getSeries(localizer, desc, conn)).append(" ").append(getAuthors(localizer, desc, conn)).append("</p>");
		sb.append("<p>").append(getPublisher(localizer, desc, conn)).append(", ").append(getYear(localizer, desc)).append("</p>");
		if (desc.placedIn != null && desc.placedIn.getValue() != 0) {
			sb.append("<p>").append(getPublishedIn(localizer, desc, conn)).append("</p>");
		}
		sb.append("</td></tr>\n");
		
		return sb.toString();
	}	

	public static String buildItemDescriptor(final Localizer localizer, final BookDescriptor desc, final Connection conn) {
		final StringBuilder	sb = new StringBuilder();
		
		sb.append("<div class=\"").append(SNIPPET_DIV_CLASS).append("\">\n");
		sb.append("<table class=\"").append(SNIPPET_TABLE_CLASS).append("\">\n");
		sb.append("<tr><td><img src=\"").append(buildHRef(ContentPath.SNIPPET_IMAGE,desc.id)).append("\" alt=\"").append("\" width=200").append(" height=297").append("></td>");
		sb.append("<td><h3><a href=\"").append(buildHRef(ContentPath.SNIPPET_CONTENT,desc.id)).append("\" type=\"").append(desc.content.getMimeType()).append("\">").append(desc.title).append("</a></h3>\n");
		sb.append("<p>").append(getSeries(localizer, desc, conn)).append(" ").append(getAuthors(localizer, desc, conn)).append("</p>");
		sb.append("<p>").append(getPublisher(localizer, desc, conn)).append(", ").append(getYear(localizer, desc)).append("</p>");
		if (desc.placedIn != null && desc.placedIn.getValue() != 0) {
			sb.append("<p>").append(getPublishedIn(localizer, desc, conn)).append("</p>");
		}
		else if (!"0".equals(extractReference(conn, "select count(*) from \"elibrary\".\"booklist\" where \"bl_Parent\" = ?", desc.id))) {
			sb.append("<hr/>\n");
			sb.append(buildSnippetContent(localizer, desc, conn));
		}
		sb.append("</td></tr></table>\n");
		sb.append(getTags(localizer, desc));
		sb.append("</div>\n");
		
		return sb.toString();
	}
	
	public static String buildSearchSnippet(final Localizer localizer, final BookDescriptor desc, final Connection conn, final String fragment) {
		final StringBuilder	sb = new StringBuilder();
		
		sb.append("<div class=\"").append(SNIPPET_DIV_CLASS).append("\">\n");
		sb.append("<table class=\"").append(SNIPPET_TABLE_CLASS).append("\">\n");
		sb.append("<tr><td><img src=\"").append(buildHRef(ContentPath.SNIPPET_IMAGE,desc.id)).append("\" alt=\"").append("\" width=200").append(" height=297").append("></td>");
		sb.append("<td><h3><a href=\"").append(buildHRef(ContentPath.SNIPPET_CONTENT,desc.id)).append("\" type=\"").append(desc.content.getMimeType()).append("\">").append(desc.title).append("</a></h3>\n");
		sb.append("<p>").append(getSeries(localizer, desc, conn)).append(" ").append(getAuthors(localizer, desc, conn)).append("</p>");
		sb.append("<p>").append(getPublisher(localizer, desc, conn)).append(", ").append(getYear(localizer, desc)).append("</p>");
		if (desc.placedIn != null && desc.placedIn.getValue() != 0) {
			sb.append("<p>").append(getPublishedIn(localizer, desc, conn)).append("</p>");
		}
		sb.append("<hr/>\n");
		sb.append("<p>").append(fragment).append("</p>\n");
		sb.append("</td></tr></table>\n");
		sb.append(getTags(localizer, desc));
		sb.append("</div>\n");
		
		return sb.toString();
	}
	
	public static String buildSnippetContent(final Localizer localizer, final BookDescriptor desc, final Connection conn) {
		final StringBuilder	sb = new StringBuilder();
	
		try(final PreparedStatement	ps = conn.prepareStatement("select * from \"elibrary\".\"booklist\" where \"bl_Parent\" = ?")) {
			
			ps.setLong(1, desc.id);
			try(final ResultSet		rs = ps.executeQuery()) {
				boolean	theSameFirst = true;
				
				while (rs.next()) {
					if (theSameFirst) {
						theSameFirst = false;
						sb.append("<ul>");
					}
					sb.append("<li><a href=\"").append(buildHRef(ContentPath.SNIPPET_TOTAL_SERIES_LIST, rs.getLong("bl_Id"))).append("\">").append(escapeHtmlString(rs.getString("bl_Title"))).append("</a></li>");
				}
				if (!theSameFirst) {
					sb.append("</ul>");
				}
			}
		} catch (SQLException e) {
			sb.append("Error: ").append(e.getLocalizedMessage());
		}
		return sb.toString();
	}	
	
	public static String extractReference(final Connection conn, final String sql, final long id) {
		try(final PreparedStatement	ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			
			try(final ResultSet		rs = ps.executeQuery()) {
				
				if (rs.next()) {
					return rs.getString(1);
				}
				else {
					return "Not found ["+id+"]";
				}
			}
		} catch (SQLException e) {
			return "error: "+e.getLocalizedMessage();
		}
	}

	public static String buildListCaption(final Localizer localizer, final String key, final String value) {
		return "<h3>"+localizer.getValue(key)+": "+escapeHtmlString(value)+"</h3>";
	}

	
	private static String getSeries(final Localizer localizer, final BookDescriptor desc, final Connection conn) {
		if (desc.seriesNumber != null) {
			final StringBuilder	sb = new StringBuilder();
			final String		presentation = extractReference(conn, "select \"bs_Name\" from \"elibrary\".\"bookseries\" where \"bs_Id\" = ?", desc.seriesNumber.getValue());
			
			return sb.append(localizer.getValue(SNIPPET_SERIES_LABEL)).append(" <a href=\"").append(buildHRef(ContentPath.SNIPPET_TOTAL_SERIES_LIST, desc.seriesNumber.getValue())).append("\">").append(escapeHtmlString(presentation)).append("</a> #").append(escapeHtmlString(desc.seriesSeq)).toString();
		}
		else {
			return "";
		}
	}

	private static String getAuthors(final Localizer localizer, final BookDescriptor desc, final Connection conn) {
		final StringBuilder	sb = new StringBuilder();
		
		sb.append(localizer.getValue(SNIPPET_AUTHORS_LABEL)).append(" ");
		try(final PreparedStatement	ps = conn.prepareStatement("select \"ba_Name\" from \"elibrary\".\"bookauthors\" where \"ba_Id\" = ?")) {
			for (LongItemAndReference<String> item : desc.authors) {
				String				presentation = "";
				
				ps.setLong(1, item.getValue());
				
				try(final ResultSet		rs = ps.executeQuery()) {
					
					if (rs.next()) {
						presentation = rs.getString(1);
					}
				} catch (SQLException e) {
					presentation = "error: "+e.getLocalizedMessage();
				}
				sb.append("<a href=\"").append(buildHRef(ContentPath.SNIPPET_TOTAL_AUTHORS_LIST, item.getValue())).append("\">").append(escapeHtmlString(presentation)).append("</a> ");
			}
			return sb.toString();
		} catch (SQLException e) {
			return e.getLocalizedMessage();
		}
	}

	private static String getPublisher(final Localizer localizer, final BookDescriptor desc, final Connection conn) {
		final StringBuilder	sb = new StringBuilder();
		final String		presentation = extractReference(conn, "select \"bp_Name\" from \"elibrary\".\"bookpublishers\" where \"bp_Id\" = ?", desc.publisher.getValue());
		
		return sb.append(localizer.getValue(SNIPPET_PUBLISHER_LABEL)).append(" <a href=\"").append(buildHRef(ContentPath.SNIPPET_TOTAL_PUBLISHERS_LIST, desc.publisher.getValue())).append("\">").append(escapeHtmlString(presentation)).append("</a>").toString();
	}
	
	private static String getYear(final Localizer localizer, final BookDescriptor desc) {
		final StringBuilder	sb = new StringBuilder();
		
		return sb.append(localizer.getValue(SNIPPET_YEAR_LABEL)).append(" <a href=\"").append(buildHRef(ContentPath.SNIPPET_TOTAL_YEAR_LIST, desc.year)).append("\">").append(desc.year).append("</a>").toString();
	}

	private static String getPublishedIn(final Localizer localizer, final BookDescriptor desc, final Connection conn) {
		if (desc.placedIn != null) {
			final StringBuilder	sb = new StringBuilder();
			final String		presentation = extractReference(conn, "select \"bl_Title\" from \"elibrary\".\"booklist\" where \"bl_Id\" = ?", desc.placedIn.getValue());
			
			sb.append(" ").append(localizer.getValue(SNIPPET_PUBLISHED_IN_LABEL)).append(" <a href=\"").append(buildHRef(ContentPath.SNIPPET_ITEM, desc.placedIn.getValue())).append("\">")
					.append(escapeHtmlString(escapeHtmlString(presentation))).append("</a> ").append(localizer.getValue(SNIPPET_PAGE_LABEL)).append(" ").append(desc.page).append(" ");
			return sb.toString();
		}
		else {
			return "";
		}
	}
	
	private static String getTags(final Localizer localizer, final BookDescriptor desc) {
		if (desc.tags != null && desc.tags.length > 0) {
			final StringBuilder	sb = new StringBuilder();

			sb.append("<table class=\"").append(SNIPPET_TAGS_CLASS).append("\">\n");
			sb.append("<tr><td>").append(localizer.getValue(SNIPPET_TAGS_LABEL)).append("</td><td>");
			sb.append("<ul>\n");
			for (ReferenceAndComment item : desc.tags) {
				sb.append("<li><a href=\"").append(item.getReference()).append("\">").append(escapeHtmlString(item.getComment())).append("</a></li>\n");
			}
			sb.append("</ul>\n");
			sb.append("</td></tr>\n");
			sb.append("</table>\n");

			return sb.toString();
		}
		else {
			return "";
		}
	}

	private static String escapeHtmlString(final String source) {
		if (source == null || source.isEmpty()) {
			return "";
		}
		else {
			return source.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
		}
	}
	
	private static String buildHRef(final ContentPath contentPath, final long id) {
		return contentPath.getContenPath()+"?id="+id;
	}
}
