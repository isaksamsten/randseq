package se.su.dsv;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoCMSPAM;
import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoSPAM;

public class RandomizeSPAM {
	public static int NO_TRIALS = 100;
	public static double alpha = 0.05;
	public static double minFreq = 0.3;
	public static String INPUT = "data/sign.txt";
	public static String OUTPUT = INPUT + "_output.txt";

	public static void main(String[] args) throws Exception {
		AlgoCMSPAM spam = new AlgoCMSPAM();
		spam.runAlgorithm(INPUT, OUTPUT, minFreq);
		spam.printStatistics();
		int totalNoSequences = countLines(INPUT);

		// <Frequent Pattern, Score>
		HashMap<String, Integer> baseline = parseStatistics(OUTPUT);
		HashMap<String, Integer> wins = new HashMap<String, Integer>();
		for (int n = 0; n < NO_TRIALS; n++) {
			Randomizer rand = new Randomizer(INPUT, "<to-keep-static>");
			rand.randomize();
			rand.writeResult(INPUT + n);

			spam.runAlgorithm(INPUT + n, OUTPUT + n, minFreq);
			spam.printStatistics();
			HashMap<String, Integer> stats = parseStatistics(OUTPUT + n);
			for (Map.Entry<String, Integer> kv : stats.entrySet()) {
				String sequence = kv.getKey();
				if (baseline.containsKey(sequence)) {
					Integer value = wins.get(sequence);
					if (value == null)
						value = 0;

					if (baseline.get(sequence) <= kv.getValue()) {
						value += 1;
					}
					wins.put(kv.getKey(), value);
				}
			}
		}

		System.out.println(" ==== Sorted by Frequency ==== ");
		for (Map.Entry<String, Integer> kv : SetUtils.sortMapByValue(baseline, false)) {
			String sequence = kv.getKey();
			double sup = kv.getValue() / ((float) totalNoSequences);
			double pvalue = 0;
			if (wins.containsKey(sequence)) {
				pvalue = (wins.get(sequence) + 1) / ((float) NO_TRIALS + 1);
			}
			System.out.println(String.format("%s \t %f (pvalue: %f)", sequence,
					sup, pvalue));

		}

		System.out.println("\n ==== Sorted by p-value ==== ");
		for (Map.Entry<String, Integer> kv : SetUtils.sortMapByValue(wins, true)) {
			String sequence = kv.getKey();
			int noWins = kv.getValue();
			double pvalue = (noWins + 1) / ((float) NO_TRIALS + 1);
			double sup = baseline.get(sequence) / ((float) totalNoSequences);
			if (pvalue < alpha) {
				System.out.println(String.format("%s \t %f (sup: %f)",
						sequence, pvalue, sup));
			}
		}

	}

	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}

	public static HashMap<String, Integer> parseStatistics(String outputFile) {
		HashMap<String, Integer> temp = new HashMap<String, Integer>();
		BufferedReader reader = null;
		try {
			FileInputStream fin = new FileInputStream(new File(outputFile));
			reader = new BufferedReader(new InputStreamReader(fin));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] split = line.split("SUP:");
				temp.put(split[0].trim(), Integer.parseInt(split[1].trim()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		return temp;
	}

}
