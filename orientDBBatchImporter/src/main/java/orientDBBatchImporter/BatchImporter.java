package orientDBBatchImporter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.cli.ParseException;

import com.opencsv.CSVReader;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.util.wrappers.batch.BatchGraph;
import com.tinkerpop.blueprints.util.wrappers.batch.VertexIDType;

public class BatchImporter
{

	static CommandLineInterface cmdLine = new CommandLineInterface();
	static OrientGraph tx;
	static BatchGraph<OrientGraph> batchGraph;
	static String[] VertexKeys;
	static String[] EdgeKeys;

	public static void main(String[] args)
	{

		parseCommandLine(args);

		try
		{
			openDatabase();
			processNodeFile();
			processEdgeFile();
			closeDatabase();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private static void openDatabase()
	{
		OrientGraphFactory factory = new OrientGraphFactory(
				"plocal:/tmp/tempDB/", "admin", "admin");
		factory.declareIntent(new OIntentMassiveInsert());

		// batchGraph = factory.getNoTx();
		tx = factory.getTx();
		batchGraph = new BatchGraph<OrientGraph>(tx, VertexIDType.STRING, 1000);
		// batchGraph.wrap()

	}

	private static void closeDatabase()
	{
		tx.commit();
		tx.shutdown();
	}

	private static void parseCommandLine(String[] args)
	{
		try
		{
			cmdLine.parseCommandLine(args);
		} catch (RuntimeException | ParseException e)
		{
			printHelpAndTerminate(e);
		}
	}

	private static void processNodeFile() throws IOException
	{

		CSVReader csvReader = getCSVReaderForFile(cmdLine.getNodeFile());

		processFirstNodeRow(csvReader);

		String[] row;
		while ((row = csvReader.readNext()) != null)
		{
			processNodeRow(row);
		}

	}

	private static void processFirstNodeRow(CSVReader csvReader)
			throws IOException
	{
		String[] row = csvReader.readNext();
		if (row == null)
			throw new RuntimeException("Node file is empty");

		initializeVertexKeys(row);
	}

	private static void initializeVertexKeys(String[] row)
	{
		VertexKeys = new String[row.length];
		for (int i = 0; i < row.length; i++)
		{
			VertexKeys[i] = row[i];
		}
	}

	private static void processNodeRow(String[] row)
	{

		// skip empty lines
		if (row.length < 1)
			return;

		String id = row[0];

		Vertex vertex = batchGraph.addVertex(id);

		for (int i = 0; i < row.length; i++)
		{
			vertex.setProperty(VertexKeys[i], row[i]);
		}
	}

	private static void processEdgeFile() throws IOException
	{
		CSVReader csvReader = getCSVReaderForFile(cmdLine.getEdgeFile());
		processFirstEdgeRow(csvReader);

		String[] row;
		while ((row = csvReader.readNext()) != null)
		{
			processEdgeRow(row);
		}

	}

	private static void processFirstEdgeRow(CSVReader csvReader)
			throws IOException
	{
		String[] row = csvReader.readNext();
		if (row == null)
			throw new RuntimeException("Edge file is empty");

		initializeEdgeKeys(row);
	}

	private static void initializeEdgeKeys(String[] row)
	{
		EdgeKeys = new String[row.length];
		for (int i = 0; i < row.length; i++)
		{
			EdgeKeys[i] = row[i];
		}
	}

	private static void processEdgeRow(String[] row)
	{

		if (row.length < 3)
			return;

		String srcId = row[0];
		String dstId = row[1];
		String label = row[2];

		Vertex outVertex = batchGraph.getVertex(srcId);
		Vertex inVertex = batchGraph.getVertex(dstId);

		System.out.println(outVertex.getId());

		Edge edge = batchGraph.addEdge(new Random().nextInt(), outVertex,
				inVertex, label);

		for (int i = 3; i < row.length; i++)
		{
			edge.setProperty(EdgeKeys[i], row[i]);
		}

	}

	private static CSVReader getCSVReaderForFile(String filename)
			throws FileNotFoundException
	{
		CSVReader reader;
		FileReader fileReader = new FileReader(filename);
		reader = new CSVReader(fileReader, '\t');
		return reader;
	}

	private static void printHelpAndTerminate(Exception e)
	{
		System.err.println(e.getMessage());
		cmdLine.printHelp();
		System.exit(0);
	}

}
