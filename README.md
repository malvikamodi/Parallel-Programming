# Parallel-Programming
Implemented flat combining, a synchronization paradigm based on coarse grained locks, wherein, a single thread holding the lock performs the combined operations of all other threads on a B-Tree to reduce synchronization overhead and cache invalidation traffic. Analyzed difference in performance of fine grained, coarse grained locks against flat combining.

FLAT COMBINING FOR BTREE <br />
The Algorithm implemented to introduce Flat Combining in B-Tree Structure is as follows: <br />
1) 	Request.opcode = opcode; Request.value = value <br />
	in thread local publication record <br />
	if(active == false): go to step 5 <br />
		
2) • if (globalLock == acquired) : <br />
	• while(response == null and active == true and globalLock == acquired); <br />
		• if(active == false): go to step 5 <br />
	• if(response != null): request=null; return response <br />
	
	
3) if (globalLock!=acquired): <br />
	• try(acquiring lock): become combiner; go to step 4 <br />
	• Else: go to step 2 <br />
	
4) Combiner <br />
• Combining pass count++; <br />
• scanCombineApply(); <br />
• if(count cleanUpAt ¿= CLEANUPCOUNT): <br />
• deleteOldRecords(); update cleanedUpAt; <br />
• Release the lock. <br />

5) if (!threadLocalPublicationRecord): Allocate and mark as active <br />
• if(threadLocalPublicationRecord as inactive): mark active <br />
• Insert record, CAS on head <br />
• Go to step 1 <br />

FINE GRAINED TREE

The fine grained B-tree is implemented by hand over hand locking technique. The lock is passed from parent to child
in order to ensure that multiple threads working on different sub trees can execute their operations concurrently.
The node class is updated to a Lockable node class which contains a re-entrant lock to lock the node by one thread at a time. There is also a head lock (re-entrant lock) which is used to lock the entire b-tree structure monetarily when traversing the root in order to ensure that no other thread is performing a split or merge operation on the root. The following sections give the algorithm for the fine grained insert and contains for hand over hand locking technique.

Algorithm for fine grained insert is as follows: <br />


1) Starting with the root, if (root.count==2*order-1) <br />
• headLock.lock(); <br /> 
• Split the root <br /> 
• Lock the new root <br />
• headLock.unlock(); <br />

2) Else: <br />
• headLock.lock(); <br />
• Lock the root <br />
• headLock.unlock(); <br />

3) Continue hand over hand locking, in the order: <br />
• The parent node is locked, identify the child node, and lock it <br />
• Check the count of the child node <br />
• if(child.count==2*order-1): split the child node <br />
• Unlock the Parent <br />

4) Perform insert on the leaf node, unlock and return <br />
