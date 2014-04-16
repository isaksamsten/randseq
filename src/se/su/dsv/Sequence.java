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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

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

	public static List<ResultSequence> loadFromResultFile(String outputFile) {
		List<ResultSequence> temp = new LinkedList<ResultSequence>();
		BufferedReader reader = null;
		try {
			FileInputStream fin = new FileInputStream(new File(outputFile));
			reader = new BufferedReader(new InputStreamReader(fin));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] split = line.split("SUP:");
				temp.add(new ResultSequence(split[0].trim(), Integer
						.parseInt(split[1].trim())));
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

	public static void writeToFile(List<Sequence> sequences, String outputFile)
			throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
		for (Sequence sequence : sequences) {
			out.write(sequence.toString() + "\n");
		}
		out.close();
	}

	public static Result search(List<Sequence> haystack, Sequence needle) {
		double frequency = 0.0, total = 0.0;
		for (Sequence s : haystack) {
			if (s.contains(needle)) {
				frequency += 1;
			}
			total += 1;
		}

		return new Result(frequency, total);
	}

	private static Random RANDOM = new Random();

	// TODO: itemsets must be refactored to an ItemSet class which basically
	// consists of a set
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

	/**
	 * Replaces subsequence a with b if exists. Otherwise, does nothing.
	 * 
	 * @param a
	 * @param b
	 * @return a new {@link Sequence} or this
	 */
	public Sequence replaceConsecutive(Sequence a, Sequence b) {
		int index = indexOfConsecutive(a);
		if (index == -1) {
			return this;
		}

		ArrayList<String> replaced = new ArrayList<String>();
		for (int n = 0; n < itemsets.size(); n++) {
			if (n == index) {
				replaced.addAll(n, b.itemsets);
				n += b.itemsets.size() - 1;
			} else {
				replaced.add(itemsets.get(n));
			}
		}

		return new Sequence(replaced);
	}

	/**
	 * Shuffle this sequence
	 * 
	 * @return
	 */
	public Sequence randomize() {
		@SuppressWarnings("unchecked")
		ArrayList<String> copy = (ArrayList<String>) itemsets.clone();
		Collections.shuffle(copy);
		return new Sequence(copy);
	}

	public Sequence randomize(Sequence keepStatic) {
		Set<Integer> indexes = indexes(keepStatic);
		if (indexes == null) {
			return randomize();
		}
		ArrayList<String> random = new ArrayList<String>();
		for (int n = 0; n < itemsets.size(); n++) {
			if (!indexes.contains(n)) {
				random.add(itemsets.get(n));
			}
		}
		Collections.shuffle(random);
		for (Integer i : indexes) {
			random.add(i, itemsets.get(i));
		}
		return new Sequence(random);
	}

	public Sequence replace(Sequence a, Sequence b) {
		Set<Integer> indexes = indexes(a);
		if (indexes == null || a.length() != b.length()) {
			return this;
		}
		int otherIndex = 0;
		ArrayList<String> replaced = new ArrayList<String>();
		for (int n = 0; n < itemsets.size(); n++) {
			if (indexes.contains(n)) {
				replaced.add(b.itemsets.get(otherIndex));
				otherIndex += 1;
			} else {
				replaced.add(itemsets.get(n));
			}
		}
		return new Sequence(replaced);
	}

	public List<Sequence> randomizeConsecutive(int maxPerm) {
		int n = length();
		long approximateFactorial = Math.round(Math.sqrt(2 * Math.PI * n)
				* Math.pow(n / Math.E, n));
		if (approximateFactorial < maxPerm) {
			return permutations();
		} else {
			ArrayList<Sequence> sequences = new ArrayList<Sequence>();
			for (int n1 = 0; n1 < maxPerm; n1++) {
				sequences.add(randomize());
			}
			return sequences;
		}
	}

	/**
	 * Returns the permutations of the sequence. Although a quite efficent
	 * implementation using the QuickPerm algorithm is used, it's not
	 * recommended for sequences longer than perhaps 8 itemsets (8! = 40320
	 * permutations). Instead use, randomize(int) which gives at maximum n
	 * "permutations".
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Sequence> permutations() {
		ArrayList<String> a = (ArrayList<String>) itemsets.clone();
		int n = a.size();
		int[] p = new int[n]; // Index control array initially all zeros
		int i = 1;
		ArrayList<Sequence> randomSequences = new ArrayList<Sequence>();
		while (i < n) {
			if (p[i] < i) {
				int j = ((i % 2) == 0) ? 0 : p[i];
				Collections.swap(a, i, j);
				randomSequences
						.add(new Sequence((ArrayList<String>) a.clone()));
				p[i]++;
				i = 1;
			} else {
				p[i] = 0;
				i++;
			}
		}

		return randomSequences;
	}

	public Sequence randomizeConsecutive(Sequence keepStatic) {
		int firstIndex = indexOfConsecutive(keepStatic);
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

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Sequence) {
			if (this == obj)
				return true;
			return toString().equals(obj.toString());
		} else {
			return false;
		}
	}

	/**
	 * Return the first indexOf the other sequence. Returns -1 if the sequences
	 * ain't found.
	 * 
	 * @param other
	 * @return
	 */
	public int indexOfConsecutive(Sequence other) {
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

	/**
	 * Return the indexes of the individual itemsets of other in this
	 * 
	 * @param other
	 * @return
	 */
	public Set<Integer> indexes(Sequence other) {
		Set<Integer> indexes = new TreeSet<Integer>();
		int otherIndex = 0, otherLength = other.length();
		for (int n = 0; n < itemsets.size(); n++) {
			if (itemsets.get(n).contains(other.itemsets.get(otherIndex))) {
				otherIndex += 1;
				indexes.add(n);
			}
			if (otherIndex == otherLength)
				return indexes;
		}
		return otherIndex == otherLength ? indexes : null;

	}

	public boolean contains(Sequence other) {
		int otherIndex = 0, otherLength = other.length();
		if (otherLength > length()) {
			return false;
		}
		for (int n = 0; n < itemsets.size(); n++) {
			if (itemsets.get(n).contains(other.itemsets.get(otherIndex))) {
				otherIndex += 1;
			}
			if (otherIndex == otherLength)
				break;
		}
		return otherIndex == otherLength;
	}

	public boolean containsConsecutive(Sequence other) {
		return indexOfConsecutive(other) != -1;
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
			sequence.append(", ");
		}
		sequence.delete(sequence.length() - 2, sequence.length());
		return sequence.toString();
	}
}
