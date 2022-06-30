package chav1961.elibrary.admin.entities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import chav1961.purelib.basic.MimeType;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.ui.interfaces.MimeBasedContent;

public class MimeBasedContentImpl implements MimeBasedContent {
	private static final byte[]	EMPTY = new byte[0];
	
	private boolean		contentFilled = false;
	private MimeType 	currentMime = PureLibSettings.MIME_OCTET_STREAM;
	private byte[]		content = EMPTY;

	public MimeBasedContentImpl() {
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
		return content.length;
	}

	@Override
	public void setMimeType(final MimeType type) throws IOException {
		if (type == null) {
			throw new NullPointerException("Content type can't be null");
		}
		else {
			this.currentMime = type;
		}
	}

	@Override
	public InputStream getContent() throws IOException {
		return new ByteArrayInputStream(content);
	}

	@Override
	public OutputStream putContent() throws IOException {
		return new ByteArrayOutputStream() {@Override
						public void close() throws IOException {
							super.close();
							content = toByteArray();
							contentFilled = true;
						}
					};
	}

	@Override
	public void clearContent() throws IOException {
		this.content = EMPTY;
		this.contentFilled = false;
	}

	@Override
	public String toString() {
		return "MimeBasedContentImpl [contentFilled=" + contentFilled + ", currentMime=" + currentMime + ", content.length=" + content.length + "]";
	}
}
