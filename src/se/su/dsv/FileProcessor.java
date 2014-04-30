package se.su.dsv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class FileProcessor implements Processor {

	private Scanner sc;
	private File file;

	public FileProcessor(File file) throws FileNotFoundException {
		this.sc = new Scanner(file);
		this.file = file;
	}

	@Override
	public String nextLine() throws IOException {
		String next = sc.nextLine();
		if (next.trim().startsWith("!")) {
			System.out.println(next);
			next = next.trim().substring(1);
		}
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

	@Override
	public void print(String out) {
		System.out.print(out);
	}

	@Override
	public String getWorkingDir() {
		return file.getParent();
	}
}
