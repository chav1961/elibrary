package chav1961.elibrary.admin.db;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import chav1961.elibrary.admin.entities.BookDescriptor;
import chav1961.elibrary.admin.entities.SeriesDescriptor;
import chav1961.purelib.basic.exceptions.FlowException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.sql.interfaces.InstanceManager;
import chav1961.purelib.sql.interfaces.UniqueIdGenerator;
import chav1961.purelib.streams.JsonStaxParser;
import chav1961.purelib.streams.JsonStaxPrinter;
import chav1961.purelib.streams.interfaces.JsonStaxParserLexType;
import chav1961.purelib.ui.interfaces.ReferenceAndComment;
import chav1961.purelib.ui.interfaces.RecordFormManager.RecordAction;

public class BooksDescriptorMgr implements InstanceManager<Long, BookDescriptor> {
	private static final ReferenceAndComment[]					EMPTY_TAGS = new ReferenceAndComment[0];
	
	private final LoggerFacade		logger;
	private final BookDescriptor	desc;
	private final UniqueIdGenerator	uig;
	
	public BooksDescriptorMgr(final LoggerFacade logger, final BookDescriptor desc, final UniqueIdGenerator uig) {
		this.logger = logger;
		this.desc = desc;
		this.uig = uig;
	}
	
	@Override
	public Class<?> getInstanceType() {
		return SeriesDescriptor.class;
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
		try{desc.onRecord(RecordAction.INSERT, null, null, desc, newKey());
			return desc;
		} catch (FlowException e) {
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
		inst.id = rs.getLong("bs_Id");
		inst.code  = rs.getInt("bl_Code");
		inst.seriesNumber.setValue(rs.getLong("bs_Id"));
		inst.title = rs.getString("bl_Title");
		inst.year = rs.getInt("bl_Year");
		inst.publisher.setValue(rs.getLong("bp_Id"));
		inst.authors[0].setValue(rs.getLong("ba_Id"));
		inst.annotation = rs.getString("bl_Comment");
		inst.tags = fromString(rs.getString("bl_Tags"));
		inst.image = null; 
	}

	@Override
	public void storeInstance(final ResultSet rs, final BookDescriptor inst, final boolean update) throws SQLException {
		rs.updateInt("bl_code", inst.code);
		rs.updateLong("bs_Id", inst.seriesNumber.getValue());
		rs.updateString("bl_Title", inst.title);
		rs.updateInt("bl_Year", inst.year);
		rs.updateLong("bp_Id", inst.publisher.getValue());
		rs.updateString("bl_Comment", inst.annotation);
		rs.updateString("bl_Tags", toString(inst.tags));
		if (inst.image != null) {
			rs.updateObject("bl_Image", inst.image);
		}
		else {
			rs.updateNull("bl_Image");
		}
		if (!update) {
			rs.updateLong("bl_Id", inst.id);
		}
	}

	@Override
	public <T> T get(final BookDescriptor inst, final String name) throws SQLException {
		switch (name) {
			case "bl_Id" 		: return (T) Long.valueOf(inst.id);
			case "bl_Code" 		: return (T) Integer.valueOf(inst.code);
			case "bs_Id" 		: return (T) Long.valueOf(inst.seriesNumber.getValue());
			case "bl_Title"		: return (T) inst.title;
			case "bl_Year" 		: return (T) Integer.valueOf(inst.year);
			case "bp_Id" 		: return (T) Long.valueOf(inst.publisher.getValue());
			case "bl_Comment"	: return (T) inst.annotation;
			case "bl_Tags" 		: return (T) inst.tags;
			case "bl_Image" 	: return (T) inst.image;
			default : throw new SQLException("Name ["+name+"] is missing in the instance");
		}
	}

	@Override
	public <T> InstanceManager<Long, BookDescriptor> set(final BookDescriptor inst, final String name, final T value) throws SQLException {
		switch (name) {
			case "ba_Id" 		: 
				inst.id = (Long)value;
				break;
//			case "ba_Name" 		:
//				inst.name = (String)value;
//				break;
//			case "ba_Comment"	:
//				inst.comment = (String)value;
//				break;
			default :
				throw new SQLException("Name ["+name+"] is missing in the instance");
		}
		return this;
	}

	@Override
	public void close() throws SQLException {
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
					prn.startObject().name("ref").value(item.getReference().toASCIIString())
						.name("comment").value(item.getComment()).endObject();
				}
				theSameFirst = false;
			}
			prn.endArray();
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
