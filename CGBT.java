
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CGBT {
	private Lock lock = new ReentrantLock();
	// here are variables available to tree

	static int order; // order of tree

	volatile BNode root; // every tree has at least a root node

	// ---------------------------------------------------------
	// here is the constructor for tree |
	// ---------------------------------------------------------

	public CGBT(int order) {
		this.order = order;

		root = new BNode(order, null);

	}

	public boolean search(Integer key) {
		lock.lock();
		try {
			int value = key.intValue();
			BNode node = search(this.root, value);
			if (node == null) {
				//.out.println("Not found: " + value);
				return false;
			}

			else {
				//System.out.println("Found: " + value);
				return true;
			}
		} finally {

			lock.unlock();
		}
	}

	public BNode search(BNode root, int key) {
		int i = 0;
		while (i < root.count && key > root.key[i]) {
			i++;
		}

		if (i <root.count && key == root.key[i]) {

			// System.out.println("Found: " + key);
			return root;
		}

		if (root.leaf) {
			// System.out.println("Not Found: " + key);
			return null;

		} else {

			return search(root.getChild(i), key);

		}

	}

	public void split(BNode x, int i, BNode y) {
		BNode z = new BNode(order, null);// gotta have extra node if we are
											// to split.

		z.leaf = y.leaf;// set boolean to same as y

		z.count = order - 1;// this is updated size

		for (int j = 0; j < order - 1; j++) {
			z.key[j] = y.key[j + order]; // copy end of y into front of z

		}
		if (!y.leaf)// if not leaf we have to reassign child nodes.
		{
			for (int k = 0; k < order; k++) {
				z.child[k] = y.child[k + order]; // reassing child of y
			}
		}

		y.count = order - 1; // new size of y

		for (int j = x.count; j > i; j--) {

			x.child[j + 1] = x.child[j];

		}
		x.child[i + 1] = z;

		for (int j = x.count - 1; j >= i; j--) {
			x.key[j + 1] = x.key[j];
		}
		x.key[i] = y.key[order - 1];

		y.key[order - 1] = 0;

		for (int j = 0; j < order - 1; j++) {
			y.key[j + order] = 0;
		}

		x.count++;
	}

	public void nonfullInsert(BNode x, int key) {
		int i = x.count;

		if (x.leaf) {
			while (i >= 1 && key < x.key[i - 1]) {
				x.key[i] = x.key[i - 1];

				i--;
			}

			x.key[i] = key;
			x.count++;

		}

		else {
			int j = 0;
			while (j < x.count && key > x.key[j])// find spot to recurse
			{ // on correct child
				j++;
			}

			// i++;

			if (x.child[j].count == order * 2 - 1) {
				split(x, j, x.child[j]);// call split on node x's ith child

				if (key > x.key[j]) {
					j++;
				}
			}

			nonfullInsert(x.child[j], key);// recurse
		}
	}

	public boolean insert(Integer key) {
		lock.lock();
		try {
			int value = key.intValue();
			insert(value);
			//System.out.println("Inserted: " + value);
			return true;
		} finally {

			lock.unlock();
		}
	}

	public void insert(int key) {

		BNode r = this.root;
		if (r.count == 2 * order - 1)// if is full
		{
			BNode s = new BNode(order, null);// new node

			this.root = s; // \
							// \
			s.leaf = false;// \
							// > this is to initialize node.
			s.count = 0; // /
							// /
			s.child[0] = r;///

			split(s, 0, r);// split root

			nonfullInsert(s, key); // call insert method
		} else
			nonfullInsert(r, key);// if its not full just insert it

	}

	public void print(BNode n) {
		for (int i = 0; i < n.count; i++) {
			System.out.print(n.getValue(i) + " ");
		}

		if (!n.leaf) {

			for (int j = 0; j <= n.count; j++) {
				if (n.getChild(j) != null) {
					System.out.println();
					print(n.getChild(j));
				}
			}
		}
	}

	public void SearchPrintNode(CGBT T, int x) {
		BNode temp = new BNode(order, null);

		temp = search(T.root, x);

		if (temp == null) {

			System.out.println("The Key does not exist in this tree");
		}

		else {

			print(temp);
		}

	}

	public boolean remove(Integer key) {
		lock.lock();
		int value = key.intValue();
		try {
			boolean delete = deleteFromNode(this.root, value);
//			if (delete == true)
//				System.out.println("Deleted: " + value);
//			else
//				System.out.println("Not Deleted: " + value);
			return delete;
		} finally {
			lock.unlock();
		}
	}

	public boolean deleteFromNode(BNode node, int key) {
		int idx = findKey(node, key);

		if (idx < node.count && node.key[idx] == key) {
			// System.out.println("Delete: " + key);
			if (node.leaf)
				removeFromLeaf(node, idx);
			else
				removeFromNonLeaf(node, idx);
			return true;
		} else {
			if (node.leaf) {
				// System.out.println("Key does not exist." + key);
				return false;
			}
			boolean flag = ((idx == node.count) ? true : false);

			if (node.child[idx].count < order) {
				fill(node, idx);

			}

			if (flag && idx > node.count)
				return deleteFromNode(node.child[idx - 1], key);
			else
				return deleteFromNode(node.child[idx], key);

		}

	}

	public int findKey(BNode node, int k) {
		int idx = 0;

		while (idx < node.count && node.key[idx] < k)
			++idx;

		return idx;
	}

	public void removeFromLeaf(BNode node, int idx) {

		for (int i = idx + 1; i < node.count; i++) {
			node.key[i - 1] = node.key[i];
		}

		node.count--;
	}

	public void removeFromNonLeaf(BNode node, int idx) {
		int k = node.key[idx];

		if (node.child[idx].count >= order) {
			int pred = getPred(node, idx);
			node.key[idx] = pred;
			deleteFromNode(node.child[idx], pred);
		} else if (node.child[idx + 1].count >= order) {
			int succ = getSucc(node, idx);
			node.key[idx] = succ;
			deleteFromNode(node.child[idx + 1], succ);
		} else {
			merge(node, idx);
			deleteFromNode(node.child[idx], k);
		}

	}

	public int getPred(BNode node, int idx) {

		BNode temp = node.child[idx];
		while (!temp.leaf)
			temp = temp.child[temp.count];

		return temp.key[temp.count - 1];

	}

	public int getSucc(BNode node, int idx) {

		BNode temp = node.child[idx + 1];
		while (!temp.leaf)
			temp = temp.child[0];

		return temp.key[0];

	}

	public void fill(BNode node, int idx) {
		if (idx != 0 && node.child[idx - 1].count >= order) {
			borrowFromPrev(node, idx);
		} else if (idx != node.count && node.child[idx + 1].count >= order) {
			borrowFromNext(node, idx);
		} else {
			if (idx != node.count)
				merge(node, idx);
			else
				merge(node, idx - 1);
		}
	}

	public void borrowFromPrev(BNode node, int idx) {
		BNode tempChild = node.child[idx];
		BNode sibling = node.child[idx - 1];

		for (int i = tempChild.count - 1; i >= 0; --i) {
			tempChild.key[i + 1] = tempChild.key[i];
		}

		if (!tempChild.leaf) {
			for (int i = tempChild.count; i >= 0; --i)
				tempChild.child[i + 1] = tempChild.child[i];
		}

		tempChild.key[0] = node.key[idx - 1];

		if (!node.leaf)
			tempChild.child[0] = sibling.child[sibling.count];

		node.key[idx - 1] = sibling.key[sibling.count - 1];

		tempChild.count += 1;
		sibling.count -= 1;

	}

	public void borrowFromNext(BNode node, int idx) {
		BNode child = node.child[idx];
		BNode sibling = node.child[idx + 1];

		child.key[child.count] = node.key[idx];

		if (!child.leaf)
			child.child[child.count + 1] = sibling.child[0];

		node.key[idx] = sibling.key[0];

		for (int i = 1; i < sibling.count; ++i) {
			sibling.key[i - 1] = sibling.key[i];
		}

		if (!sibling.leaf) {
			for (int i = 1; i <= sibling.count; ++i) {
				sibling.child[i - 1] = sibling.child[i];
			}
		}

		child.count += 1;
		sibling.count -= 1;

	}

	public void merge(BNode node, int idx) {
		BNode child = node.child[idx];
		BNode sibling = node.child[idx + 1];

		child.key[order - 1] = node.key[idx];

		for (int i = 0; i < sibling.count; ++i)
			child.key[i + order] = sibling.key[i];

		if (!child.leaf) {
			for (int i = 0; i <= sibling.count; ++i) {
				child.child[i + order] = sibling.child[i];
			}
		}

		for (int i = idx + 1; i < node.count; ++i)
			node.key[i - 1] = node.key[i];

		for (int i = idx + 2; i <= node.count; ++i)
			node.child[i - 1] = node.child[i];

		child.count += sibling.count + 1;
		node.count--;

		if (node.count == 0 && root == node) {
			root = child;
			node = child;
			node.count = child.count;
		}

	}

}
