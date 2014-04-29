package se.su.dsv;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

public class BoxPlot implements Plot {

	private Processor out;
	private int maxLength;

	public BoxPlot(Processor out, int maxLength) {
		this.out = out;
		this.maxLength = maxLength;
	}

	public BoxPlot(Processor out) {
		this(out, 50);
	}

	@Override
	public void plot(List<String> labels, List<Double> values) {
		if (labels.size() != values.size())
			return;

		int longest = 0;
		double sum = 0;
		for (String label : labels) {
			if (label.length() > longest)
				longest = label.length();
		}
		for (double d : values) {
			sum += d;
		}

		Iterator<String> labelIterator = labels.iterator();
		Iterator<Double> valueIterator = values.iterator();
		while (labelIterator.hasNext() && valueIterator.hasNext()) {
			String label = labelIterator.next();
			double value = Math.ceil((valueIterator.next() / sum) * maxLength);
			out.print(label);
			for (int i = 0; i < longest - label.length(); i++)
				out.print(" ");
			out.print(": ");
			for (int i = 0; i < value; i++) {
				out.print("*");
			}
			out.println("");
		}

	}
}
