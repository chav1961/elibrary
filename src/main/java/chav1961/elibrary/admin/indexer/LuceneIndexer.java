package chav1961.elibrary.admin.indexer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;

import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.growablearrays.GrowableLongArray;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacadeOwner;
import chav1961.purelib.basic.interfaces.ProgressIndicator;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.LocalizerOwner;
import chav1961.purelib.sql.JDBCUtils;

public class LuceneIndexer implements LoggerFacadeOwner, LocalizerOwner, Closeable {
	private static final String	KEY_START_INDEXING = "";
	
	private final Localizer		localizer;
	private final LoggerFacade	logger;
	private final File			luceneDirectory;
	private Directory 			index;
	
	
	public LuceneIndexer(final Localizer localizer, final LoggerFacade logger, final File luceneDirectory) throws NullPointerException, IllegalArgumentException, IOException {
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (logger == null) {
			throw new NullPointerException("Logger can't be null");
		}
		else if (luceneDirectory == null) {
			throw new NullPointerException("Lucene directory file can't be null");
		}
		else if (luceneDirectory.exists() && (luceneDirectory.isFile() || !luceneDirectory.canRead() || !luceneDirectory.canWrite())) {
			throw new IllegalArgumentException("Lucene directory ["+luceneDirectory.getAbsolutePath()+"] is file or doesn't have access rights to manipulate it");
		}
		else {
			this.localizer = localizer;			
			this.logger = logger;
			this.luceneDirectory = luceneDirectory;
			
			if (!luceneDirectory.exists()) {
				if (!luceneDirectory.mkdirs()) {
					throw new IllegalArgumentException("Lucene directory ["+luceneDirectory.getAbsolutePath()+"] cen't be created");
				}
			}
			this.index = new NIOFSDirectory(luceneDirectory.toPath());
		}
	}

	@Override
	public LoggerFacade getLogger() {
		return logger;
	}	

	@Override
	public Localizer getLocalizer() {
		return localizer;
	}
	
	@Override
	public void close() throws IOException {
		index.close();
	}

	public void clear() throws SQLException {
		try{index.close();
			Utils.deleteDir(luceneDirectory);
			luceneDirectory.mkdirs();
			index = new NIOFSDirectory(luceneDirectory.toPath());
		} catch (IOException e) {
			throw new SQLException(e.getLocalizedMessage(), e);
		}
	}
	
	public void createIndex(final Connection conn) throws SQLException {
		createIndex(conn, ProgressIndicator.DUMMY);
	}
	
	public void createIndex(final Connection conn, final ProgressIndicator pi) throws SQLException {
		try(final StandardAnalyzer	analyzer = new StandardAnalyzer()) {
			final IndexWriterConfig	config = new IndexWriterConfig(analyzer);
			
			try(final Statement	stmt = conn.createStatement()) {
				final int		count;

				try(final ResultSet	rs = stmt.executeQuery("select count(*) from \"e\".\"e\"")) {
					if (rs.next()) {
						count = rs.getInt(1);
					}
					else {
						count = 0;
					}
				}
				
				int	current = 0;
				
				pi.start(localizer.getValue(KEY_START_INDEXING), count);
				try(final ResultSet		rs = stmt.executeQuery("select * from \"e\".\"e\""); 
					final IndexWriter	wr = new IndexWriter(index, config)) {
					
					addDoc(wr, rs);
					pi.processed(current++);
				} finally {
					pi.end();
				}
			} catch (IOException e) {
				throw new SQLException(e.getLocalizedMessage(), e);
			}
		}
	}
	
	private static void addDoc(final IndexWriter wr, final ResultSet rs) throws IOException, SQLException {
	  final Document doc = new Document();
	  
	  doc.add(new StoredField("bl_Id", rs.getLong("bl_Id")));
	  doc.add(new StringField("bl_Title", rs.getString("bl_Title"), Field.Store.YES));
	  doc.add(new TextField("bl_Comment", rs.getString("bl_Comment"), Field.Store.YES));
	  wr.addDocument(doc);
	}

	public long[] search(final String queryStr, final int hitsPerPage) throws IOException, SyntaxException {
		try(final StandardAnalyzer 	analyzer = new StandardAnalyzer()) {
			final Query 			q = new QueryParser("bl_Comment", analyzer).parse(queryStr);
	
	        try(final IndexReader 	reader = DirectoryReader.open(index)) {
	            final IndexSearcher searcher = new IndexSearcher(reader);
	            final TopDocs 		docs = searcher.search(q, hitsPerPage);
	            final ScoreDoc[] 	hits = docs.scoreDocs;
	            final GrowableLongArray	gla = new GrowableLongArray(false);
	            
//		        System.out.println("Found " + hits.length + " hits.");
		        for(int i = 0; i < hits.length; i++) {
		            final int 		docId = hits[i].doc;
		            final Document	d = searcher.doc(docId);
		            
		            gla.append(Long.valueOf(d.get("bl_Id")));
//		            System.out.println((i + 1) + ". " + d.get("bl_Id") + "\t" + d.get("title"));
		        }
		        return gla.extract();
	        }
		} catch (ParseException e) {
			throw new SyntaxException(0, 0, e.getLocalizedMessage(), e);
		}
	}
}
