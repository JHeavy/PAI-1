package ssii.pai_1;

public class App {
	
    public static void main( String[] args ) throws Exception {
    	ExecuteThread exec = new ExecuteThread();
    	System.out.println(">-----------------------------------------------------------------------------------<");
    	System.out.println(String.format("Running %s", exec.scheduler.toString()));
    	System.out.println(">-----------------------------------------------------------------------------------<");
    }
}
