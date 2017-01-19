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
			for (HeapNode root: getRoots()){
				stack.push(root);
			}
			StringBuffer str = new StringBuffer();
			while (!(stack.empty())){
				FibonacciHeap.HeapNode curr = stack.pop();
				str.append("\tkey-"+curr.key+" mark-" + curr.getMark()+" degree-"+curr.degree);
				for (HeapNode child: curr.getChildren()){
					stack.push(child);
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
	
	@Test
	public void experiments(){
		int base = 1000;
		for (int i=1; i<=3; i++){
			singleExperiments(i*base,false);
			singleExperiments(i*base,true);
		}
	}
	
	
	private void singleExperiments(int size,boolean delete){
		int[] toInsert = new int[size];
		int j=0;
		for (int i=size; i>0;i-- ){
			toInsert[j]=i;
			j++;
		}
		FibonacciHeap heap = new FibonacciHeap();
		FibonacciHeap.initializeCuts();
		FibonacciHeap.initializeLinks();
		long startTime = System.nanoTime();
		for (int i=0; i<size; i++){
			heap.insert(toInsert[i]);
		}
		if (delete){
			for (int i=0; i<size/2; i++){
				heap.deleteMin();
			}
		}
		long endTime = System.nanoTime();
		if (delete){
			assertEquals((int)(size-size/2), heap.size());
			
		}
		double seconds = (double)(endTime-startTime)/(1000000);
		System.out.println("InsertExperiment- Size: " + size + " deletd:" + delete + " Elapsesd: "+ seconds +
		" totalLinks: "+ FibonacciHeap.totalLinks() + " totalCuts: " + FibonacciHeap.totalCuts() + " potential: " + heap.potential());
		
	}
	
	//**************GILI's TESTS***************//
	   @Test
	    public void TestInsertAndDeleteMin() throws IllegalArgumentException {
	        FibonacciHeap heap = new FibonacciHeap();
	        heap.insert(9);
	        heap.insert(2);
	        heap.insert(47);
	        heap.insert(13);
	        heap.insert(7);
	        heap.insert(5);
	        heap.insert(22);
	        heap.insert(1);
	        heap.insert(83);
	        assertEquals(1, heap.findMin().getKey());
	        heap.deleteMin();
	        assertEquals(2, heap.findMin().getKey());
	        heap.deleteMin();
	        assertEquals(5, heap.findMin().getKey());
	        heap.deleteMin();
	        assertEquals(7, heap.findMin().getKey());
	        heap.deleteMin();
	        assertEquals(9, heap.findMin().getKey());
	        heap.deleteMin();
	        assertEquals(13, heap.findMin().getKey());
	        heap.deleteMin();
	        assertEquals(22, heap.findMin().getKey());
	        heap.deleteMin();
	        assertEquals(47, heap.findMin().getKey());
	        heap.deleteMin();
	        assertEquals(83, heap.findMin().getKey());
	        heap.deleteMin();
	        assertEquals(null, heap.findMin());
	    }

	    @Test(expected=IllegalArgumentException.class)
	    public void deleteNullEmptyHeap() throws IllegalArgumentException {
	        FibonacciHeap heap = new FibonacciHeap();
	        heap.delete(null);
	    }
	    
	    @Test(expected=IllegalArgumentException.class)
	    public void deleteNullNonEmptyHeap() throws IllegalArgumentException {
	        FibonacciHeap heap = new FibonacciHeap();
	        heap.insert(1);
	        heap.delete(null);
	    }
	    
	    @Test
	    public void meld() throws IllegalArgumentException {
	        FibonacciHeap heap1 = new FibonacciHeap();
	        heap1.insert(9);
	        heap1.insert(2);
	        heap1.insert(47);
	        heap1.insert(13);
	        heap1.insert(7);
	        heap1.insert(5);
	        heap1.insert(22);
	        heap1.insert(1);
	        heap1.insert(83);
	        FibonacciHeap heap2 = new FibonacciHeap();
	        heap2.insert(60);
	        heap2.insert(18);
	        heap2.insert(10);
	        heap2.insert(4);
	        heap2.insert(30);
	        heap2.insert(90);
	        heap2.insert(50);
	        heap1.meld(heap2);
	        assertEquals(1, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(2, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(4, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(5, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(7, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(9, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(10, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(13, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(18, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(22, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(30, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(47, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(50, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(60, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(83, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(90, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(null, heap1.findMin());
	    }
	    
	    @Test
	    public void meldWithEmptyHeap() throws IllegalArgumentException {
	        FibonacciHeap heap1 = new FibonacciHeap();
	        heap1.insert(9);
	        heap1.insert(2);
	        heap1.insert(47);
	        heap1.insert(13);
	        heap1.insert(7);
	        heap1.insert(5);
	        heap1.insert(22);
	        heap1.insert(1);
	        heap1.insert(83);
	        FibonacciHeap heap2 = new FibonacciHeap();
	        heap1.meld(heap2);
	        assertEquals(1, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(2, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(5, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(7, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(9, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(13, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(22, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(47, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(83, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(null, heap1.findMin());

	    }
	    
	    @Test
	    public void EmptyHeapMeldWithFullHeap() throws IllegalArgumentException {
	        FibonacciHeapTest heap1 = new FibonacciHeapTest();
	        FibonacciHeapTest heap2 = new FibonacciHeapTest();
	        heap2.insert(9);
	        heap2.insert(2);
	        heap2.insert(47);
	        heap2.insert(13);
	        heap2.insert(7);
	        heap2.insert(5);
	        heap2.insert(22);
	        heap2.insert(1);
	        heap2.insert(83);
	        heap1.meld(heap2);
	        assertEquals(1, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(2, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(5, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(7, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(9, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(13, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(22, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(47, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(83, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(null, heap1.findMin());
	    }
	    
	    @Test
	    public void EmptyHeapMeldWithEmptyHeap() throws IllegalArgumentException {
	        FibonacciHeap heap1 = new FibonacciHeap();
	        FibonacciHeap heap2 = new FibonacciHeap();
	        heap1.meld(heap2);
	        assertEquals(null, heap1.findMin());
	    }
	    
	    @Test
	    public void oneWithOneMeld() throws IllegalArgumentException {
	        FibonacciHeap heap1 = new FibonacciHeap();
	        FibonacciHeap heap2 = new FibonacciHeap();
	        heap1.insert(9);
	        heap2.insert(2);
	        heap1.meld(heap2);
	        assertEquals(2, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(9, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(null, heap1.findMin());
	    }
	    
	    @Test(expected=IllegalArgumentException.class)
	    public void meldWithNull() throws IllegalArgumentException {
	        FibonacciHeap heap = new FibonacciHeap();
	        heap.insert(1);
	        heap.meld(null);
	    }
	    
	    @Test
	    public void decreaseKeyChangeMinTester() throws IllegalArgumentException {
	        FibonacciHeap heap1 = new FibonacciHeap();
	        FibonacciHeap.HeapNode nodeToDecreaseKey = heap1.insert(9);
	        heap1.insert(2);
	        heap1.insert(47);
	        heap1.insert(13);
	        heap1.insert(7);
	        heap1.insert(5);
	        heap1.insert(22);
	        heap1.insert(83);
	        heap1.decreaseKey(nodeToDecreaseKey, 8);
	        assertEquals(1, heap1.findMin().getKey());

	    }
	    
	    @Test
	    public void decreaseKeyTester() throws IllegalArgumentException {
	        FibonacciHeap heap1 = new FibonacciHeap();
	        FibonacciHeap.HeapNode nodeToDecreaseKey1 = heap1.insert(9);
	        FibonacciHeap.HeapNode nodeToDecreaseKey2 = heap1.insert(15);
	        FibonacciHeap.HeapNode nodeToDecreaseKey3 = heap1.insert(47);
	        FibonacciHeap.HeapNode nodeToDecreaseKey4 = heap1.insert(13);
	        FibonacciHeap.HeapNode nodeToDecreaseKey5 = heap1.insert(7);
	        FibonacciHeap.HeapNode nodeToDecreaseKey6 =  heap1.insert(5);
	        FibonacciHeap.HeapNode nodeToDecreaseKey7 = heap1.insert(22);
	        FibonacciHeap.HeapNode nodeToDecreaseKey8 = heap1.insert(83);
	        heap1.decreaseKey(nodeToDecreaseKey1, 0);
	        assertEquals(5, heap1.findMin().getKey());
	        heap1.decreaseKey(nodeToDecreaseKey2, 0);
	        assertEquals(5, heap1.findMin().getKey());
	        heap1.decreaseKey(nodeToDecreaseKey3, 7);
	        assertEquals(5, heap1.findMin().getKey());
	        heap1.decreaseKey(nodeToDecreaseKey4, 3);
	        assertEquals(5, heap1.findMin().getKey());
	        heap1.decreaseKey(nodeToDecreaseKey5, 5);
	        assertEquals(2, heap1.findMin().getKey());
	        heap1.decreaseKey(nodeToDecreaseKey6, 0);
	        assertEquals(2, heap1.findMin().getKey());
	        heap1.decreaseKey(nodeToDecreaseKey7, 4);
	        assertEquals(2, heap1.findMin().getKey());
	    }
	    
	    @Test(expected=IllegalArgumentException.class)
	    public void decreaseKeyNull() throws IllegalArgumentException {
	        FibonacciHeap heap = new FibonacciHeap();
	        heap.insert(1);
	        heap.decreaseKey(null, 8);
	    }
	    
	    @Test
	    public void decreaseKeyAndDelete() throws IllegalArgumentException {
	        FibonacciHeap heap1 = new FibonacciHeap();
	        FibonacciHeap.HeapNode nodeToDecreaseKey1 = heap1.insert(9);
	        FibonacciHeap.HeapNode nodeToDecreaseKey2 = heap1.insert(15);
	        FibonacciHeap.HeapNode nodeToDecreaseKey3 = heap1.insert(47);
	        FibonacciHeap.HeapNode nodeToDecreaseKey4 = heap1.insert(13);
	        FibonacciHeap.HeapNode nodeToDecreaseKey5 = heap1.insert(7);
	        FibonacciHeap.HeapNode nodeToDecreaseKey6 =  heap1.insert(5);
	        FibonacciHeap.HeapNode nodeToDecreaseKey7 = heap1.insert(22);
	        FibonacciHeap.HeapNode nodeToDecreaseKey8 = heap1.insert(83);
	        heap1.decreaseKey(nodeToDecreaseKey1, 0);
	        assertEquals(5, heap1.findMin().getKey());
	        heap1.decreaseKey(nodeToDecreaseKey2, 0);
	        assertEquals(5, heap1.findMin().getKey());
	        heap1.decreaseKey(nodeToDecreaseKey3, 7);
	        assertEquals(5, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(7, heap1.findMin().getKey());
	        heap1.decreaseKey(nodeToDecreaseKey4, 3);
	        assertEquals(7, heap1.findMin().getKey());
	        heap1.decreaseKey(nodeToDecreaseKey5, 5);
	        assertEquals(2, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(9, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(10, heap1.findMin().getKey());
	        heap1.deleteMin();
	        assertEquals(15, heap1.findMin().getKey());
	        heap1.decreaseKey(nodeToDecreaseKey7, 4);
	        assertEquals(15, heap1.findMin().getKey());
	    }
	    
	    @Test
	    public void decreaseKeyAndDelete2() throws IllegalArgumentException {
	        FibonacciHeap heap = new FibonacciHeap();
	        heap.insert(4);
	        heap.insert(2);
	        FibonacciHeap.HeapNode nodeToDecrease1 = heap.insert(3);
	        FibonacciHeap.HeapNode nodeToDecrease2 = heap.insert(5);
	        heap.insert(1);
	        heap.deleteMin();
	        heap.decreaseKey(nodeToDecrease1, 2);
	        heap.decreaseKey(nodeToDecrease2, 5);
	        heap.deleteMin();
	        assertEquals(1, heap.findMin().getKey());
	    }
	    
	    @Test
	    public void cutBug() {
	        FibonacciHeap heap = new FibonacciHeap();
	        FibonacciHeap.HeapNode nodeToDecrease1 = heap.insert(4);
	        FibonacciHeap.HeapNode nodeToDecrease2 = heap.insert(5);
	        heap.insert(1);
	        heap.deleteMin();
	        heap.decreaseKey(nodeToDecrease2, 2);
	        heap.deleteMin();
	        heap.insert(100);
	        heap.deleteMin();
	        assertEquals(100, heap.findMin().getKey());
	    }
	    
	    @Test
	    public void delete() throws IllegalArgumentException {
	        FibonacciHeap heap = new FibonacciHeap();
	        FibonacciHeap.HeapNode nodeToDelete3 = heap.insert(5);
	        heap.insert(2);
	        FibonacciHeap.HeapNode nodeToDelete1 = heap.insert(3);
	        FibonacciHeap.HeapNode nodeToDelete2 = heap.insert(4);
	        heap.insert(1);
	        heap.deleteMin();
	        heap.delete(nodeToDelete1);
	        assertEquals(2, heap.findMin().getKey());
	        heap.deleteMin();
	        heap.delete(nodeToDelete2);
	        assertEquals(5, heap.findMin().getKey());
	        heap.delete(nodeToDelete3);
	        assertEquals(null, heap.findMin());
	    }
	    
	    @Test
	    public void numberOfTreeTest() throws IllegalArgumentException {
	        FibonacciHeap heap = new FibonacciHeap();
	        FibonacciHeap.HeapNode nodeToDecrease3 = heap.insert(4);
	        heap.insert(2);
	        FibonacciHeap.HeapNode nodeToDecrease1 = heap.insert(3);
	        FibonacciHeap.HeapNode nodeToDecrease2 = heap.insert(5);
	        heap.insert(1);
	        heap.deleteMin();
	        assertEquals(1, heap.potential());
	        heap.decreaseKey(nodeToDecrease1, 2);
	        assertEquals(2, heap.potential());
	        heap.decreaseKey(nodeToDecrease2, 5);
	        assertEquals(3, heap.potential());
	        heap.decreaseKey(nodeToDecrease3, 4);
	        assertEquals(4, heap.potential());

	    }
	    
	    @Test
	    public void potentialTest() throws IllegalArgumentException {
	    	FibonacciHeap heap = new FibonacciHeap();
	    	FibonacciHeap.HeapNode nodeToDecreaseKey9 = heap.insert(10);
	    	FibonacciHeap.HeapNode nodeToDecreaseKey1 = heap.insert(9);
	    	FibonacciHeap.HeapNode nodeToDecreaseKey2 = heap.insert(19);
	    	FibonacciHeap.HeapNode nodeToDecreaseKey3 = heap.insert(47);
	    	FibonacciHeap.HeapNode nodeToDecreaseKey4 = heap.insert(13);
	    	FibonacciHeap.HeapNode nodeToDecreaseKey5 = heap.insert(7);
	    	FibonacciHeap.HeapNode nodeToDecreaseKey6 =  heap.insert(5);
	    	FibonacciHeap.HeapNode nodeToDecreaseKey7 = heap.insert(22);
	    	FibonacciHeap.HeapNode nodeToDecreaseKey8 = heap.insert(83);
	        heap.deleteMin();
	        assertEquals(7, heap.findMin().getKey());
	        heap.decreaseKey(nodeToDecreaseKey2, 11);
	        assertEquals(4, heap.potential());
	        assertEquals(7, heap.findMin().getKey());
	        heap.decreaseKey(nodeToDecreaseKey8, 70);
	        assertEquals(7, heap.potential());        
	        heap.decreaseKey(nodeToDecreaseKey9, 10);
	        assertEquals(0, heap.findMin().getKey());
	        assertEquals(7, heap.potential());
	    }
	    
	    @Test
	    public void counterArrayTest() throws IllegalArgumentException {
	    	FibonacciHeap heap = new FibonacciHeap();
	    	FibonacciHeap.HeapNode nodeToDecreaseKey9 = heap.insert(10);
	        FibonacciHeap.HeapNode nodeToDecreaseKey1 = heap.insert(9);
	        FibonacciHeap.HeapNode nodeToDecreaseKey2 = heap.insert(19);
	        FibonacciHeap.HeapNode nodeToDecreaseKey3 = heap.insert(47);
	        FibonacciHeap.HeapNode nodeToDecreaseKey4 = heap.insert(13);
	        FibonacciHeap.HeapNode nodeToDecreaseKey5 = heap.insert(7);
	        FibonacciHeap.HeapNode nodeToDecreaseKey6 =  heap.insert(5);
	        FibonacciHeap.HeapNode nodeToDecreaseKey7 = heap.insert(22);
	        FibonacciHeap.HeapNode nodeToDecreaseKey8 = heap.insert(83);
	        heap.deleteMin();
	        assertEquals(1, heap.countersRep()[3]);
	        heap.decreaseKey(nodeToDecreaseKey2, 11);
	        assertEquals(1, heap.countersRep()[1]);
	        assertEquals(1, heap.countersRep()[3]);
	        heap.decreaseKey(nodeToDecreaseKey8, 70);
	        assertEquals(1, heap.countersRep()[0]);
	        assertEquals(1, heap.countersRep()[1]);
	        assertEquals(1, heap.countersRep()[3]);        
	        heap.decreaseKey(nodeToDecreaseKey9, 10);
	        assertEquals(3, heap.countersRep()[0]);
	        assertEquals(1, heap.countersRep()[1]);
	        assertEquals(1, heap.countersRep()[2]);

	    }
	    
	    @Test
	    public void testArrayToHeap(){
	    	FibonacciHeap heap = new FibonacciHeap();
	    	int[] arr = {1,4,6,2,3,8};
	    	int[] arr2 = {2};
	    	for (int i:arr){
	    		heap.insert(i);
	    	}
	    	heap.arrayToHeap(arr2);
	    	assertEquals(1,heap.size());
	    	assertEquals(2, heap.findMin().getKey());
	    }

}
