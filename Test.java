
public class Test {

	private static final int THREAD_COUNT = 32;
	private static final int ORDER = 4;
	private static final int iterations = 4;
	private static CGBT bTree = new CGBT(ORDER);

	public static void main(String[] args) throws InterruptedException {
		int warmup = 4;
		int threadCount = Integer.parseInt(args[0]);
		int add = Integer.parseInt(args[1]);
		int remove = Integer.parseInt(args[2]);
		int contains = Integer.parseInt(args[3]);

		System.out.println("Warming up");
		for (int i = 0; i < warmup; i++) {
			TestThread[] threads = new TestThread[threadCount];
			double timeElapsed = 0;

			for (int t = 0; t < threadCount; t++)
				threads[t] = new TestThread(threadCount, bTree, add, remove, contains);

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
		for (int j = 0; j < iterations;j++) {
			TestThread[] threads = new TestThread[threadCount];
			double timeElapsed = 0;

			for (int t = 0; t < threadCount; t++)
				threads[t] = new TestThread(threadCount, bTree, add, remove, contains);

			for (int t = 0; t < threadCount; t++)
				threads[t].start();

			double start = System.currentTimeMillis();

			while (timeElapsed < 2000) {
				timeElapsed = System.currentTimeMillis() - start;
			}

			int total = 0;
			for (int i = 0; i < threadCount; i++) {
				threads[i].interrupt();
				total += threads[i].getOperations();
			}

			for (int t = 0; t < threadCount; t++)
				threads[t].join();

			// bTree.print(bTree.root);
			throughput[j]=((double)total*1000)/(double)timeElapsed;
			System.out.println("For iteration: "+j);
			System.out.println("Throughput of the CGBT tree: " + throughput[j] + " ops/sec");
		}
		double avg=0;
		for(int i=0;i<iterations;i++){
			avg+=throughput[i];
		}
		
		avg=avg/iterations;
		
		System.out.println("Average throughput of the CGBT tree: " + avg + " ops/sec for number of threads: "
				+ threadCount + "; add: " + add + ";remove: " + remove + ";contains: " + contains);
		System.out.println("---------------------------------");
	}

}
