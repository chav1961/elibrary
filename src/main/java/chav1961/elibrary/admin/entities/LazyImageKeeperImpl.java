package chav1961.elibrary.admin.entities;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import chav1961.elibrary.admin.db.ContentManipulator;
import chav1961.purelib.json.ImageKeeper;

public class LazyImageKeeperImpl extends ImageKeeper {
	private static final long serialVersionUID = 1L;
	
	private final Context	modelContext;
	private long			contentKey = -1;
	private boolean			needDownload = false;
	private boolean			contentChanged = false;
	
	public LazyImageKeeperImpl() throws NamingException {
		this.modelContext = new InitialContext();
	}
	
	@Override
	public Image getImage() {
		ensureContentLoaded();
		return super.getImage();
	}

	@Override
	public void setImage(Image image) {
		super.setImage(image);
		contentChanged = true;
	}
	
	public void setContentKey(final long contentKey) {
		this.contentKey = contentKey; 
		this.needDownload = true;
		this.contentChanged = false;
	}
	
	public boolean isContentChanged() {
		return contentChanged;
	}
	
	private void ensureContentLoaded() {
		if (needDownload) {
			try{final ContentManipulator	cm = (ContentManipulator)modelContext.lookup("models/content");
			
				setImage(ImageIO.read(new ByteArrayInputStream(cm.loadImage(contentKey))));
			} catch (NamingException | SQLException |IOException e) {
				e.printStackTrace();
			}
			needDownload = false;
		}
	}
	
}
