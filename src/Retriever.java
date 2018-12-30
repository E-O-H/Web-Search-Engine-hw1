import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * <p>An index search query Retriever.</p>
 * 
 * <p>Searches the query terms in an on-disk index 
 * and output to stdout the search results in HTML format.</p>
 * 
 * <p>Usage:<br>
 * Provide arguments for the entry point 
 * "-index INDEX_PATH -query QUERY_STRING" 
 * (options can be abbreviated)</p>
 * 
 * @author Chenyang Tang
 *
 */
public class Retriever {

  /*
   * command-line arguments for the entry point
   */
  @Option(name = "-index", aliases = "-i", required = true, 
          usage = "Path to the directory of the index files to be searched. Required option.")
  private File indexDir;

  @Option(name = "-query", aliases = "-q", required = true, 
          usage = "The search query terms string. Required option.")
  private String queryString;

  @Option(name = "-help", aliases = "-h", required = false, 
          usage = "Print help text.")
  private boolean printHelp = false;
  
  /*
   * Lucene retriever internal objects
   */
  private StandardAnalyzer analyzer; // analyzer for tokenizing text
  private Directory index;           // the index
  private IndexReader reader;        // reader object
  private IndexSearcher searcher;    // searcher object
  private TopDocs docs;              // top docs
  
  private final int hitsPerPage = 10; // Max number of search results to output
  
  private void initialize() {
    try {
      // Open a File-System-Index-Directory for use 
      // (i.e. an index on disk, as opposed to one in memory).
      // FSDirectory.open() chooses the best FSDirectory implementation automatically 
      // given the environment and the known limitations of each implementation.
      index = FSDirectory.open(indexDir.toPath());
    } catch (IOException e) {
      System.err.println("Error opening index directory" + indexDir);
      e.printStackTrace();
    }
    analyzer = new StandardAnalyzer();
  }
  
  /**
   * Search the query string.
   */
  private int search() {
    initialize();
    
    // Build the Query object.
    // (The "text" arg specifies the default field to use
    // when no field is explicitly specified in the query.
    // The analyzer must be the same one used when building the index.)
    Query query;
    try {
      query = new QueryParser("text", analyzer).parse(queryString);
    } catch (ParseException e) {
      System.err.println("Error parsing the query string: \"" + queryString + "\"");
      e.printStackTrace();
      return 1;
    }

    // Search
    try {
      reader = DirectoryReader.open(index);
      searcher = new IndexSearcher(reader);
      docs = searcher.search(query, hitsPerPage);
      
      outputResults();
    } catch (IOException e) {
      System.err.println("Error opening index.");
      e.printStackTrace();
      return 2;
    }
    
    return 0;
  }
  
  /**
   * Output results
   * @throws IOException 
   */
  private void outputResults() throws IOException {
    if (searcher.getIndexReader().maxDoc() == 0) {
      System.err.println("No document in the index!");
      return;
    }
    Path htmlDirName = Paths.get(searcher.doc(0).get("path")).getParent().getFileName();
    
    System.out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"");
    System.out.println("\"http://www.w3.org/TR/html4/loose.dtd\">");
    System.out.println("<html>");
    System.out.println("<head>");
    System.out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
    System.out.println("<title>Results for query " + queryString 
                       + "in directory " + htmlDirName + "</title>");
    System.out.println("<h1>Results for query <u>" + queryString + "</u> in directory <u>"
                       + htmlDirName + "</u></h1>");
    System.out.println("</head>");
    System.out.println("<body>");
    
    ScoreDoc[] hits = docs.scoreDocs;
    for(int i = 0; i < hits.length; ++i) {
        int docId = hits[i].doc;
        Document doc = searcher.doc(docId);
        System.out.println("<p><b><i>" + (i + 1) + "</i>. " + doc.get("title") 
                           + "</b><br><span style='margin-left:3em'>" + doc.get("path") + "</span></p>");
    }
    
    System.out.println("</body>");
    System.out.println("</html>");
  }
  
  
  private int parseArgs(String[] args) {
    final CmdLineParser args4jCmdLineParser = new CmdLineParser(this);
    try {
      args4jCmdLineParser.parseArgument(args);
    } catch (final CmdLineException e) {
      System.err.println(e.getMessage());
      System.err.println("Usage:");
      args4jCmdLineParser.printUsage(System.err);
      return 2;
    }
    
    if (printHelp) {
      System.err.println("Usage:");
      args4jCmdLineParser.printUsage(System.err);
      return 1;
    }
    
    return 0;
  }
  
  /**
   * Retriever Entry point.
   * 
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    final Retriever retriever = new Retriever();
    int status;
    status = retriever.parseArgs(args);
    if (status != 0) System.exit(status);
    status = retriever.search();
    if (status != 0) System.exit(status);
  }
}
