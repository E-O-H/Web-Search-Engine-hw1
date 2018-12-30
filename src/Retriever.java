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
 * <p>Retriever class. Search the query terms in an on-disk index 
 * and output to stdout the search results in HTML format.</p>
 * <p>Usage:<br>
 * Provide arguments for the entry point 
 * "-index INDEX_PATH -query QUERY_STRING" </p>
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
    } catch (IOException e) {
      System.err.println("Error opening index.");
      e.printStackTrace();
      return 2;
    }
    
    outputResults();
    return 0;
  }
  
  /**
   * Output results
   */
  private void outputResults() {
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
    
    
    
    <body><p><b><i>1</i>. Web Search Engines: Lecture 3. Indexing and Query Engines</b><br><span style='margin-left:3em'>Prog1ExampleDirectory\Prog1ExampleDirectory\indexing.html</span></p>
    <p><b><i>2</i>. Sentiment Analysis</b><br><span style='margin-left:3em'>Prog1ExampleDirectory\Prog1ExampleDirectory\SentimentAnalysis.html</span></p>
    <p><b><i>3</i>. The Multi-Lingual Web</b><br><span style='margin-left:3em'>Prog1ExampleDirectory\Prog1ExampleDirectory\Multilingual.html</span></p>
    <p><b><i>4</i>. Lecture 8: Invisible Web; Tables; Sentiment Analysis</b><br><span style='margin-left:3em'>Prog1ExampleDirectory\Prog1ExampleDirectory\lec8.html</span></p>
    <p><b><i>5</i>. Media: Images and Music</b><br><span style='margin-left:3em'>Prog1ExampleDirectory\Prog1ExampleDirectory\Media.html</span></p>
    <p><b><i>6</i>. Lecture 7: Clustering Algorithms</b><br><span style='margin-left:3em'>Prog1ExampleDirectory\Prog1ExampleDirectory\lecCluster2.html</span></p>
    <p><b><i>7</i>. Searching for Software</b><br><span style='margin-left:3em'>Prog1ExampleDirectory\Prog1ExampleDirectory\Software.html</span></p>
    <p><b><i>8</i>. Web Structure and Evolution</b><br><span style='margin-left:3em'>Prog1ExampleDirectory\Prog1ExampleDirectory\Webology.html</span></p>

    </body>
    </html>
    
    ScoreDoc[] hits = docs.scoreDocs;
    System.out.println("Found " + hits.length + " hits.");
    for(int i=0;i<htis.length;++i) {
        int docId = hits[i].doc;
        Document d = searcher.doc(docId);
        System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
    }

    // reader can only be closed when there
    // is no need to access the documents any more.
    reader.close();
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
  
  public static void main(String[] args) {
    final Retriever retriever = new Retriever();
    int status;
    status = retriever.parseArgs(args);
    if (status != 0) System.exit(status);
  }
}
