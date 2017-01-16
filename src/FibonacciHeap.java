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
	private List<HeapNode> roots = new ArrayList<HeapNode>();
	private int size = 0;
	private HeapNode min_node = null;
	
	private static int totalLinks = 0;
	private static int totalCuts =0;
	public int marked = 0;
   /**
    * public boolean empty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
	private void initializeHeap(){
		roots = new ArrayList<HeapNode>();
		size = 0;
		min_node = null;
		initializeLinks();
		initializeCuts();
		marked = 0;
	}
	
    public boolean empty()
    {
    	return roots.isEmpty(); // should be replaced by student code
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
    */
    
    public HeapNode insert(int key)
    {   
    	HeapNode newNode = new HeapNode(key);
    	if (empty()) {
    		this.min_node =  newNode;
    		
    	} else{
    		if (key < this.min_node.getKey()) {
    			this.min_node = newNode;		
    		}
    	}
    	getRoots().add(newNode);
    	this.size++;
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
    	if (min_node ==null || empty()){
    		return;
    	}
    	//we can be sure that min_node isn't null:
    	addMinChildrenToRoots();
    	//TODO - this doesn't delete min, need to check
    	findMinInroots();
    	if (min_node != null){
    		link(); 
    	}
     	
    }

	private void findMinInroots() {
		//we want to look from the starting point temp for the minimum between it's siblings.
		if (roots!= null && !(roots.isEmpty())){
			min_node = getRoots().get(0);
			for (int i=1; i<getRoots().size(); i++){
				if (getRoots().get(i).getKey() < min_node.getKey()){
					min_node = getRoots().get(i);
				}
			}
			return;
		}
		min_node = null;
	}

	private void link() {
		HeapNode[] arrByRanks = new HeapNode[(int)(Math.log(size)/Math.log(2)+1e-10)+2];
		if (size <= 1){
			return;
		}
		int index = 0;
		int rootsSize = getRoots().size();
		while(index < rootsSize){
			HeapNode current = getRoots().get(index);
			int rank = current.getDegree();
			while (arrByRanks[rank] != null){
				current = linkTwoTrees(current, arrByRanks[rank]);
				arrByRanks[rank] = null;
				rank = current.getDegree();
				index--;
				rootsSize --;
			}
			arrByRanks[rank] = current;
			index++;
		}
	}
	

	private HeapNode linkTwoTrees(HeapNode current, HeapNode heapNode) {
		//we want to make sure that current.key <= other.key: 
		if(current.getKey() > heapNode.getKey()){
			HeapNode temp = current;
			current = heapNode;
			heapNode = temp;
		}
		
		//remove heapNode from it's siblings:
		//we know that there are at least two trees, so it's linked to some other node and not to itself:
		detachNodeFromItsSiblings(heapNode);
		current.addChild(heapNode);
		heapNode.setParent(current);
		current.setDegree(current.getDegree()+1);
		totalLinks++;
		return current;
	}
	

	/***
	 * 
	 * @param heapNode - a HeapNode we would like to detach from it's siblings and parents.
	 */
	private void detachNodeFromItsSiblings(HeapNode heapNode) {
		if (heapNode.getParent() != null){
			heapNode.getParent().removeChild(heapNode);
		} else{
			getRoots().remove(getRoots().indexOf(heapNode));
		}
	}
	
	
	

	/**
	 *  Here we want to add all min's children to the list:    
	 */
	private void addMinChildrenToRoots() {
		ArrayList<HeapNode> minChildren = min_node.getChildren();
		//Now returnNode holds min's brother:
		detachNodeFromItsSiblings(min_node);
		//We want to add all of the child to be roots, so we will link them in together with the other roots:
		getRoots().addAll(minChildren);
		for (HeapNode child:minChildren){
			child.setParent(null);
			if(child.getMark()==true){
				child.setMark(false);
				marked--;
			}
		}
		//decrease size:
		size--;
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
    	if (heap2 ==null){
    		throw new IllegalArgumentException();
    	}
    	roots.addAll(heap2.getRoots());
    	findMinInroots();
    	
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
    	List<Integer> heapSizes = new ArrayList<Integer>();
    	for (HeapNode root:roots){
    		heapSizes.add(root.degree);
    	}
    	int largest_tree = Collections.max(heapSizes);
    	int[] sizes_array = new int[largest_tree+1];
    	for (int i=0; i <= largest_tree; i++){
    		sizes_array[i] = Collections.frequency(heapSizes, i);
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
    	initializeHeap();
    	if (array.length > 0) {
    		for (int key:array){
    			insert(key);
    		}
    	}
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) 
    {    
    	if (x == null){
    		throw new IllegalArgumentException();
    	}
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
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {
    	if(x == null){
    		throw new IllegalArgumentException();
    	}
    	x.setKey(x.getKey()- delta);
    	if (!(x.isLegalKey())){
    		cascadingCut(x);
    	}
    	if (x.getKey() < min_node.getKey()){
    		min_node = x;
    	}
    }

   private void cut(HeapNode node) {
	   HeapNode parent = node.getParent();
	   totalCuts += 1;
	   node.mark = false;
	   if (parent == null)
		   throw new IllegalArgumentException();
	   parent.setDegree(parent.getDegree() - 1);
	   getRoots().add(node);
	   parent.removeChild(node);	   
   } 
    
   private void cascadingCut(HeapNode node) {
	   HeapNode parent = node.getParent();
       if (node.mark) {
           this.marked -= 1;        	
       }
       this.cut(node);
       if (parent.parent != null) {
           if (!(parent.mark)) {
        	   this.marked += 1;
               parent.mark = true;
           } else {
               cascadingCut(parent);
           }
       }
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
    	return (getRoots().size()+ 2*this.marked); 
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
    	return totalLinks; // should be replaced by student code
    }
    public static void  initializeLinks(){
    	totalLinks = 0;
    }
    
    public static void initializeCuts(){
    	totalCuts = 0;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return totalCuts; // should be replaced by student code
    }
    
   public List<HeapNode> getRoots() {
	return roots;
}

public void setRoots(List<HeapNode> roots) {
	this.roots = roots;
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
    	protected ArrayList<HeapNode> children = new ArrayList<HeapNode>();
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

		
		public ArrayList<HeapNode> getChildren(){
			return this.children;
		}
		
		public void addChild(HeapNode newChild){
			this.children.add(newChild);
		}
		
		public void removeChild(HeapNode grownUpChild){
			int index = this.children.indexOf(grownUpChild);
			if (index != -1){
				this.children.remove(index);
				grownUpChild.setParent(null);
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
	   			return this.key>this.parent.getKey();
	   		}
	   		return true;
	   	}
	   	
	   	public String toString(){
	   		return Integer.toString(key);
	   	}
    }
}
