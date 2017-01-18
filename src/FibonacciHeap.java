/***
 * @author Carine Bell Feder 203336425
 * @author Tami Lavi 203010574
 */
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
	//Array list to hold roots of fibonacci trees in the heap:
	private List<HeapNode> roots = new ArrayList<HeapNode>();
	private int size = 0;
	private HeapNode min_node = null;
	private int marked = 0;
	
	//Parameters to 
	private static int totalLinks = 0;
	private static int totalCuts =0;
	
	/***
	 * Initialize all heap parameters:
	 */
	private void initializeHeap(){
		roots = new ArrayList<HeapNode>();
		size = 0;
		min_node = null;
		initializeLinks();
		initializeCuts();
		marked = 0;
	}
	

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
    	//if the heap is empty, there is nothing to do:
    	if (min_node ==null || empty()){
    		return;
    	}
    	//we can be sure that min_node isn't null:
    	//lets add it's children to the roots:
    	addMinChildrenToRoots();
    	//and now link all the roots:
    	if (min_node != null){
    		link(); 
    	}
    	//after we've linked, there should be O(log(size)) roots, now lets find the minimum:
    	findMinInroots();
     	
    }

    /***
     * Updates the min-node:
     */
	private void findMinInroots() {
		//if we have roots, there should be a minimum:
		if (roots!= null && !(roots.isEmpty())){
			min_node = getRoots().get(0);
			//look for the minimum:
			for (int i=1; i<getRoots().size(); i++){
				if (getRoots().get(i).getKey() < min_node.getKey()){
					min_node = getRoots().get(i);
				}
			}
			return;
		}
		//there aren't any nodes, so theere isn't a miniumum:
		min_node = null;
	}
	
	/**
	 * This method is incharge of linking the roots according to size
	 */
	private void link() {
		if (size <= 1){
			return;
		}
		//We want to link each two trees the same size, so we will hold an array with the possible sizes:
		int arrSize = (int)(Math.log(size)/Math.log(2)+1e-10)+2;
		HeapNode[] arrByRanks = new HeapNode[arrSize];
		
		
		int index = 0;
		int rootsSize = getRoots().size();
		//while there is still a root we haven't checked or roots we've updated:
		while(index < rootsSize){
			HeapNode current = getRoots().get(index);
			//this will help us decide if to link two trees:
			int rank = current.getDegree();
			//While it doesn't have a unique tree size, link with the tree it's size:
			while (arrByRanks[rank] != null){
				current = linkTwoTrees(current, arrByRanks[rank]);
				arrByRanks[rank] = null;
				rank = current.getDegree();
				index--;
				rootsSize --;
			}
			//we now that current has a unique size now, we can add it to the array safely:
			arrByRanks[rank] = current;
			index++;
		}
	}
	

	/**
	 * Incharge of melding two trees togetherr:
	 * @param current - the current root we are checking
	 * @param heapNode - the heapNode of the root with the same degree ad current
	 * @return the new root of the linked tree (the with with the smaller key between the two)
	 */
	private HeapNode linkTwoTrees(HeapNode current, HeapNode heapNode) {
		//we want to make sure that current.key <= other.key: 
		if(current.getKey() > heapNode.getKey()){
			HeapNode temp = current;
			current = heapNode;
			heapNode = temp;
		}
		
		//remove heapNode from it's siblings and parent:
		detachNodeFromItsSiblings(heapNode);
		//add heapNode to it's new family:
		current.addChild(heapNode);
		heapNode.setParent(current);
		//supdate the rank:
		current.setDegree(current.getDegree()+1);
		totalLinks++;
		return current;
	}
	

	/***
	 * 
	 * @param heapNode - a HeapNode we would like to detach from it's siblings and parents.
	 */
	private void detachNodeFromItsSiblings(HeapNode heapNode) {
		//We aren't sure iif the node is somene's child or not, handle bith cases:
		if (heapNode.getParent() != null){
			heapNode.getParent().removeChild(heapNode);
		} else{
			getRoots().remove(getRoots().indexOf(heapNode));
		}
	}
	
	
	

	/**
	 *  Here we want to add all min's children to the roots:    
	 */
	private void addMinChildrenToRoots() {
		ArrayList<HeapNode> minChildren = min_node.getChildren();
		//remove min_node from the roots:
		detachNodeFromItsSiblings(min_node);
		//We want to add all of the children to be roots, so we will link them in together with the other roots:
		getRoots().addAll(minChildren);
		//and update their mark & parent:
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
    	//according to the instructions, this is an illegal argument:
    	if (heap2 ==null ){
    		throw new IllegalArgumentException();
    	}
    	//add to roots and update minimun:
    	roots.addAll(heap2.getRoots());
    	if (this.min_node == null){
    		this.min_node = heap2.min_node;
    	} else {
	    	if (!(heap2.empty()) && heap2.min_node.key < this.min_node.key){
	    		min_node = heap2.min_node;
	    	}
    	}
    	
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
    	//according to the instructions, these are illegal arguments:
    	if(x == null || delta <0){
    		throw new IllegalArgumentException();
    	}
    	//update the key:
    	x.setKey(x.getKey()- delta);
    	//if it isn't a legal situation, call cacading cuts:
    	if (!(x.isLegalKey())){
    		cascadingCut(x);
    	}
    	//update minimum iif needed:
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
    
    //Used because totalLinks is static, and at times we want to initialize it:
    public static void  initializeLinks(){
    	totalLinks = 0;
    }
    
    //Same ad initializeCuts
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

/**
    * public class HeapNode 
    */
    public static class HeapNode{
    	protected int key;
    	protected boolean mark = false;
    	protected HeapNode parent = null;
    	protected ArrayList<HeapNode> children = new ArrayList<HeapNode>();
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

		public void  setParent(HeapNode parent) {
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
	   	
	   	/***
	   	 * @return if the node is in a legal position
	   	 */
	   	public boolean isLegalKey(){
	   		if(this.parent != null){
	   			return this.key>this.parent.getKey();
	   		}
	   		return true;
	   	}
	   	
	   	//Used to print the heap:
	   	public String toString(){
	   		return Integer.toString(key);
	   	}
    }
}
