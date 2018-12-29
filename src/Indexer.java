import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;


public class Indexer {
  
  /**
   * command-line arguments for entry point
   */
  @Option(name = "-index", aliases = "-i", required = true, 
          usage = "Path to the directory to save index files (aka. output). Required option.")
  private File indexDir;

  @Option(name = "-docs", aliases = "-d", required = true, 
          usage = "Path to the directory containing html files to search (aka. input). Required option.")
  private File htmlDir;

  @Option(name = "-help", aliases = "-h", required = false, 
          usage = "Print help text.")
  private boolean printHelp = false;
  
  /**
   * Lucene indexer internal objects
   */
  private StandardAnalyzer analyzer; // analyzer for tokenizing text
  private Directory index;           // the index
  
  private void initialize() {
    try {
      // Make a File-System-Index-Directory (i.e. an index on disk, as opposed to one in memory).
      // FSDirectory.open() chooses the best FSDirectory implementation given the environment, 
      // and the known limitations of each implementation.
      index = FSDirectory.open(indexDir.toPath());
    } catch (IOException e) {
      System.err.println("Error opening index directory" + indexDir);
      e.printStackTrace();
    }
    analyzer = new StandardAnalyzer();
    
  }
  
  private static void addDoc(IndexWriter indexWriter, String title, String text, String path) throws IOException {
    Document doc = new Document();
    doc.add(new TextField("title", title, Field.Store.YES));
    doc.add(new TextField("text", text, Field.Store.YES));
    
    // use a string field for path because we don't want it tokenized
    doc.add(new StringField("path", path, Field.Store.YES));
    
    indexWriter.addDocument(doc);
}
  /**
   * read a html file, extract its text and add to the indexer
   * @param path the html file
   */
  private void processHtmlFile(Path path) {
    String rawHtml;
    IndexWriterConfig config = new IndexWriterConfig(analyzer);
    try (IndexWriter indexWriter = new IndexWriter(index, config)) {
      rawHtml = new String(Files.readAllBytes(path), "UTF-8");      // read html file
      String tidyHtml = Jsoup.clean(rawHtml, Whitelist.relaxed());  // tidy up the html
      org.jsoup.nodes.Document dom = Jsoup.parse(tidyHtml);         // parse html into DOM
      String title = dom.title();
      String text = dom.text();
      addDoc(indexWriter, title, text, path.toString());
    } catch (IOException e) {
      System.err.println("Error opening html file" + path);
      e.printStackTrace();
      return;
    }
  }
  
  private int makeIndex() {
    initialize();
    // process every file under the path htmlDir
    try (Stream<Path> paths = Files.walk(htmlDir.toPath())) {
        paths.filter(Files::isRegularFile)
             .forEach(this::processHtmlFile);
    } catch (IOException e) {
      System.err.println("Error opening html directory" + htmlDir);
      e.printStackTrace();
      return 1;
    }
    return 0;
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
    final Indexer indexer = new Indexer();
    int status;
    status = indexer.parseArgs(args);
    if (status != 0) System.exit(status);
    status = indexer.makeIndex();
    if (status != 0) System.exit(status);
  }
}
