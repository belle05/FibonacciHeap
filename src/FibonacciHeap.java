import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over non-negative integers.
 */
public class FibonacciHeap
{
	private List<HeapNode> heap = new ArrayList<HeapNode>();
	private List<Integer> heapSize = new ArrayList();
	private boolean isEmpty = true;
	private int size = 0;
	private HeapNode min_node = null;
   /**
    * public boolean empty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean empty()
    {
    	return isEmpty; // should be replaced by student code
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
    */
    private void setEmptyness(boolean emptyness) {
    	this.isEmpty = emptyness;
    }
    
    public HeapNode insert(int key)
    {    
    	HeapNode newNode = new HeapNode(key);
    	heap.add(newNode);
    	heapSize.add(1);
    	if (isEmpty) {
    		this.setEmptyness(false);
    		this.min_node =  newNode;
    	} else if (key < this.min_node.getKey()) {
    		this.min_node = newNode;
    	}
    	return newNode;
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
     	return; // should be replaced by student code
     	
    }

   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
    	return this.min_node;// should be replaced by student code
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	  return; // should be replaced by student code   		
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()
    {
    	return this.size; // should be replaced by student code
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
    	int largest_tree = Collections.max(this.heapSize);
    	int[] sizes_array = new int[largest_tree];
    	for (int i=0; i < largest_tree; i++){
    		sizes_array[i] = Collections.frequency(heapSize, i);
    	}
        return sizes_array;
    }

   /**
    * public void arrayToHeap()
    *
    * Insert the array to the heap. Delete previous elemnts in the heap.
    * 
    */
    public void arrayToHeap(int[] array)
    {
    	if (array.length == 0) {
    		this.heap = new ArrayList<HeapNode>();
    		this.heapSize = new ArrayList();
    		this.isEmpty = true;
    		this.size = 0;
    		this.min_node = null;
    	}
        return; //	 to be replaced by student code
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) 
    {    
    	return; // should be replaced by student code
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	return; // should be replaced by student code
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	return 0; // should be replaced by student code
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return 0; // should be replaced by student code
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return 0; // should be replaced by student code
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{
    	protected int key;
    	protected boolean mark = false;
    	protected HeapNode parent = null;
    	protected HeapNode child = null;
    	protected HeapNode left = null;
    	protected HeapNode right = null;
    	protected int degree = 0;
    	
    	public HeapNode(int key) {
    		this.setKey(key);
    	}
    	
	   	public int getKey() {
			return this.key;
		}
    	
	   	public void setKey(int key) {
			this.key = key;
		}
	   	
	   	public boolean getMark() {
	   		return this.mark;
	   	}
	   	
	   	public void setMark(boolean mark) {
	   		this.mark = mark;
	   	}
    	
	   	public HeapNode getParent() {
			return parent;
		}

		public void setParent(HeapNode parent) {
			this.parent = parent;
		}

		public HeapNode getChild() {
			return this.child;
		}
		
		public void setChild(HeapNode child) {
			this.child = child;
			child.setParent(this);
		}
		
		public HeapNode getLeft() {
			return this.left;
		}
		
		public void setLeft(HeapNode left) {
			this.left = left;
			left.setRight(this);
		}
		
		public HeapNode getRight() {
			return this.right;
		}
		
		public void setRight(HeapNode right) {
			this.right = right;
			right.setLeft(this);
		}
		
		public int getDegree() {
			return degree;
		}

	   	public void setDegree(int degree) {
			this.degree = degree;
		}
    }
}
