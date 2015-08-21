package orientDBBatchImporter;

import org.apache.commons.cli.ParseException;

public class CommandLineInterface extends CommonCommandLineInterface
{
	String nodeFile;
	String edgeFile;

	public String getNodeFile()
	{
		return nodeFile;
	}

	public String getEdgeFile()
	{
		return edgeFile;
	}

	public void printHelp()
	{
		formater.printHelp("importer <nodes.csv> <edges.csv> ...", options);
	}

	public void parseCommandLine(String[] args) throws ParseException
	{
		if (args.length != 2)
			throw new RuntimeException("Please supply a node and an edge file");

		cmd = parser.parse(options, args);

		String[] arguments = cmd.getArgs();
		nodeFile = arguments[0];
		edgeFile = arguments[1];
	}

}
