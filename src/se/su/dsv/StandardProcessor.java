package se.su.dsv;

import java.io.IOException;
import java.util.Scanner;

public class StandardProcessor implements Processor {

	private Scanner scanner;

	public StandardProcessor() {
		this.scanner = new Scanner(System.in);
	}

	@Override
	public String nextLine() throws IOException {
		System.out.print("In  : ");
		return scanner.nextLine();
	}

	@Override
	public boolean hasNextLine() throws IOException {
		return true;
	}

	@Override
	public void println(String out) {
		System.out.println(out);
	}

	@Override
	public void print(String out) {
		System.out.print(out);
	}

	@Override
	public String getWorkingDir() {
		return ".";
	}

}
