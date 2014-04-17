package se.su.dsv;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.AlgoCM_ClaSP;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.creators.AbstractionCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.creators.AbstractionCreator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.database.SequenceDatabase;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.idlists.creators.IdListCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.idlists.creators.IdListCreatorStandard_Map;

public class Randomizer {

	private static List<Sequence> sequences = null;
	private static List<ResultSequence> resultSequences = null;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String line = null;
		System.out.print(">> ");
		load(new String[] { "data/contextPrefixSpan.txt" });
		run(new String[] { "dsadsa", "0.5" });
		while (!(line = sc.nextLine()).equals("exit")) {
			String[] cmd = line.split("\\s+");
			processCommand(cmd);

			System.out.print(">> ");
		}

	}

	private static void processCommand(String[] cmd) {
		if (cmd.length == 0) {
			out("invalid command");
			return;
		}
		String name = cmd[0];
		String[] args = Arrays.copyOfRange(cmd, 1, cmd.length);
		if (name.equalsIgnoreCase("help")) {
			printHelp();
		} else if (name.equalsIgnoreCase("load")) {
			load(args);
		} else if (name.equalsIgnoreCase("ls")) {
			ls(args);
		} else if (name.equalsIgnoreCase("run")) {
			run(args);
		} else if (name.equalsIgnoreCase("frequent")) {
			frequent(args);
		} else if (name.equalsIgnoreCase("pvalue")) {
			pvalue(args);
		} else {

			out("invalid command");
		}

	}

	private static void pvalue(String[] args) {
		if (args.length == 2) { // with out static
			int id = Integer.parseInt(args[0]), iter = Integer
					.parseInt(args[1]);
			if (id > resultSequences.size() - 1) {
				id = resultSequences.size() - 1;
			}
			ResultSequence toPValue = resultSequences.get(id);
			double losses = 0;
			for (int n = 0; n < iter; n++) {
				List<Sequence> random = new LinkedList<Sequence>();
				for (Sequence seq : sequences) {
					random.add(seq.randomize());
				}
				Result res = Sequence.search(random, toPValue);
				if (toPValue.getFrequency() <= res.getFrequency()) {
					losses += 1;
				}
			}
			out("pvalue is " + losses / iter);
		} else if (args.length == 3) { // with static
			int id = Integer.parseInt(args[0]), iter = Integer
					.parseInt(args[1]), staticId = Integer.parseInt(args[2]);
			if (id > resultSequences.size() - 1) {
				id = resultSequences.size() - 1;
			}
			if (staticId > resultSequences.size() - 1 || staticId == id) {
				out("can't keep " + staticId + " static");
				return;
			}
			ResultSequence toPValue = resultSequences.get(id);
			ResultSequence keepStatic = resultSequences.get(staticId);
			double losses = 0;
			for (int n = 0; n < iter; n++) {
				List<Sequence> random = new LinkedList<Sequence>();
				for (Sequence seq : sequences) {
					random.add(seq.randomize(keepStatic));
				}
				Result res = Sequence.search(random, toPValue);
				if (toPValue.getFrequency() <= res.getFrequency()) {
					losses += 1;
				}
			}
			out("pvalue is " + losses / iter);
		} else {
			out("'pvalue' expects at least two argument");
		}
	}

	private static void frequent(String[] args) {
		if (resultSequences == null) {
			out("'frequent' expects an algorithm to have ran");
			return;
		}
		out("Id\tFreq.\tSequence");
		int id = 0;
		for (ResultSequence r : resultSequences) {
			out(id++ + "\t" + r.getFrequency() + "\t" + r.prettyPrint());
		}
	}

	private static void run(String[] args) {
		if (args.length < 2) {
			out("'run' expects two arguments");
			return;
		}
		if (sequences == null) {
			out("'run' expects a file to be loaded");
			return;
		}
		double minSup = 0;
		try {
			minSup = Double.parseDouble(args[1]);
		} catch (NumberFormatException e1) {
			out("invalid minsup");
			return;
		}
		AbstractionCreator abstractionCreator = AbstractionCreator_Qualitative
				.getInstance();
		IdListCreator idListCreator = IdListCreatorStandard_Map.getInstance();
		SequenceDatabase sd = new SequenceDatabase(abstractionCreator,
				idListCreator);

		try {
			Sequence.writeToFile(sequences, "/tmp/randomizer");
			double relativeMinSup = sd.loadFile("/tmp/randomizer", minSup);
			AlgoCM_ClaSP spam = new AlgoCM_ClaSP(relativeMinSup,
					abstractionCreator, true, true);
			spam.runAlgorithm(sd, true, false, "/tmp/randomizer_output");
			spam.printStatistics();

			resultSequences = Sequence.loadFromResultFile("/tmp/randomizer_output");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void ls(String[] args) {
		if (sequences == null) {
			out("'ls' expects a file to be loaded");
			return;
		}
		int maxSequences = 100, currentSequence = 0;
		if (args.length > 0) {
			try {
				maxSequences = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
			}
		}

		out("Id\tSequence");
		for (Sequence s : sequences) {
			if (currentSequence++ > maxSequences) {
				break;
			}
			out(currentSequence - 1 + "\t" + s.prettyPrint());
		}
	}

	private static void load(String[] args) {
		if (args.length == 0) {
			out("'load' expects file argument");
			return;
		}
		try {
			sequences = Sequence.loadFromFile(args[0]);
		} catch (IOException e) {
			out(e.getMessage());
		}
	}

	private static void out(Object str) {
		System.out.println(str);
	}

	private static void printHelp() {
		System.out.println("Commands:");
		System.out.println(" help: show this");
		System.out.println(" load [filename]: load file");
		System.out
				.println(" run (algorithm) (min freq): run algorithm on loaded file");
		System.out.println(" ls: show loaded sequences");
		System.out.println(" frequent: list frequent sequences");
		System.out
				.println(" pvalue (for) (iter=100) [static]: calculate pvalue for 'for' keeping 'static' static'");
		System.out.println(" exit: exit program");

	}
}
