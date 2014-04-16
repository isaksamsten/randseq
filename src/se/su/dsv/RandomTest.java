package se.su.dsv;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoCMSPAM;

public class RandomTest {

	public static void main(String[] args) throws IOException {
		List<Sequence> sequences = Sequence.loadFromFile("data/contextPrefixSpan.txt");

		Sequence.writeToFile(sequences, "data/tmp");
		AlgoCMSPAM spam = new AlgoCMSPAM();
		spam.runAlgorithm("data/tmp", "data/output", 0.2);
		spam.printStatistics();

		List<ResultSequence> result = Sequence
				.loadFromResultFile("data/output");
		int index = 0;
		for (ResultSequence s : result) {
			System.out.println(index
					+ ": "
					+ s.prettyPrint()
					+ " => "
					+ (s.getFrequency() == +Sequence.search(sequences, s)
							.getFrequency()));
			index += 1;
		}
		ResultSequence s = result.get(3);
		System.out.println("===========");
		System.out.println(s.prettyPrint() + " " + s.getFrequency());
		System.out.println("===========");
		Result r = Sequence.search(sequences, s);
		System.out.println(r.getFrequency());

		ResultSequence keepStatic = result.get(7);
		ResultSequence toPValue = result.get(8);
		double losses = 0;
		for (int n = 0; n < 100; n++) {
			List<Sequence> random = new LinkedList<Sequence>();
			for (Sequence seq : sequences) {
				random.add(seq.randomize());
			}
			Result res = Sequence.search(random, toPValue);
			System.out.println(toPValue.getFrequency() + " "
					+ res.getFrequency());
			if (toPValue.getFrequency() <= res.getFrequency()) {
				losses += 1;
			}
		}
		System.out.println("Static sequence: " + keepStatic.prettyPrint());
		System.out.println("Searching for: " + toPValue.prettyPrint());
		System.out.println("p-value: " + losses / 100);

	}
}
