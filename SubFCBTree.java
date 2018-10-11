

import java.util.concurrent.atomic.AtomicReference;



public class SubFCBTree 
{
	private ThreadLocal<Record> rec = null;
	private AtomicReference<Record> head;
	private CASLock lock;
	private long count = 0;
	private long cleanedupat = 0;

	private static final int CLEANUP_COUNT = 100;
	private static final int AGE_DIFFERENCE = 1000;

	private static BTree btree = new BTree(4);
	public double scanTime;
	
	public SubFCBTree(int order)
	{
		btree = new BTree(order);
		rec = new ThreadLocal<Record>(){
			protected Record initialValue(){
				return new Record();
			}
		};
		lock = new CASLock();
		head  = new AtomicReference<Record>();
		scanTime=0;
	}

	public boolean insert(Integer val){
		return runFunc(0, val);
	}

	/**
	 * Removes the key and value associated with the provided key from the 
	 * hash map as either the combiner or the waiter thread
	 */
	public boolean remove(Integer val){
		return runFunc(1, val);
	}

	/**
	 * Gets the value associated with the key from the hash map as either 
	 * the combiner or the waiter thread
	 */
	public boolean search(Integer val){
		return runFunc(2, val);
	}


	private boolean runFunc(int op, Integer val)
	{
		//System.out.println("running op " + op +" with "+ key + " and " + val);
		while(true){
			//set all of the relevent values
			rec.get().req.op = op;
			rec.get().req.value = val;
			//if it's active run the active function
			if(rec.get().active){
				boolean ret = active();
				return ret;
			}else{
				set_active();
			}
		}
	}

	//Record is active, we must enter and run
	private boolean active(){
		//we know at the start, we clearly aren't done
		//and we also know the params are also set
		rec.get().req.done = false;
		while(true){
			boolean locked = true;
			if(!lock.lock()){ //NOT the combiner
				while(!rec.get().req.done &&
						rec.get().active &&
						(locked = lock.isLocked()));
			}
			else{//AM the combiner
				//run through the publication list and complete all the operations
				amLockholder();
				lock.unlock();
				//don't worry about locked. 
			}
			//if we've been set inactive, we need to fix that and continue again
			if(!rec.get().active){
				set_active();
				continue;
			}
			//if lockholder, won't enter.  if came unlocked, and spinning, 
			//we try to grab it
			else if(!locked){
				continue;
			}
			else{//returned
				return rec.get().req.retval;
			}
		}
	}

	/**
	 * I am the lockholder!  increase the age and run scanCombineApply()
	 * Possibly need to remove older records as well, if it's time to do so
	 */
	private void amLockholder(){
		count++;
		scanCombineApply();
		double start=System.nanoTime();
		if((count - cleanedupat) >= CLEANUP_COUNT){
			removeOldRecords();
			cleanedupat=count;
		}
		double end=System.nanoTime();
		scanTime=scanTime+(end-start);
	//System.out.println("Value of: "+(double)(end-start));	
	}

	/**
	 * to remove old record, we run through the entire publication list and read 
	 * everybody's age, comparing it to our threshold.  A physical remove may be
	 * necessary
	 */
	private void removeOldRecords(){
	//	System.out.println("Removing records: CLEANUP");
		Record curr = head.get();
		Record pred;

		while(curr != null){
			pred = curr;
			curr = curr.next; //skip first node

			//perform the physical deletion
			while((curr != null) && curr.age < (count - AGE_DIFFERENCE)){
				pred.next = curr.next;
				curr.next = null;
				curr.added = false;
				curr.active = false;
				curr = curr.next;
			}
		}
	}


	private void scanCombineApply() {
		Record curr = head.get();
		//start at head and run through the publication list
		while(curr != null){
			//check if it's necessary to operate
			if(!curr.req.done){ //means we need to execute
				switch(curr.req.op){
				case 0:
					curr.req.retval = btree.insert(curr.req.value);
					break;
				case 1:
					curr.req.retval = btree.deleteKey(curr.req.value);
					break;
				case 2: 
					curr.req.retval = btree.searchKey(curr.req.value);
					break;
				default:
					System.err.println("I have no idea how this happened but a " +
							"bad op was passed.  We'll pass over this one.");
					break;
				}
				//set the new age and set done to true!
				curr.age = count;
				curr.req.done = true;
			}
			curr = curr.next;
		}
	}

	/**
	 * Sets the record as active.  
	 * If the record isn't in the publication list, we add it in
	 */
	private void set_active(){
		while(!rec.get().added){
			Record headref = head.get();
			rec.get().next = headref;
			rec.get().added = head.compareAndSet(headref, rec.get());
		}
		rec.get().active = true;
	}

	/**
	 * prints the entire publication list.  This is just a helper function
	 * for debugging.
	 */

	public synchronized void printList(){
		Record curr = head.get();
		while(curr != null){
			System.out.println("node: " + curr);
			curr = curr.next;
		}
	}
}