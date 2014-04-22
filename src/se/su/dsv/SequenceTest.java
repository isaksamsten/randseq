package se.su.dsv;

import java.io.IOException;
import java.util.List;

public class SequenceTest {

	public static void main(String[] args) throws IOException {

		Sequence s1 = new Sequence("1 -1 2 3 -1 2 3 -1 3 -1 5 -1");
		Sequence sub = new Sequence("1 -1 2 3 -1 -2").randomize();
		Sequence sub2 = new Sequence("3 -1 5 -1 -2");
		Sequence sub3 = new Sequence("99 -1 88 -1 -2");

		List<Sequence> ss = Sequence.loadFromFile("data/contextPrefixSpan.txt");
		Sequence c = sub2;
		Sequence a = ss.get(1);
		Sequence b = ss.get(2);

		System.out.println(a.prettyPrint());
		System.out.println(b.prettyPrint());
		System.out.println(a.randomize(c).prettyPrint());

		List<Sequence> lst = sub2.randomizeConsecutive(1000000);
		System.out.println("== " + lst.size() + " permutations + original ==");
		System.out.println(sub2.prettyPrint());
		for (Sequence s : lst) {
			System.out.println(s.prettyPrint());
		}

		System.out.println("== Replace ==");
		Sequence r = new Sequence("6 -1 10 11 -1 -2");
		System.out.println("Replacing " + sub.prettyPrint() + " with "
				+ r.prettyPrint() + " in " + s1.prettyPrint());
		System.out.println("Result: "
				+ s1.replaceConsecutive(sub, r).prettyPrint());

		System.out.println("== other test ==");
		System.out.println(sub2.prettyPrint() + " in " + s1.prettyPrint()
				+ " == " + s1.indexes(sub2));
		System.out.println("Randomizes with static: "
				+ s1.randomize(sub2).prettyPrint());
		System.out.println("Replaces: "
				+ s1.randomize(sub2).replace(sub2, sub3).prettyPrint());
		System.out.println(s1.subSequence(3, 5).prettyPrint());
		System.out.println(s1.indexOfConsecutive(sub));
		System.out.println(sub.prettyPrint());
		System.out.println(s1.prettyPrint());

	}
}