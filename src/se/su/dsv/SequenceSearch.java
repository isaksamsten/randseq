package se.su.dsv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class SequenceSearch {
	private List<String> sequences = new LinkedList<String>();

	public SequenceSearch(String inputFile) throws IOException {
		BufferedReader reader = null;
		try {
			FileInputStream fin = new FileInputStream(new File(inputFile));
			reader = new BufferedReader(new InputStreamReader(fin));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sequences.add(line);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					throw e;
				}
		}
	}

	public double find(String subsequence) {
		return 0;
	}

}
