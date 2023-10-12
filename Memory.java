import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Scanner;

//act as the Main Memory 
public class Memory 
{
	//initialize the memory
	static int[] memory = new int[2000];
	OutputStream out;

	//function to initialize memory
	//will place all contents of the file into memory
	private static void initialize(String fileName) throws FileNotFoundException {
		
		int in = 0;
		
		//get the file
	    File file = new File(fileName); 
	    Scanner sc = new Scanner(file); 
	    
	    //read the file line by line
	    while(sc.hasNextLine()){
	    	
	    	//if it is an integer
	    	if(sc.hasNextInt() == true){
	    		
	    		//store into main memory
	    		memory[in++] = sc.nextInt(); 
	    	} 
	    	else {
	    		
	    		//determine the character
	    		//can ignore comments
	    		if(sc.hasNext("//..*")){
	    			
	    			//remove the decimal and get the integer only
	    			//use that integer to jump to that position in memory
	    			int jump = Integer.parseInt(sc.next().substring(1));
	    			in = jump;
	    		}
	    		sc.nextLine();
		    		
	    	}
	    }
	    	
	}
	//function to read from main memory
	public static int read(int address) {
		
		//return the info at the given address
		return memory[address];
	}
	//function to write to main memory
	public static void write(int address, int data) {
		
		//store the information to memory
		memory[address] = data;
	}
	//main function
	public static void main(String[] args) throws FileNotFoundException {
	    
		Scanner scan = new Scanner(System.in);
		 
		String fileName = null;
		
		if (scan.hasNext() == true) {
			
		   fileName = scan.nextLine(); 	
		}
	
		//initialize memory - store the contents of the file into the array
		initialize(fileName);
		
	    //continue for all instructions 
	    while(scan.hasNext()){
	    	
	    	String nextLine = scan.nextLine();
	    	//
	    	//String[] temp = nextLine.split(" ");
	    	//int address;
	    	//int val;
	    	char act = nextLine.charAt(0);
	    	
	    	//read main memory
	    	if(act == 'R'){
	    		
	    		//System.out.println(read(Integer.parseInt(temp[0])));
	    		//address = Integer.parseInt(nextLine);
	    		System.out.println(read(scan.nextInt()));
	    	}
	    	//write to memory
	    	else if (act == 'W'){
		   
	    		//String[] arg = nextLine.substring(1).split(",");
				//address = Integer.parseInt(arg[0]);
				//val = Integer.parseInt(arg[1]);
	    		//write(address, val);
	    		//write(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
	    		write(scan.nextInt(), scan.nextInt());
	    	}
	    }
	  
	}
}