package ollie.utils.sortsearch;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;



@RunWith(JUnit4.class)
public class SortTest {

	@Test
	public void mergeSort() {
		Integer[] arr = {9,2,5,6,1};
		Sort.mergeSort(arr);
		Assert.assertArrayEquals(new Integer[]{1,2,5,6,9}, arr);
	}
}
