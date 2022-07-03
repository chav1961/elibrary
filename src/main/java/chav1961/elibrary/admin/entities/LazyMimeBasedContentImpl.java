package chav1961.elibrary.admin.entities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import chav1961.elibrary.admin.db.ContentManipulator;
import chav1961.purelib.basic.MimeType;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.ui.interfaces.MimeBasedContent;

public class LazyMimeBasedContentImpl implements MimeBasedContent {
	private static final byte[]	EMPTY = new byte[0];
	
	private final Context	modelContext;
	private long			contentKey = -1;
	private boolean			contentFilled = false;
	private boolean			needDownload = false;
	private boolean			contentChanged = false;
	private MimeType 		currentMime = PureLibSettings.MIME_OCTET_STREAM;
	private byte[]			content = EMPTY;

	public LazyMimeBasedContentImpl() throws NamingException {
		this.modelContext = new InitialContext();
	}

	@Override
	public Class<String> getPresentationClass() {
		return String.class;
	}

	@Override
	public String getPresentation() {
		return currentMime.toString();
	}
	
	@Override
	public MimeType getMimeType() {
		return currentMime;
	}

	@Override
	public boolean isContentFilled() {
		return contentFilled;
	}

	@Override
	public long getContentSize() throws IOException {
		ensureContentLoaded();
		return content.length;
	}


	@Override
	public void setMimeType(final MimeType type) throws IOException {
		if (type == null) {
			throw new NullPointerException("Content type can't be null");
		}
		else {
			this.currentMime = type;
			this.contentChanged = true;
		}
	}

	@Override
	public InputStream getContent() throws IOException {
		ensureContentLoaded();
		return new GZIPInputStream(new ByteArrayInputStream(content));
	}

	@Override
	public OutputStream putContent() throws IOException {
		final OutputStream	os = new ByteArrayOutputStream() {
									@Override
									public void close() throws IOException {
										flush();
										super.close();
										content = toByteArray();
										contentFilled = true;
										contentChanged = true;
									}
								};
		return  new GZIPOutputStream(os) {
									@Override
									public void close() throws IOException {
										flush();
										super.close();
										os.close();
									}
								};
	}

	@Override
	public void clearContent() throws IOException {
		this.content = EMPTY;
		this.contentFilled = false;
		this.contentChanged = true;
	}

	public void setContentKey(final long contentKey) {
		this.contentKey = contentKey; 
		this.contentFilled = true;
		this.needDownload = true;
		this.contentChanged = false;
	}

	public boolean isContentChanged() {
		return contentChanged;
	}
	
	@Override
	public String toString() {
		return "MimeBasedContentImpl [contentFilled=" + contentFilled + ", currentMime=" + currentMime + ", content.length=" + content.length + "]";
	}

	private void ensureContentLoaded() throws IOException {
		if (needDownload) {
			if (!contentChanged) {
				try{final ContentManipulator	cm = (ContentManipulator)modelContext.lookup("models/content");
				
					content = cm.loadContent(contentKey);
				} catch (NamingException | SQLException e) {
					throw new IOException(e); 
				}
			}
			needDownload = false;
		}
	}
}
