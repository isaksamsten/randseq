package se.su.dsv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Sequence {

	public static List<Sequence> loadFromFile(String inputFile) {
		List<Sequence> sequences = new LinkedList<Sequence>();
		BufferedReader reader = null;
		try {
			FileInputStream fin = new FileInputStream(new File(inputFile));
			reader = new BufferedReader(new InputStreamReader(fin));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sequences.add(new Sequence(line));
			}
		} catch (Exception e) {

		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		return sequences;
	}

	public static void writeToFile(List<Sequence> sequences, String outputFile)
			throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
		for (Sequence sequence : sequences) {
			out.write(sequence.toString() + "\n");
		}
		out.close();
	}

	private static Random RANDOM = new Random();
	private ArrayList<String> itemsets = new ArrayList<String>();

	/**
	 * Build a sequences (non-lazy) and not fault tolerant. That is, the
	 * constructor assumes a correct sequence.
	 * 
	 * @param line
	 */
	public Sequence(String line) {
		StringBuilder itemset = new StringBuilder();
		for (String token : line.split(" ")) {
			if (token.equals("-2")) {
				// EOF sequence
				break;
			} else if (token.equals("-1")) {
				// EOF itemset
				this.itemsets.add(itemset.toString());
				itemset = new StringBuilder();
			} else {
				itemset.append(token);
				itemset.append(' ');
			}
		}
	}

	private Sequence(ArrayList<String> copy) {
		this.itemsets = copy;
	}

	public Sequence randomize() {
		@SuppressWarnings("unchecked")
		ArrayList<String> copy = (ArrayList<String>) itemsets.clone();
		Collections.shuffle(copy);
		return new Sequence(copy);
	}

	public Sequence randomize(Sequence keepStatic) {
		int firstIndex = indexOf(keepStatic);
		if (firstIndex == -1)
			return randomize();
		int lastIndex = firstIndex + keepStatic.length();

		ArrayList<String> randomItemsets = new ArrayList<String>();
		for (int n = 0; n < itemsets.size(); n++) {
			if (n >= firstIndex && n < lastIndex)
				continue;
			randomItemsets.add(itemsets.get(n));
		}
		Collections.shuffle(randomItemsets);
		randomItemsets.addAll(RANDOM.nextInt(randomItemsets.size()),
				keepStatic.itemsets);

		return new Sequence(randomItemsets);
	}

	public Sequence subSequence(int start, int end) {
		if (start > end || end > itemsets.size() || start < 0) {
			return null;
		}

		ArrayList<String> subSequence = new ArrayList<String>();
		for (int n = 0; n < itemsets.size(); n++) {
			if (n >= start && n < end) {
				subSequence.add(itemsets.get(n));
			}
			if (n >= end) {
				break;
			}
		}
		return new Sequence(subSequence);
	}

	/**
	 * Return the first indexOf the other sequence. Returns -1 if the sequences
	 * ain't found.
	 * 
	 * @param other
	 * @return
	 */
	public int indexOf(Sequence other) {
		int otherIndex = 0;
		int indexOf = -1;
		int otherLength = other.length();
		for (int n = 0; n < itemsets.size(); n++) {
			if (itemsets.get(n).equals(other.itemsets.get(otherIndex))) {
				otherIndex += 1;
				if (indexOf == -1)
					indexOf = n;
			} else {
				otherIndex = 0;
				indexOf = -1;
			}

			if (otherIndex == otherLength)
				return indexOf;
		}
		if (otherIndex == otherLength)
			return indexOf;
		else
			return -1;
	}

	public String itemset(int index) {
		return itemsets.get(index);
	}

	public int length() {
		return itemsets.size();
	}

	@Override
	public String toString() {
		StringBuilder sequence = new StringBuilder();
		for (String item : itemsets) {
			sequence.append(item);
			sequence.append("-1 ");
		}
		sequence.append("-2");
		return sequence.toString();
	}

	public String prettyPrint() {
		StringBuilder sequence = new StringBuilder();
		for (String item : itemsets) {
			sequence.append(item);
			sequence.append("| ");
		}
		return sequence.toString();
	}
}
