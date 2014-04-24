package se.su.dsv;

import java.io.IOException;

public interface Processor {

	String nextLine() throws IOException;

	boolean hasNextLine() throws IOException;

	void println(String out);

}
