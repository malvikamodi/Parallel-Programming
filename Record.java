

public class Record {
	public Record next;
	public long age;
	public boolean active;
	public Request req;
	public boolean added;
	
	class Request{
		public int op; //0 for add
				//1 for remove
				//2 for contains
		public Integer value;
		public boolean done;
		public boolean retval;
		
		public Request(){
			done = true;		//true at first so the combiner doesn't pick it up
			// retval = false;
			op = -1;
		}
	}
	
	public Record(){
		age = 0;
		next = null;
		active = false;
		req = new Request();
	}
}
