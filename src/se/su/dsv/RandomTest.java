package se.su.dsv;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.AlgoCM_ClaSP;
import ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.database.SequenceDatabase;
import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoCMSPAM;

public class RandomTest {
	/*
	 * for (ResultSequence s : result) { System.out.println(index + ": " +
	 * s.prettyPrint() + " => " + (s.getFrequency() ==
	 * Sequence.search(sequences, s) .getFrequency())); index += 1; }
	 */
	public static void main(String[] args) throws IOException {
		List<Sequence> sequences = Sequence.loadFromFile(new File(
				"data/lev.txt"));
		Sequence.writeToFile(sequences, "data/tmp");

		ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.creators.AbstractionCreator abstractionCreator = ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.creators.AbstractionCreator_Qualitative
				.getInstance();
		ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.idlists.creators.IdListCreator idListCreator = ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.idlists.creators.IdListCreatorStandard_Map
				.getInstance();
		ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.database.SequenceDatabase sd = new ca.pfv.spmf.algorithms.sequentialpatterns.clasp_AGP.dataStructures.database.SequenceDatabase(
				abstractionCreator, idListCreator);

		double relativeMinSup = sd.loadFile("data/tmp", 0.3);

		AlgoCM_ClaSP spam = new AlgoCM_ClaSP(relativeMinSup,
				abstractionCreator, true, true);
		spam.runAlgorithm(sd, true, false, "data/output");
		// spam.runAlgorithm("data/tmp", "data/output", 0.4);
		spam.printStatistics();

		List<ResultSequence> result = Sequence
				.loadFromResultFile("data/output");
		int index = 0;

		for (ResultSequence s : result) {
			System.out.println(index
					+ ": "
					+ s.prettyPrint()
					+ " => "
					+ (s.getFrequency() == Sequence.search(sequences, s)
							.getFrequency()));
			index += 1;
		}

		ResultSequence keepStatic = result.get(8);
		ResultSequence toPValue = result.get(5);
		System.out.println("===========");
		System.out.print(toPValue.prettyPrint() + " " + toPValue.getFrequency()
				+ " == ");
		Result r = Sequence.search(sequences, toPValue);
		System.out.println(r.getFrequency());

		System.out.println("===========");

		double losses = 0, total = 100;
		for (int n = 0; n < total; n++) {
			List<Sequence> random = new LinkedList<Sequence>();
			for (Sequence seq : sequences) {
				random.add(seq.randomize(keepStatic));
			}
			Result res = Sequence.search(random, toPValue);
			if (toPValue.getFrequency() <= res.getFrequency()) {
				losses += 1;
			}
		}
		System.out.println("Static sequence: " + keepStatic.prettyPrint());
		System.out.println("Searching for: " + toPValue.prettyPrint());
		System.out.println("p-value: " + losses / total);

		losses = 0;
		List<Sequence> permutations = toPValue.permute(1000);
		for (Sequence perm : permutations) {
			Result res = Sequence.search(sequences, perm);
			if (toPValue.getFrequency() <= res.getFrequency()) {
				losses += 1;
			}
		}
		System.out.println("Permuted sequence: " + toPValue.prettyPrint());
		System.out.format("p-value: %.2f (%.2f/%d)\n",
				losses / permutations.size(), losses, permutations.size());

	}
}
