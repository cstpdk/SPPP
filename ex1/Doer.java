class Doer{
	public static void doTwice(Runnable r){
		r.run();
		r.run();
	}

	public static void doNTimes(Runnable r, int n){
		for(int i = 0; i < n; i++){
			r.run();
		}
	}

	public static void main(String[] args) {
		doTwice(new Runnable(){
			public void run(){
				System.out.println("HALLO WELT!");
			}
		});
		doNTimes(new Runnable(){
			public void run(){
				System.out.println("HALLO WELT!");
			}},14);
	}
}
