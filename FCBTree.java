

public class FCBTree {
	
	public SubFCBTree btree;

	public FCBTree(int order)
	{
		btree = new SubFCBTree(order);
	}

	public boolean insert (Integer val) {
		return btree.insert(val);
	}

	public boolean remove(Integer val) {
		return btree.remove(val);
	}

	public boolean search(Integer val) {
		return btree.search(val);
	}
}
