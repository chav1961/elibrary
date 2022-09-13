package chav1961.elibrary.service;

import chav1961.elibrary.admin.entities.BookDescriptor;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.interfaces.LongItemAndReference;
import chav1961.purelib.ui.interfaces.ReferenceAndComment;

public class ResponseFormatter {
	public static final String	SNIPPET_DIV_CLASS = "snippetDiv";
	public static final String	SNIPPET_TABLE_CLASS = "snippetTable";
	public static final String	SNIPPET_TAGS_CLASS = "snippetTags";

	public static final String	SNIPPET_SERIES_LABEL = "html.snippet.series.label";
	public static final String	SNIPPET_AUTHORS_LABEL = "html.snippet.authors.label";
	public static final String	SNIPPET_PUBLISHER_LABEL = "html.snippet.publisher.label";
	public static final String	SNIPPET_YEAR_LABEL = "html.snippet.year.label";
	public static final String	SNIPPET_PUBLISHED_IN_LABEL = "html.snippet.publishedIn.label";
	public static final String	SNIPPET_PAGE_LABEL = "html.snippet.page.label";
	public static final String	SNIPPET_TAGS_LABEL = "html.snippet.tags.label";

	public static enum ContentPath {
		SNIPPET("");
		
		private final String	contentPath;
		
		private ContentPath(final String contentPath) {
			this.contentPath = contentPath;
		}
		
		public String getContenPath() {
			return contentPath;
		}
	}
	
	public static String buildSearchSnippet(final Localizer localizer, final BookDescriptor desc) {
		final StringBuilder	sb = new StringBuilder();
		
		sb.append("<div class=\"").append(SNIPPET_DIV_CLASS).append(">\n");
		sb.append("<table class=\"").append(SNIPPET_TABLE_CLASS).append(">\n");
		sb.append("<tr><td><img src=\"").append("\" alt=\"").append("\" width=").append(" height=").append("></td>");
		sb.append("<td><h3><a href=\"\">").append("").append("</a></h3>\n");
		sb.append("<p>").append(getSeries(localizer, desc)).append(" ").append(getAuthors(localizer, desc)).append("</p>");
		sb.append("<p>").append(getPublisher(localizer, desc)).append(" ").append(getYear(localizer, desc)).append(" ").append(getPublishedIn(localizer, desc)).append("</p>");
		sb.append("<hr/>\n");
		sb.append("</table>\n");
		sb.append(getTags(localizer, desc));
		sb.append("</div>\n");
		
		return sb.toString();
	}

	private static String getSeries(final Localizer localizer, final BookDescriptor desc) {
		if (desc.seriesNumber != null) {
			final StringBuilder	sb = new StringBuilder();
			
			return sb.append(localizer.getValue(SNIPPET_SERIES_LABEL)).append(" </a href=\"").append("???").append(">").append("???").append("</a>").toString();
		}
		else {
			return "";
		}
	}

	private static String getAuthors(final Localizer localizer, final BookDescriptor desc) {
		final StringBuilder	sb = new StringBuilder();
		
		sb.append(localizer.getValue(SNIPPET_AUTHORS_LABEL)).append(" ");
		for (LongItemAndReference<String> item : desc.authors) {
			sb.append("<a href=\"").append("???").append("\">").append(escapeHtmlString(item.getKeyName())).append("</a> ");
		}
		return sb.toString();
	}

	private static String getPublisher(final Localizer localizer, final BookDescriptor desc) {
		final StringBuilder	sb = new StringBuilder();
		
		return sb.append(localizer.getValue(SNIPPET_PUBLISHER_LABEL)).append(" </a href=\"").append("???").append(">").append(escapeHtmlString(desc.publisher.getKeyName())).append("</a>").toString();
	}
	
	private static String getYear(final Localizer localizer, final BookDescriptor desc) {
		final StringBuilder	sb = new StringBuilder();
		
		return sb.append(localizer.getValue(SNIPPET_YEAR_LABEL)).append(" </a href=\"").append("???").append(">").append(desc.year).append("</a>").toString();
	}

	private static String getPublishedIn(final Localizer localizer, final BookDescriptor desc) {
		if (desc.placedIn != null) {
			final StringBuilder	sb = new StringBuilder();
			
			sb.append(" ").append(localizer.getValue(SNIPPET_PUBLISHED_IN_LABEL)).append(" <a href=\"").append("???").append("\">")
					.append(escapeHtmlString(escapeHtmlString(desc.placedIn.getKeyName()))).append("</a> ").append(localizer.getValue(SNIPPET_PAGE_LABEL)).append(" ").append(desc.page).append(" ");
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
		return source.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
	
	private static String buildHRef(final ContentPath contentPath, final long id) {
		return "";
	}
}
