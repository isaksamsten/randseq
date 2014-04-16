package se.su.dsv;

import java.util.List;

public class SequenceTest {

	public static void main(String[] args) {
		Sequence s1 = new Sequence("2 3 -1 1 -1 2 3 -1 3 -1 5 -1 -2");
		Sequence sub = new Sequence("1 -1 2 3 -1 -2").randomize();
		Sequence sub2 = new Sequence("3 -1 5 -1 -2");

		System.out.println(s1.randomize(sub2).prettyPrint());
		System.out.println(s1.subSequence(3, 5).prettyPrint());
		System.out.println(s1.indexOf(sub));
		System.out.println(sub.prettyPrint());
		System.out.println(s1.prettyPrint());
	}
}
