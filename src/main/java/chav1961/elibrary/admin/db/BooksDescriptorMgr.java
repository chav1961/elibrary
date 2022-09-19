package chav1961.elibrary.admin.db;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import chav1961.elibrary.admin.entities.AuthorsDescriptor;
import chav1961.elibrary.admin.entities.BookDescriptor;
import chav1961.elibrary.admin.entities.LazyImageKeeperImpl;
import chav1961.elibrary.admin.entities.LazyMimeBasedContentImpl;
import chav1961.elibrary.admin.entities.SeriesDescriptor;
import chav1961.purelib.basic.MimeType;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.MimeParseException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.model.ImageKeeperImpl;
import chav1961.purelib.sql.interfaces.InstanceManager;
import chav1961.purelib.sql.interfaces.UniqueIdGenerator;
import chav1961.purelib.streams.JsonStaxParser;
import chav1961.purelib.streams.JsonStaxPrinter;
import chav1961.purelib.streams.interfaces.JsonStaxParserLexType;
import chav1961.purelib.ui.interfaces.LongItemAndReference;
import chav1961.purelib.ui.interfaces.ReferenceAndComment;
import chav1961.purelib.ui.interfaces.RecordFormManager.RecordAction;

public class BooksDescriptorMgr implements InstanceManager<Long, BookDescriptor> {
	private final BookDescriptor	desc;
	private final UniqueIdGenerator	uig;
	private final PreparedStatement	ps;
	
	public BooksDescriptorMgr(final LoggerFacade logger, final BookDescriptor desc, final UniqueIdGenerator uig, final Connection conn) throws SQLException {
		this.desc = desc;
		this.uig = uig;
		this.ps = conn.prepareStatement("select \"bl_Id\", \"ba_Id\" from \"elibrary\".\"book2authors\" where \"bl_Id\" = ?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
	}
	
	@Override
	public Class<?> getInstanceType() {
		return BookDescriptor.class;
	}

	@Override
	public Class<?> getKeyType() {
		return Long.class;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public BookDescriptor newInstance() throws SQLException {
		try{final Long				key = newKey();
			final BookDescriptor	newInst = desc.clone();
		
			assignKey(newInst, key);
			newInst.onRecord(RecordAction.INSERT, null, null, newInst, key);
			return newInst;
		} catch (FlowException | CloneNotSupportedException e) {
			throw new SQLException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Long newKey() throws SQLException {
		return uig.getId();
	}

	@Override
	public Long extractKey(final BookDescriptor inst) throws SQLException {
		return inst.id;
	}
	
	@Override
	public Long extractKey(final ResultSet rs) throws SQLException {
		return rs.getLong("bl_Id");
	}
	
	
	@Override
	public void assignKey(final BookDescriptor inst, final Long key) throws SQLException {
		inst.id = key;
	}
	
	@Override
	public BookDescriptor clone(final BookDescriptor inst) throws SQLException {
		try{final BookDescriptor	clone = inst.clone();

			clone.id = newKey();
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new SQLException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void loadInstance(final ResultSet rs, final BookDescriptor inst) throws SQLException {
		if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
			rs.refreshRow();
		}
		inst.id = rs.getLong("bl_Id");
		inst.placedIn.setValue(rs.getLong("bl_Parent"));
		inst.code  = rs.getString("bl_Code");
		inst.seriesNumber.setValue(rs.getLong("bs_Id"));
		inst.seriesSeq = rs.getString("bs_Seq");
		inst.title = rs.getString("bl_Title");
		inst.year = rs.getInt("bl_Year");
		inst.publisher.setValue(rs.getLong("bp_Id"));
		inst.annotation = rs.getString("bl_Comment");
		inst.tags = fromString(rs.getString("bl_Tags"));
		inst.page = rs.getInt("bl_Page");
		((LazyImageKeeperImpl)inst.image).setContentKey(inst.id);
		
		try{inst.content.setMimeType(MimeType.parseMimeList(rs.getString("bl_Mime"))[0]);
			((LazyMimeBasedContentImpl)inst.content).setContentKey(inst.id);
		} catch (MimeParseException | IOException e) {
			throw new SQLException(e.getLocalizedMessage(), e);
		}
		
		final List<LongItemAndReference<String>>	list = new ArrayList<>();

		ps.setLong(1, inst.id);		
		try(final ResultSet	rs1 = ps.executeQuery()) {
			
			while (rs1.next()) {
				final LongItemAndReference<String>	item = (LongItemAndReference<String>) inst.authors[0].clone();
				
				item.setValue(rs1.getLong(2));
				list.add(item);
			}
			if (list.isEmpty()) {
				final LongItemAndReference<String>	item = (LongItemAndReference<String>) inst.authors[0].clone();
				
				item.setValue(-1L);
				list.add(inst.authors[0]);
			}
		} catch (CloneNotSupportedException e) {
			throw new SQLException(e);
		}
		inst.authors = list.toArray(new LongItemAndReference[list.size()]);
	}

	@Override
	public void storeInstance(final ResultSet rs, final BookDescriptor inst, final boolean update) throws SQLException {
		if (inst.placedIn.getValue() > 0) {
			rs.updateLong("bl_Parent", inst.placedIn.getValue());
		}
		else {
			rs.updateNull("bl_Parent");
		}
		rs.updateString("bl_code", inst.code);
		rs.updateLong("bs_Id", inst.seriesNumber.getValue());
		rs.updateString("bs_Seq", inst.seriesSeq);
		rs.updateString("bl_Title", inst.title);
		rs.updateInt("bl_Year", inst.year);
		rs.updateLong("bp_Id", inst.publisher.getValue());
		rs.updateString("bl_Comment", inst.annotation);
		rs.updateString("bl_Tags", toString(inst.tags));
		rs.updateInt("bl_Page", inst.page);
		
		if (((LazyImageKeeperImpl)inst.image).isModified()) {
			try(final ByteArrayOutputStream	baos = new ByteArrayOutputStream()) {
				ImageIO.write((RenderedImage) inst.image.getImage(), "png", baos);
				
				rs.updateBytes("bl_Image", baos.toByteArray());
				((LazyImageKeeperImpl)inst.image).setModified(false);
			} catch (IOException e) {
				throw new SQLException(e.getLocalizedMessage(), e);
			}
		}
		
		if (((LazyMimeBasedContentImpl)inst.content).isContentChanged()) {
			rs.updateString("bl_Mime", inst.content.getMimeType().toString());
			try(final InputStream	is = inst.content.getContent();
				final ByteArrayOutputStream	baos = new ByteArrayOutputStream()) {

				Utils.copyStream(is, baos);
				rs.updateBytes("bl_Content", baos.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
				throw new SQLException(e.getLocalizedMessage(), e);
			}
		}
		else if (!update) {
			rs.updateString("bl_Mime", PureLibSettings.MIME_OCTET_STREAM.toString());
			rs.updateBytes("bl_Content", new byte[0]);
		}
		
		if (!update) {
			rs.updateLong("bl_Id", inst.id);
		}
		
		ps.setLong(1, inst.id);	// Refresh authors list		
		try(final ResultSet	rs1 = ps.executeQuery()) {
			
			while (rs1.next()) {
				rs1.deleteRow();
			}
			for (LongItemAndReference<String> item : inst.authors) {
				if (item.getValue() != -1L) {
					rs1.moveToInsertRow();
					rs1.updateLong(1, inst.id);
					rs1.updateLong(2, item.getValue());
					rs1.insertRow();
				}
			}
		}
	}

	@Override
	public <T> T get(final BookDescriptor inst, final String name) throws SQLException {
		switch (name) {
			case "bl_Id" 		: return (T) Long.valueOf(inst.id);
			case "bl_Parent"	: return (T) Long.valueOf(inst.placedIn.getValue());
			case "bl_Code" 		: return (T) inst.code;
			case "bs_Id" 		: return (T) Long.valueOf(inst.seriesNumber.getValue());
			case "bs_Seq" 		: return (T) inst.seriesSeq;
			case "bl_Title"		: return (T) inst.title;
			case "bl_Year" 		: return (T) Integer.valueOf(inst.year);
			case "bp_Id" 		: return (T) Long.valueOf(inst.publisher.getValue());
			case "bl_Comment"	: return (T) inst.annotation;
			case "bl_Tags" 		: return (T) inst.tags;
			case "bl_Image" 	: return (T) inst.image.getImage();
			case "bl_Page" 		: return (T) Integer.valueOf(inst.page);
			default : throw new SQLException("Name ["+name+"] is missing in the instance");
		}
	}

	@Override
	public <T> InstanceManager<Long, BookDescriptor> set(final BookDescriptor inst, final String name, final T value) throws SQLException {
		switch (name) {
			case "bl_Id" 		: 
				inst.id = (Long)value;
				break;
			case "bl_Parent"	: 
				inst.placedIn.setValue((Long)value);
				break;
			case "bl_Code" 		: 
				inst.code = (String)value;
				break;
			case "bs_Id" 		: 
				inst.seriesNumber.setValue((Long)value);
				break;
			case "bs_Seq" 		: 
				inst.seriesSeq = (String)value;
				break;
			case "bl_Title"		: 
				inst.title = (String)value;
				break;
			case "bl_Year" 		: 
				inst.year = (Integer)value;
				break;
			case "bp_Id" 		: 
				inst.publisher.setValue((Long)value);
				break;
			case "bl_Comment"	: 
				inst.annotation = (String)value;
				break;
//			case "bl_Tags" 		: inst.tags;
			case "bl_Image" 	: 
				inst.image.setImage((Image)value);
				break;
			case "bl_Page" 	: 
				inst.page = (Integer)value;
				break;
			default : throw new SQLException("Name ["+name+"] is missing in the instance");
		}
		return this;
	}

	@Override
	public void close() throws SQLException {
		ps.close();
	}

	@Override
	public void storeInstance(PreparedStatement ps, BookDescriptor inst, boolean update) throws SQLException {
		throw new UnsupportedOperationException("This method is not implemented yet"); 
	}

	private static String toString(final ReferenceAndComment[] tags) throws SQLException {
		try(final Writer			wr = new StringWriter();
			final JsonStaxPrinter	prn = new JsonStaxPrinter(wr)) {
			boolean		theSameFirst = true;
			
			prn.startArray();
			for (ReferenceAndComment item : tags) {
				if(!theSameFirst) {
					prn.splitter();
				}
				prn.startObject().name("ref").value(item.getReference().toASCIIString()).splitter().name("comment").value(item.getComment()).endObject();
				theSameFirst = false;
			}
			prn.endArray();
			prn.flush();
			return wr.toString();
		} catch (IOException e) {
			throw new SQLException(e.getLocalizedMessage(), e);
		}
	}

	private static ReferenceAndComment[] fromString(final String string) throws SQLException {
		try(final Reader			rdr = new StringReader(string);
			final JsonStaxParser	parser = new JsonStaxParser(rdr)) {
			final List<ReferenceAndComment>	result = new ArrayList<>();
			
			String					forName = "", forUri = "", forComment = "";

loop:		for(JsonStaxParserLexType item : parser) {
				switch (item) {
					case START_ARRAY : case START_OBJECT : case LIST_SPLITTER : case NAME_SPLITTER :
						break;
					case NAME			:
						forName = parser.name();
						break;
					case STRING_VALUE	:
						switch (forName) {
							case "ref" 		:
								forUri = parser.stringValue();
								break;
							case "comment"	:
								forComment = parser.stringValue();
								break;
							default :
								throw new IOException(new SyntaxException(parser.row(), parser.col(), "Unsupported field name ["+forName+"]"));
						}
						break;
					case END_OBJECT		:
						result.add(ReferenceAndComment.of(URI.create(forUri), forComment));
						forUri = forComment = "";
						break;
					case END_ARRAY		:
						break loop;
					default:
						throw new IOException(new SyntaxException(parser.row(), parser.col(), "Unwaited lexema"));
				}
			}
			return result.toArray(new ReferenceAndComment[result.size()]); 
		} catch (IOException e) {
			throw new SQLException(e.getLocalizedMessage(), e);
		}
	}

}
