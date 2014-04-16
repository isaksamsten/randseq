package se.su.dsv;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ItemSet {
	private Set<String> items = new HashSet<String>();
	private String toString;

	public boolean contains(ItemSet o) {
		for (String item : o.items)
			if (items.contains(item))
				return true;

		return false;
	}

	public int size() {
		return items.size();
	}

	public ItemSet(String items) {
		toString = items;
		for (String item : items.split(" "))
			this.items.add(item.trim());
	}

	@Override
	public String toString() {
		return toString;
	}
}
