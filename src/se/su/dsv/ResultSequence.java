package se.su.dsv;

public class ResultSequence extends Sequence {

	private double result;

	public ResultSequence(String line, int result) {
		super(line);
		this.setFrequency(result);
	}

	public double getFrequency() {
		return result;
	}

	protected void setFrequency(double result) {
		this.result = result;
	}

}
