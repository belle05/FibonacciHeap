import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over non-negative integers.
 */

//TODO:delete min
//TODO:experiments
public class FibonacciHeap
{
	private TreeMap<Integer,HeapNode> heapMap =new TreeMap<Integer,HeapNode>();
	private List<HeapNode> heap = new ArrayList<HeapNode>();
	private List<Integer> heapSize = new ArrayList();
	private boolean isEmpty = true;
	private int size = 0;
	private HeapNode min_node = null;
	private int marked;
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
    	} else{
    		insertNodeToList(min_node, newNode);

    		if (key < this.min_node.getKey()) {
    			this.min_node = newNode;		
    		}
    	}
    	size++;
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
    	if (empty()){
    		return;
    	}
    	//we can be sure that min_node isn't null:
    	HeapNode temp = addMinChildrenToRoots();//TODO - this doesn't delete min, need to check
    	findMinInDoubleLinkedList(temp);
    	if (min_node != null){
    		link(); 
    	}
     	
    }

	private void findMinInDoubleLinkedList(HeapNode temp) {
		//we want to look from the starting point temp for the minimum between it's siblings.
		min_node = temp;
		if (temp != null){
			HeapNode brother = temp.getLeft();
			//while we haven't completed a circle, check if the next left sibling has a smaller key:
			while (brother != temp){
				if (brother.key<min_node.key){
					min_node = brother;
				}
				brother = brother.getLeft();
			}
		}
	}

	private void link() {
		HeapNode[] arrByRanks = new HeapNode[size];
		HeapNode current = min_node;
		
		//We want to go over the roots, and for each one, if we already have a tree with the same degree
		//link the two of them, and so on until it has a unique rank.
		do{
			int rank = current.getDegree();
			while (arrByRanks[rank] != null){
				current = linkTwoTrees(current, arrByRanks[rank]);
				arrByRanks[rank] = null;
				rank = current.getDegree();
			}
			arrByRanks[rank] = current;
			current = current.getLeft();
		}while(current != min_node);
	}
	

	private HeapNode linkTwoTrees(HeapNode current, HeapNode heapNode) {
		//we want to make sure that current.key <= other.key: 
		if(current.getKey() > heapNode.getKey()){
			HeapNode temp = current;
			current = heapNode;
			heapNode = temp;
		}
		
		//remove heapnode from it's siblings:
		//we know that there are at least two trees, so it's linked to some other node and not to itself:
		detachNodeFromItsSiblings(heapNode);
		HeapNode child = current.child;
		if (child == null){
			current.setChild(heapNode);
		} else{
		//add heapNode to current's children:
			insertNodeToList(child, heapNode);
		}
		heapNode.setParent(current);
		current.setDegree(current.getDegree()+1);
		return current;
	}
	

	private HeapNode detachNodeFromItsSiblings(HeapNode heapNode) {
		if (heapNode.getLeft() != heapNode && heapNode.getLeft() != null){
			heapNode.left.setRight(heapNode.right);
			HeapNode left = heapNode.getLeft();
			heapNode.left = null;
			heapNode.right = null;
			return left;
		}
		return null;
	}

	private void insertNodeToList(HeapNode node, HeapNode toInsert) {
		if (node.getRight() != null){
			HeapNode rightChild = node.getRight();
			rightChild.setLeft(toInsert);
			node.setRight(toInsert);
		}else{
			node.setRight(toInsert);
			node.setLeft(toInsert);
		}
	}
	
	
	
	

	/**
	 *  Here we want to add all min's children to the list:    
	 */
	private HeapNode addMinChildrenToRoots() {
		HeapNode returnNode = null;
		HeapNode firstChild = min_node.child;
		//We want to add all of the child to be roots, so we will link them in together with the other roots:
		if (firstChild != null){
			//connect min_left to child, connect min.right to child.left
			insertNodeToList(min_node, firstChild);
			HeapNode leftChild = firstChild.getLeft();
			min_node.left.setRight(firstChild);
			firstChild.setLeft(min_node.getLeft());
			min_node.right.setLeft(leftChild);
			leftChild.setRight(min_node.right);
			returnNode= leftChild;
		}else{
			//no children, we just want to get rid of min:
			returnNode = detachNodeFromItsSiblings(min_node);
		}
		//decrease size:
		size--;
		return returnNode;
	}

	
	
   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
    	return this.min_node;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	//TODO
    	if (heap2 ==null){
    		return;
    	}
    	//Add to the list of sources all the sources of the second heap
    	this.heap.addAll(heap2.heap);
    	//if this heap was empty, or the other heap wansn't empty and it's min was smaller than ours - change the min node.
    	if (this.min_node == null || (heap2.min_node != null && this.min_node.getKey() > heap2.min_node.getKey())){
    		this.min_node = heap2.min_node;
    	}//otherwise - there's no reason to change min..
    	
    	//update size and marked:
    	this.size += heap2.size;
    	this.marked += heap2.marked;
    	
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
    	//we already have a function in charge of deleting a node, why not use it?
    	//So we we'll decrease the key to be smaller than the min, and then delete min
    	//key - delta = min-1 => key-min+1 = delta
    	this.decreaseKey(x,(x.getKey()-min_node.getKey() +1));
    	this.deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {
    	x.setKey(x.getKey()- delta);
    	if (!(x.isLegalKey())){
    		HeapNode y = x.getParent();
    		if (y!= null && y.mark){
    			cascadingCut(x,y);
    			
    		}
    		else{
    			y.setMark(true);
    			this.marked +=1;
    		}
    	}
    	if (x.getKey() < min_node.getKey()){
    		min_node = x;
    	}	
    }

    
    
   private void cascadingCut(HeapNode x, HeapNode y) {
	// TODO Auto-generated method stub
	
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
    	return (this.heap.size() + 2*this.marked); 
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
    public static class HeapNode{
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
			if(this.left != left){
				this.left = left;
				left.setRight(this);
			}
		}
		
		public HeapNode getRight() {
			return this.right;
		}
		
		public void setRight(HeapNode right) {
			if (this.right != right){
				this.right = right;
				right.setLeft(this);
			}
		}
		
		public int getDegree() {
			return degree;
		}

	   	public void setDegree(int degree) {
			this.degree = degree;
		}
	   	
	   	public boolean isLegalKey(){
	   		if(this.parent != null){
	   			return this.key<this.parent.getKey();
	   		}
	   		return true;
	   	}
	   	
	   	public String toString(){
	   		return Integer.toString(key);
	   	}
    }
}
