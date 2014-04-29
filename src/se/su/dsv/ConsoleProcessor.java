package se.su.dsv;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.completer.CandidateListCompletionHandler;
import jline.console.completer.CompletionHandler;
import jline.console.completer.StringsCompleter;

public class ConsoleProcessor implements Processor {

	private ConsoleReader sc;

	public ConsoleProcessor(String prompt) throws Exception {
		this.sc = new ConsoleReader();
		this.sc.addCompleter(new StringsCompleter(Arrays.asList(new String[] {
				"run", "frequent", "pvalue", "pvalue_all", "exit", "eval",
				"help", "ls" })));
		this.sc.setPrompt("In  : ");
	}

	@Override
	public String nextLine() throws IOException {
		String next = sc.readLine();
		return next;
	}

	@Override
	public boolean hasNextLine() throws IOException {
		return true;
	}

	@Override
	public void println(String out) {
		try {
			sc.println(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void print(String out) {
		try {
			sc.print(out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getWorkingDir() {
		return ".";
	}

}
