public class BNode
{
	static int t;  //variable to determine order of tree

	volatile int count; // number of keys in node

	volatile int key[];  // array of key values

	volatile BNode child[]; //array of references

	volatile boolean leaf; //is node a leaf or not

	volatile BNode parent;  //parent of current node.


	public BNode()
	{}

	public BNode(int t, BNode parent)
	{
		this.t = t;  //assign size

		this.parent = parent; //assign parent

		key = new int[2*t - 1];  // array of proper size

		child = new BNode[2*t]; // array of refs proper size
		
		leaf = true; // everynode is leaf at first;

		count = 0; //until we add keys later.

	}

// -----------------------------------------------------
// this is method to return key value at index position|
// -----------------------------------------------------

	public int getValue(int index)
	{
		return key[index];
	}

// ----------------------------------------------------
// this is method to get ith child of node            |
// ----------------------------------------------------

	public BNode getChild(int index)
	{
		return child[index];
	}


}
