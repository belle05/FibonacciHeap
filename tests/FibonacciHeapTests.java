import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

public class FibonacciHeapTests {
	private class FibonacciHeapTest extends FibonacciHeap{
		public String toString(){
			if(this.findMin() ==null){
				return "empty";
			}
			Stack<FibonacciHeap.HeapNode> stack = new Stack<FibonacciHeap.HeapNode>();
			stack.push(this.findMin());
			StringBuffer str = new StringBuffer();
			while (!(stack.empty())){
				FibonacciHeap.HeapNode curr = stack.pop();
				str.append("\tkey-"+curr.key+" mark-" + curr.getMark()+" degree-"+curr.degree);
				if(curr.getChild() != null){
					stack.push(curr.getChild());
				}
				if(curr.getLeft() !=null){
					FibonacciHeap.HeapNode start = curr;
					curr = curr.getLeft();
					while(curr!=start){
						str.append("\tkey-"+curr.key+" mark-" + curr.getMark()+" degree-"+curr.degree);
						if(curr.getChild() != null){
							stack.push(curr.getChild());
						}
						curr = curr.getLeft();
					}
				}
			}
			return str.toString();
		}

	}
	
	@Test
	public void testFindMinAfterInsrtsAndMinDelets(){
		FibonacciHeapTest heap = new FibonacciHeapTest();
		int[] toInsert = {5,456,2,67,32,76,8,15};
		int[] min = {5,5,2,2,2,2,2,2};
		for (int i=0; i<toInsert.length; i++){
			heap.insert(toInsert[i]);
			assertEquals(min[i],heap.findMin().getKey());
		}
		Arrays.sort(toInsert);
		for (int i=0; i<toInsert.length-1; i++){
			heap.deleteMin();
			//System.out.println(heap.toString());
			assertEquals(toInsert[i+1], heap.findMin().getKey());
		}
	}
	
	@Test
	public void testRandomDelets(){
		//TODO: waiting for cascading cuts for this one..
		//create random heap:
		List<FibonacciHeap.HeapNode> nodes =new ArrayList<FibonacciHeap.HeapNode>();
		List<Integer> keys = new ArrayList<Integer>();
		FibonacciHeapTest heap = new FibonacciHeapTest();
		for (int i=0; i<1000; i++){
			int rand = ThreadLocalRandom.current().nextInt(0, 100000);
			nodes.add(heap.insert(rand));
			keys.add(rand);
			}
		int size = 1000;
		for (int i=0; i<100; i++){
			int rand = ThreadLocalRandom.current().nextInt(0, size);
			FibonacciHeap.HeapNode toDelete = nodes.get(rand);
			heap.delete(toDelete);
			nodes.remove(rand);
			keys.remove(rand);
			size--;
			List<Integer> sortedKeys = new ArrayList<Integer>(keys);
			Collections.sort(sortedKeys);
			assertEquals(size, heap.size());
			int min = heap.findMin().getKey();
			int expected = sortedKeys.get(0);
			assertEquals(expected, min);
			
		}
		
	}
	
	@Test
	public void testMeldTwoFullHeaps(){
		//test normal meld:
		FibonacciHeapTest heap1 = new FibonacciHeapTest();
		for (int i=0; i<1001; i++){
			int rand = ThreadLocalRandom.current().nextInt(0, 100000);
			heap1.insert(rand);
		}
		//make somoe trees:
		heap1.deleteMin();
		FibonacciHeapTest heap2 = new FibonacciHeapTest();
		for (int i=0; i<1001; i++){
			int rand = ThreadLocalRandom.current().nextInt(0, 100000);
			heap2.insert(rand);
		}
		heap2.deleteMin();
		int heap2Potential = heap2.potential();
		int heap1Potential = heap1.potential();
		int min1 = heap1.findMin().getKey();
		int min2 = heap2.findMin().getKey();
		heap1.meld(heap2);
		assertEquals(heap2Potential+heap1Potential, heap1.potential());
		assertEquals(Math.min(min1, min2), heap1.findMin().getKey());
	}
	
	@Test
	public void testMeldEmptyHeaps(){
		FibonacciHeapTest heap1 = new FibonacciHeapTest();
		for (int i=0; i<1001; i++){
			int rand = ThreadLocalRandom.current().nextInt(0, 100000);
			heap1.insert(rand);
		}
		FibonacciHeapTest heap2 = new FibonacciHeapTest();
		int min1 = heap1.findMin().getKey();
		int heap2Potential = heap2.potential();
		int heap1Potential = heap1.potential();
		heap1.meld(heap2);
		assertEquals(heap2Potential+heap1Potential, heap1.potential());
		assertEquals(min1, heap1.findMin().getKey());
		
		heap2.meld(heap1);
		int min2 = heap2.findMin().getKey();
		
		assertEquals(heap2Potential+heap1Potential, heap1.potential());
		assertEquals(Math.min(min1, min2), heap1.findMin().getKey());

	}
	

}
