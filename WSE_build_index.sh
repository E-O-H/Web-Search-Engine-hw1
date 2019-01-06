classpath="/home/ct1856/public_html/java-bin"
INDEX_PATH="/home/ct1856/public_html/WSE-hw1-index-files"
HTML_PATH="/home/ct1856/public_html/WSE-hw1-input-html-files/Prog1ExampleDirectory"
java -cp "${classpath}/args4j-2.33.jar:${classpath}/jsoup-1.11.3/jsoup-1.11.3.jar:${classpath}/lucene-6.6.0/core/lucene-core-6.6.0.jar:${classpath}" Indexer -i $INDEX_PATH -d $HTML_PATH
