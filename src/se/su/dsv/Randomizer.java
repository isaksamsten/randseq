package se.su.dsv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Randomizer {

	private String inputFile;
	private String staticSequence;
	private List<String> randomSequence = new LinkedList<String>();

	public Randomizer(String inputFile, String sequence) {
		this.inputFile = inputFile;
		this.staticSequence = sequence;
	}

	public void randomize() {
		randomSequence = new LinkedList<String>();
		BufferedReader reader = null;
		try {
			FileInputStream fin = new FileInputStream(new File(inputFile));
			reader = new BufferedReader(new InputStreamReader(fin));
			String line = null;
			while ((line = reader.readLine()) != null) {
				randomSequence.add(randomizeSequence(line));
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

	}

	private String randomizeSequence(String line) {
		ArrayList<String> itemsets = new ArrayList<String>();
		StringBuilder itemset = new StringBuilder();
		for (String token : line.split(" ")) {
			if (token.equals("-2")) {
				// EOF sequence
				break;
			} else if (token.equals("-1")) {
				// EOF itemset
				itemsets.add(itemset.toString());
				itemset = new StringBuilder();
			} else {
				itemset.append(token);
				itemset.append(' ');
			}

		}
		Collections.shuffle(itemsets);
		StringBuilder sequence = new StringBuilder();
		for (String item : itemsets) {
			sequence.append(item);
			sequence.append("-1 ");
		}
		sequence.append("-2");
		return sequence.toString();
	}

	public void writeResult(String outputFile) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(outputFile));
		for (String sequence : randomSequence) {
			out.write(sequence + "\n");
		}
		out.close();
	}

}
