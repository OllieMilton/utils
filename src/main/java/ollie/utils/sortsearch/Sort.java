package ollie.utils.sortsearch;

public class Sort {

	public static <T extends Comparable<T>> void mergeSort(T[] arr) {
		Object[] workspace = new Object[arr.length];
		recurseMerge(workspace, arr, 0, arr.length -1);
	}
	
	private static <T extends Comparable<T>> void recurseMerge(Object[] workspace, T[] toSort, int lower, int upper) {
		if (lower == upper) {
			// we have got the base condition of a single element which is considered sorted
			return;
		} else {
			// find the mid point in this section of the array
			int midPoint = (upper+lower) / 2;
			// pointer to the bottom of the upper section of the array
			int upPtr = midPoint+1;
			// recursively sort the lower section
			recurseMerge(workspace, toSort, lower, midPoint);
			// and now the upper section
			recurseMerge(workspace, toSort, upPtr, upper);
			// now merge the array elements
			merge(workspace, toSort, lower, upPtr, upper);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Comparable<T>> void merge(Object[] workspace, T[] toSort, int lower, int upPtr, int upper) {
		int wsPtr = 0;
		int lowPtr = lower;
		int mid = upPtr -1;
		int size = upper-lower+1;
		
		// while the pointer to the lower section of the array is lees than or equal to the middle
		// and the point to the upper section is less than or equal to the upper bound
		// order the in into the work space
		while (lowPtr <= mid && upPtr <= upper) {
			if (toSort[lowPtr].compareTo(toSort[upPtr]) < 0) {
				workspace[wsPtr++] = toSort[lowPtr++];
			} else {
				workspace[wsPtr++] = toSort[upPtr++];
			}
		}
		
		// now handle any cases the size of the array length is not an even number
		while (lowPtr <= mid) {
			workspace[wsPtr++] = toSort[lowPtr++];
		}
		
		while (upPtr <= upper) {
			workspace[wsPtr++] = toSort[upPtr++];
		}
		// now copy the work space back into the array
		for (wsPtr = 0; wsPtr < size; wsPtr++ ) {
			toSort[lower+wsPtr] = (T) workspace[wsPtr];
		}
	}
}
