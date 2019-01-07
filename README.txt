To build index:

$ LIBRARY_PATH="lib"
$ java -cp "${LIBRARY_PATH}/args4j-2.33.jar;${LIBRARY_PATH}/jsoup-1.11.3/jsoup-1.11.3.jar;${LIBRARY_PATH}/lucene-6.6.0/core/lucene-core-6.6.0.jar;${LIBRARY_PATH};bin;." Indexer -i INDEX_PATH -d HTML_PATH

(Change LIBRARY_PATH accordingly if you put external dependencies in other locations. Note on Unix-like systems, ":" instead of ";" should be used as delimiter in java classpath.)

To search index:

$ java -cp "${LIBRARY_PATH}/args4j-2.33.jar;${LIBRARY_PATH}/jsoup-1.11.3/jsoup-1.11.3.jar;${LIBRARY_PATH}/lucene-6.6.0/core/lucene-core-6.6.0.jar;${LIBRARY_PATH}/lucene-6.6.0/queryparser/lucene-queryparser-6.6.0.jar;${LIBRARY_PATH};bin;." Retriever -i INDEX_PATH -q QUERY_STRING

(Note multiple-word query string needs to be quoted.)

Use -h (-help) to print usage.
