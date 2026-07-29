package ollie.utils.sortsearch;

import java.util.Arrays;
import java.util.Comparator;

public class Search {

	public static <T extends Comparable<T>> T binarySearch(T[] arr, Comparable<T> x) {
		if (arr.length > 1) {
			int mid = arr.length / 2;
			int cmp = x.compareTo(arr[mid]);
			if (cmp < 0) {
				return binarySearch(Arrays.copyOfRange(arr, 0, mid), x);
			} else if (cmp > 0) {
				return binarySearch(Arrays.copyOfRange(arr, mid, arr.length), x);
			} else {
				return arr[mid].equals(x) ? arr[mid] : null;
			}
		} else {
			return arr[0].equals(x) ? arr[0] : null;
		}
	}
	
	public static <T> T binarySearch(T[] arr, T x, Comparator<T> comparator) {
		if (arr.length > 1) {
			int mid = arr.length / 2;
			int cmp = comparator.compare(x, arr[mid]);
			if (cmp < 0) {
				return binarySearch(Arrays.copyOfRange(arr, 0, mid), x, comparator);
			} else if (cmp > 0) {
				return binarySearch(Arrays.copyOfRange(arr, mid, arr.length), x, comparator);
			} else {
				return arr[mid].equals(x) ? arr[mid] : null;
			}
		} else {
			return arr[0].equals(x) ? arr[0] : null;
		}
	}
}
