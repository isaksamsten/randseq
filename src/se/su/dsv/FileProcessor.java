package se.su.dsv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class FileProcessor implements Processor {

	private Scanner sc;

	public FileProcessor(File file) throws FileNotFoundException {
		this.sc = new Scanner(file);
	}

	@Override
	public String nextLine() throws IOException {
		String next = sc.nextLine();
		System.out.println("Eval: " + next);
		return next;
	}

	@Override
	public boolean hasNextLine() throws IOException {
		return sc.hasNextLine();
	}

	@Override
	public void println(String out) {
		System.out.println(out);
	}

}
