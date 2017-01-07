import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class FibonacciHeapTests {
	
	@Test
	public void testFindMinAfterInsetsAndDelets(){
		FibonacciHeap heap = new FibonacciHeap();
		int[] toInsert = {5,456,2,67,32,76,8,15};
		int[] min = {5,5,2,2,2,2,2,2};
		for (int i=0; i<toInsert.length; i++){
			heap.insert(toInsert[i]);
			assertEquals(min[i],heap.findMin().getKey());
		}
		Arrays.sort(toInsert);
		for (int i=0; i<toInsert.length-1; i++){
			heap.deleteMin();
			assertEquals(toInsert[i+1], heap.findMin().getKey());
		}
	}

}
