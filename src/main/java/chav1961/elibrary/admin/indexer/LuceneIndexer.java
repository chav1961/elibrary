package chav1961.elibrary.admin.indexer;


import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchResult;

import org.apache.lucene.analysis.TokenStream;
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
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;

import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.growablearrays.GrowableLongArray;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacadeOwner;
import chav1961.purelib.basic.interfaces.ProgressIndicator;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.LocalizerOwner;

public class LuceneIndexer implements LoggerFacadeOwner, LocalizerOwner, Closeable {
	public static final String	LUCENE_DEFAULT_INDEXING_DIR = "./lucene";
	
	private static final String	KEY_START_INDEXING = "LuceneIndexer.startIndexing";
	
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

				try(final ResultSet	rs = stmt.executeQuery("select count(*) from \"elibrary\".\"booklist\"")) {
					if (rs.next()) {
						count = rs.getInt(1);
					}
					else {
						count = 0;
					}
				}
				
				int	current = 0;
				
				pi.start(localizer.getValue(KEY_START_INDEXING), count);
				try(final ResultSet		rs = stmt.executeQuery("select * from \"elibrary\".\"booklist\""); 
					final IndexWriter	wr = new IndexWriter(index, config)) {
					
					while (rs.next()) {
						addDoc(wr, rs);
						pi.processed(current++);
					}
				} finally {
					pi.end();
				}
			} catch (IOException e) {
				throw new SQLException(e.getLocalizedMessage(), e);
			}
		}
	}
	
	private static void addDoc(final IndexWriter wr, final ResultSet rs) throws IOException, SQLException {
	  final Document 		doc = new Document();
	  final StringBuilder	sb = new StringBuilder();
	  
	  doc.add(new StoredField("bl_Id", rs.getLong("bl_Id")));
	  doc.add(new TextField("bl_Title", rs.getString("bl_Title"), Field.Store.YES));
	  sb.append(rs.getString("bl_Title")).append(' ');
	  doc.add(new TextField("bl_Comment", rs.getString("bl_Comment"), Field.Store.YES));
	  sb.append(rs.getString("bl_Comment")).append(' ');
	  doc.add(new TextField("anywhere", sb.toString(), Field.Store.YES));
	  System.err.println("ADD "+sb.toString());
	  wr.addDocument(doc);
	}

	public SearchResult[] search(final String queryStr, final int hitsPerPage) throws IOException, SyntaxException {
		try(final StandardAnalyzer 	analyzer = new StandardAnalyzer()) {
			final Query 			q = new QueryParser("anywhere", analyzer).parse(queryStr);

	        System.err.println("Search " + queryStr);
			
	        try(final IndexReader 	reader = DirectoryReader.open(index)) {
	            final IndexSearcher searcher = new IndexSearcher(reader);
	            final TopDocs 		docs = searcher.search(q, hitsPerPage);
	    	    final Formatter 	formatter = new SimpleHTMLFormatter();
		        final QueryScorer 	scorer = new QueryScorer(q);
		        final Highlighter 	highlighter = new Highlighter(formatter, scorer);
		        final Fragmenter 	fragmenter = new SimpleSpanFragmenter(scorer, 256);
	            final ScoreDoc[] 	hits = docs.scoreDocs;
	            final List<SearchResult>	result = new ArrayList<>();
	            
		        highlighter.setTextFragmenter(fragmenter);
	            
		        System.err.println("Found " + hits.length + " hits.");
		        for(int i = 0; i < hits.length; i++) {
		            final int 			docId = hits[i].doc;
		            final Document		d = searcher.doc(docId);
		            final String 		text = d.get("anywhere");
		            final TokenStream 	stream = TokenSources.getAnyTokenStream(reader, docId, "anywhere", analyzer);
		            final String[] 		frags = highlighter.getBestFragments(stream, text, 10);
		            
		            System.err.println((i + 1) + ". " + d.get("bl_Id") + "\t" + d.get("title"));
		            result.add(new SearchResult(Long.valueOf(d.get("bl_Id")), hits[i].score, frags.length > 0 ? frags[0] : ""));
		        }
		        return result.toArray(new SearchResult[result.size()]);
	        }
		} catch (ParseException | InvalidTokenOffsetsException e) {
			throw new SyntaxException(0, 0, e.getLocalizedMessage(), e);
		}
	}
	
	public static class SearchResult {
		public final long	docId;
		public final double	score;
		public final String	fragment;
		
		public SearchResult(long docId, double score, String fragment) {
			this.docId = docId;
			this.score = score;
			this.fragment = fragment;
		}

		@Override
		public String toString() {
			return "SearchResult [docId=" + docId + ", score=" + score + ", fragment=" + fragment + "]";
		}
	}
}
