
public class TestFCB {

	private static final int THREAD_COUNT = 64;
	private static final int ORDER = 4;
	private static final int iterations = 4;
	private static FCBTree bTree = new FCBTree(ORDER);

	public static void main(String[] args) throws InterruptedException {
		int warmup = 4;
		int threadCount = Integer.parseInt(args[0]);
		int add = Integer.parseInt(args[1]);
		int remove = Integer.parseInt(args[2]);
		int contains = Integer.parseInt(args[3]);

		System.out.println("Warming up");
		for (int i = 0; i < warmup; i++) {
			TestThreadFCB[] threads = new TestThreadFCB[threadCount];
			double timeElapsed = 0;

			for (int t = 0; t < threadCount; t++)
				threads[t] = new TestThreadFCB(threadCount, bTree, add, remove, contains);

			for (int t = 0; t < threadCount; t++)
				threads[t].start();

			double start = System.currentTimeMillis();

			while (timeElapsed < 1000) {
				timeElapsed = System.currentTimeMillis() - start;
			}

			int total = 0;
			for (int i1 = 0; i1 < threadCount; i1++) {
				threads[i1].interrupt();
				total += threads[i1].getOperations();
			}

			for (int t = 0; t < threadCount; t++)
				threads[t].join();
		}
		
		double throughput[] = new double[iterations];
		double newThrough[] = new double[iterations];
		for (int j = 0; j < iterations;j++) {
			bTree.btree.scanTime=0;
			double scanTime=0;
			TestThreadFCB[] threads = new TestThreadFCB[threadCount];
			double timeElapsed = 0;

			for (int t = 0; t < threadCount; t++)
				threads[t] = new TestThreadFCB(threadCount, bTree, add, remove, contains);

			for (int t = 0; t < threadCount; t++)
				threads[t].start();

			double start = System.currentTimeMillis();

			//while (timeElapsed < 200000) {
			//	timeElapsed = System.currentTimeMillis() - start;
			//}
			int sum=0;
			
			while(sum<=1000000)
			for(int i=0;i<threadCount;i++)
			{
				sum+=threads[i].getOperations();
			}

			int total = 0;
			for (int i = 0; i < threadCount; i++) {
				threads[i].interrupt();
				total += threads[i].getOperations();
			}

			for (int t = 0; t < threadCount; t++)
				threads[t].join();

			// bTree.print(bTree.root);
			scanTime=bTree.btree.scanTime;
			throughput[j]=((double)total*1000)/(double)timeElapsed;
			//newThrough[j]=((double)total*1000)/(timeElapsed-(scanTime/1000000));
			System.out.println("For iteration: "+j);
			System.out.println("Throughput of the FCB tree: " + throughput[j] + " ops/sec");
			//System.out.println("New Throughput of the FCB tree: " + newThrough[j] + " ops/sec");
		}
		double avg=0,newavg=0;
		for(int i=0;i<iterations;i++){
			avg+=throughput[i];
			//newavg+=newThrough[i];
		}
		
		avg=avg/iterations;
		//newavg=newavg/iterations;
		
		System.out.println("Average throughput of the FCB tree: " + avg + " ops/sec for number of threads: "
				+ threadCount + "; add: " + add + ";remove: " + remove + ";contains: " + contains);
		//System.out.println("New average throughput of the FCB tree: " + newavg + " ops/sec for number of threads: "
			//	+ threadCount + "; add: " + add + ";remove: " + remove + ";contains: " + contains);
		System.out.println("---------------------------------");
	}

}
