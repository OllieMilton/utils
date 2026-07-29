package ollie.utils.sortsearch;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SearchTest {

	@Test
	public void testBinarySearch() {
		Integer[] arr = {1,2,3,4,5,7,8,9,14,16,27,67,78,99,222,501};
		Assert.assertEquals(null, Search.binarySearch(arr, 223));
		Assert.assertEquals(new Integer(9), Search.binarySearch(arr, 9));
		Assert.assertEquals(new Integer(1), Search.binarySearch(arr, 1));
		Assert.assertEquals(new Integer(14), Search.binarySearch(arr, 14));
		Assert.assertEquals(new Integer(501), Search.binarySearch(arr, 501));
	}
	
	@Test
	public void testBinarySearchSingleEntry() {
		Integer[] arr = {2};
		Assert.assertEquals(null, Search.binarySearch(arr, 223));
		Assert.assertEquals(new Integer(2), Search.binarySearch(arr, 2));
	}
	
	@Test
	public void testBinarySearchDoubleEntry() {
		Integer[] arr = {2, 3};
		Assert.assertEquals(null, Search.binarySearch(arr, 223));
		Assert.assertEquals(new Integer(2), Search.binarySearch(arr, 2));
		Assert.assertEquals(new Integer(3), Search.binarySearch(arr, 3));
	}
}
