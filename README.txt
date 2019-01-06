To build index:

$ classpath="D:/myJavaWorkspace/"
$ java -cp "${classpath}args4j-2.33.jar;${classpath}jsoup-1.11.3/jsoup-1.11.3.jar;${classpath}lucene-6.6.0/core/lucene-core-6.6.0.jar;${classpath};bin;." Indexer -i INDEX_PATH -d HTML_PATH

(Change classpath accordingly for external dependencies.)

To search index:

$ java -cp "${classpath}args4j-2.33.jar;${classpath}jsoup-1.11.3/jsoup-1.11.3.jar;${classpath}lucene-6.6.0/core/lucene-core-6.6.0.jar;${classpath}lucene-6.6.0/queryparser/lucene-queryparser-6.6.0.jar;${classpath};bin;." Retriever -i INDEX_PATH -q QUERY_STRING

(Change classpath accordingly for external dependencies. Note multiple-word query string needs to be quoted.)

Use -h (-help) to print usage.