To build index:
$ java -cp "bin;D:/myJavaWorkspace/args4j-2.33.jar;D:/myJavaWorkspace/jsoup-1.11.3/jsoup-1.11.3.jar;D:/myJavaWorkspace/lucene-6.6.0/core/lucene-core-6.6.0.jar;" Indexer -i index -d html_input/Prog1ExampleDirectory

To search index:
$ java -cp "bin;D:/myJavaWorkspace/args4j-2.33.jar;D:/myJavaWorkspace/jsoup-1.11.3/jsoup-1.11.3.jar;D:/myJavaWorkspace/lucene-6.6.0/core/lucene-core-6.6.0.jar;D:/myJavaWorkspace/lucene-6.6.0/queryparser/lucene-queryparser-6.6.0.jar;" Retriever -i index -q QUERY_STRING