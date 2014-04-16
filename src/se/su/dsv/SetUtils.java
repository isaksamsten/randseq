package se.su.dsv;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

public class SetUtils {
	public static <K, V extends Comparable<? super V>> Set<Map.Entry<K, V>> sortMapByValue(
			Map<K, V> map, final boolean asc) {
		Set<Map.Entry<K, V>> sorted = new TreeSet<Map.Entry<K, V>>(
				new Comparator<Map.Entry<K, V>>() {
					@Override
					public int compare(Entry<K, V> o1, Entry<K, V> o2) {
						if (asc) {
							return o2.getValue().compareTo(o1.getValue());
						} else {
							return o1.getValue().compareTo(o2.getValue());
						}
					}
				});
		sorted.addAll(map.entrySet());
		return sorted;
	}
}
