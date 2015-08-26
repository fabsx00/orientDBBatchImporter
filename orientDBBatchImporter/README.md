Batch Importer for OrientDB
===========================

This is a quick&dirty batch importer for OrientDB, created to import
graph data given in CSV files. Like the neo4j batch inserter by
Michael Hunger (see https://github.com/jexp/batch-import), it accepts
(a) a CSV file specifying the graph's nodes, and (b) a CSV file
specifying its edges. Indexing is currently not yet supported.


Input format
------------

nodes.csv:

	nodeId	key1	key2 ...
	1	val1	val2 ...
	.
	.
	.

edges.csv

	srcId   dstId   edgeType        edgeProp1
	1	2	FOO_EDGE	...
	.
	.
	.

Building
--------

To build the importer, simply install maven and execute

   mvn package

Executing
----------

	java -jar ./target/orientDBBatchImporter-0.0.1-SNAPSHOT.jar
	<nodes.csv> <edges.csv>

The tool will create a new database in /tmp/tempDB .
