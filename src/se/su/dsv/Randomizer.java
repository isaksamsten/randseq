package se.su.dsv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import jline.TerminalFactory;
import jline.console.ConsoleReader;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.AlgoCM_ClaSP;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.creators.AbstractionCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.creators.AbstractionCreator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.database.SequenceDatabase;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.idlists.creators.IdListCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.idlists.creators.IdListCreatorStandard_Map;
import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoCMSPAM;

public class Randomizer {

	private static List<Sequence> sequences = null;
	private static List<ResultSequence> resultSequences = null;
	private static int lineNo = 1;

	public static void main(String[] args) throws Exception {
		Processor sc = !TerminalFactory.get().isSupported() ? new ConsoleProcessor(
				"") : new StandardProcessor();
		if (args.length > 0) {
			eval(sc, args);
		}
		stdin(sc);
	}

	private static boolean eval(Processor sc2, String[] args) {
		try {
			FileProcessor sc = new FileProcessor(new File(args[0]));
			if (!process(sc, true))
				return false;
		} catch (FileNotFoundException e) {
			out(sc2, e.getMessage());
			return false;
		} catch (Exception e) {
			out(sc2, e.getMessage());
			return false;
		}
		return true;
	}

	private static void stdin(Processor sc) {
		process(sc);
	}

	private static void process(Processor sc) {
		process(sc, false);
	}

	private static boolean process(Processor sc, boolean eval) {
		String line = null;
		try {
			while (sc.hasNextLine() && (line = sc.nextLine()) != null) {
				String[] cmd = line.trim().split("\\s+");
				if (!processCommand(sc, cmd) && eval)
					return false;
				lineNo++;
			}
		} catch (IOException e) {
			out(sc, e.getMessage());
			return false;
		}
		return true;
	}

	private static boolean processCommand(Processor sc, String[] cmd) {
		if (cmd.length == 0) {
			out(sc, "invalid command");
			return false;
		}
		String name = cmd[0];
		String[] args = Arrays.copyOfRange(cmd, 1, cmd.length);
		if (name.equalsIgnoreCase("help")) {
			printHelp();
			return true;
		} else if (name.equalsIgnoreCase("load")) {
			return load(sc, args);
		} else if (name.equalsIgnoreCase("ls")) {
			return ls(sc, args);
		} else if (name.equalsIgnoreCase("run")) {
			return run(sc, args);
		} else if (name.equalsIgnoreCase("frequent")) {
			return frequent(sc, args);
		} else if (name.equalsIgnoreCase("pvalue")) {
			return pvalue(sc, args);
		} else if (name.equalsIgnoreCase("pvalue_all")) {
			return pvalueAll(sc, args);
		} else if (name.equalsIgnoreCase("permute")) {
			return permute(sc, args);
		} else if (name.equalsIgnoreCase("eval")) {
			return eval(sc, args);
		} else if (name.equalsIgnoreCase("exit")) {
			System.exit(1);
			return false;
		} else {
			out(sc, "invalid command");
			return false;
		}

	}

	private static boolean permute(Processor sc, String[] args) {
		if (args.length != 1) {
			sc.println("need sequence to permute");
			return false;
		}
		int id = Integer.parseInt(args[0]);
		if (id > resultSequences.size() - 1 || id < 0) {
			out(sc, "'permute' out of bounds");
			return false;
		}
		ResultSequence pivot = resultSequences.get(id);
		List<Sequence> permutations = pivot.permute(100);
		List<String> labels = new ArrayList<String>();
		labels.add(pivot.prettyPrint());
		List<Double> values = new ArrayList<Double>();
		values.add(pivot.getFrequency());
		for (Sequence s : permutations) {
			Result res = Sequence.search(sequences, s);
			labels.add(s.prettyPrint());
			values.add(res.getFrequency());
		}
		Plot p = new BoxPlot(sc, 100);
		p.plot(labels, values);
		return true;
	}

	private static boolean pvalueAll(Processor sc, String[] args) {
		sc.println("p-value   Sequence.");
		int iter = 100;
		for (ResultSequence toPValue : resultSequences) {
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
			sc.println(String.format("%.2f", (losses / iter)) + "      "
					+ toPValue.prettyPrint());
		}

		return true;
	}

	private static boolean pvalue(Processor sc, String[] args) {
		if (args.length == 2) { // with out static
			int id = Integer.parseInt(args[0]), iter = Integer
					.parseInt(args[1]);
			if (id > resultSequences.size() - 1 || id < 0) {
				out(sc, "'for' out of bounds");
				return false;
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
			out(sc, toPValue.prettyPrint() + " : " + losses / iter);
		} else if (args.length == 3) { // with static
			int id = Integer.parseInt(args[0]), iter = Integer
					.parseInt(args[1]), staticId = Integer.parseInt(args[2]);
			if (id > resultSequences.size() - 1 || id < 0) {
				out(sc, "'for' out of bounds");
				return false;
			}
			if (staticId > resultSequences.size() - 1 || staticId == id) {
				out(sc, "can't keep " + staticId
						+ " static static == non-static");
				return false;
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
			out(sc, toPValue.prettyPrint() + " : " + losses / iter);
		} else {
			out(sc, "'pvalue' expects at least two argument");
			return false;
		}

		return true;
	}

	private static boolean frequent(Processor sc, String[] args) {
		if (resultSequences == null) {
			out(sc, "'frequent' expects an algorithm to have run");
			return false;
		}
		out(sc, "Id\tFreq.\tSequence");
		int id = 0;
		for (ResultSequence r : resultSequences) {
			out(sc, id++ + "\t" + r.getFrequency() + "\t" + r.prettyPrint());
		}
		return true;
	}

	private static boolean run(Processor sc, String[] args) {
		if (args.length < 2) {
			out(sc, "'run' expects two arguments");
			return false;
		}
		if (sequences == null) {
			out(sc, "'run' expects a file to be loaded");
			return false;
		}
		double minSup = 0;
		try {
			minSup = Double.parseDouble(args[1]);
		} catch (NumberFormatException e1) {
			out(sc, "invalid minsup");
			return false;
		}
		try {
			Sequence.writeToFile(sequences, "/tmp/randomizer");
			if (args[0].equalsIgnoreCase("clasp")) {
				AbstractionCreator abstractionCreator = AbstractionCreator_Qualitative
						.getInstance();
				IdListCreator idListCreator = IdListCreatorStandard_Map
						.getInstance();
				SequenceDatabase sd = new SequenceDatabase(abstractionCreator,
						idListCreator);
				double relativeMinSup = sd.loadFile("/tmp/randomizer", minSup);
				AlgoCM_ClaSP spam = new AlgoCM_ClaSP(relativeMinSup,
						abstractionCreator, true, true);
				spam.runAlgorithm(sd, true, false, "/tmp/randomizer_output");
				spam.printStatistics();
			} else if (args[0].equalsIgnoreCase("spam")) {
				AlgoCMSPAM spam = new AlgoCMSPAM();
				spam.runAlgorithm("/tmp/randomizer", "/tmp/randomizer_output",
						minSup);
			} else {
				out(sc, "'run' unkown algorithm: " + args[0]);
			}

			resultSequences = Sequence
					.loadFromResultFile("/tmp/randomizer_output");
		} catch (IOException e) {
			out(sc, e.getMessage());
			return false;
		}
		return true;
	}

	private static boolean ls(Processor sc, String[] args) {
		if (sequences == null) {
			out(sc, "'ls' expects a file to be loaded");
			return false;
		}
		int maxSequences = 100, currentSequence = 0;
		if (args.length > 0) {
			try {
				maxSequences = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
			}
		}

		out(sc, "Id\tSequence");
		for (Sequence s : sequences) {
			if (currentSequence++ > maxSequences) {
				break;
			}
			out(sc, currentSequence - 1 + "\t" + s.prettyPrint());
		}
		return true;
	}

	private static boolean load(Processor sc, String[] args) {
		if (args.length == 0) {
			out(sc, "'load' expects file argument");
			return false;
		}
		try {
			sequences = Sequence.loadFromFile(new File(sc.getWorkingDir(),
					args[0]));
		} catch (IOException e) {
			out(sc, e.getMessage());
			return false;
		}
		return true;
	}

	private static void out(Processor out, Object str) {
		out.println(str.toString());
	}

	private static void printHelp() {
		System.out.println("Commands:");
		System.out.println(" help: show this");
		System.out.println(" eval: evaluate file with commands");
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
