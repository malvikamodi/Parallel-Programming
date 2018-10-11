

import java.util.concurrent.atomic.AtomicInteger;




public class CASLock {
	private AtomicInteger lockholder;
	
	public CASLock(){
		lockholder = new AtomicInteger(-1);
	}
	
	public boolean lock() {
		int threadid = ((TestThreadFCB) Thread.currentThread()).getThreadId();
		return lockholder.compareAndSet(-1, threadid);
	}

	public boolean unlock() {
		int threadid = ((TestThreadFCB) Thread.currentThread()).getThreadId();
		return lockholder.compareAndSet(threadid, -1);
	}
	
	public boolean isLocked(){
		return !lockholder.compareAndSet(-1, -1);
	}
}
