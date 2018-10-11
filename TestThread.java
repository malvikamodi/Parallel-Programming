

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class TestThread  extends Thread {

	private static int ID_GEN = 0;
	private CGBT bTree;	
	private int id;
	AtomicInteger operations = new AtomicInteger();
	private int add;
	private int remove;
	private int contains;
	
	
	public TestThread(int threadCount,CGBT bTree, int add, int remove, int contains) {
		id = ID_GEN++;
		this.bTree=bTree;
		this.add=add;
		this.remove=remove;
		this.contains=contains;
//	System.out.println(id+"\n");
	}
	
	@Override
	public void run() {
		while(!Thread.interrupted()){
			Random random = new Random();
			int number;
		//	System.out.println("In testThread for thread: "+id+";add: "+add+"; remove: "+remove+";contains: "+contains);
			for(int i=0;i<add;i++){
				number= random.nextInt(100);
			//	System.out.println("Adding "+number);
				bTree.insert(new Integer(number));
			}
			
			for(int j=0;j<contains;j++){
				number= random.nextInt(100);
			//	System.out.println("Searching for "+number);
				bTree.search(new Integer(number));
			}
			
			for(int k=0;k<remove;k++){
				number= random.nextInt(100);	
			//	System.out.println("delete for "+number);
				bTree.remove(new Integer(number));
			}
		

			   operations.set(operations.get()+20);
			
		}

	
		
	}
	
	
	
	
	public int getThreadId(){
		return id;
	}
	
	public int getOperations(){
		return operations.get();
		
	}
	
	
}
