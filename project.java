import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Random;

public class project {
	
	//will act as the processor 
	public static class Processor{
		
		//variables
		PrintWriter p;
		OutputStream out;
		InputStream in;
		static Scanner inpt;
		static Process memory;
		static int timer;
		int timerLength;
		static boolean currentMode;	
		static final int timerInterrupt = 1000;
		static final int intInterrupt = 1500;
		static final int user = 1000;
		static final int system = 2000;

		//the CPU registers
		static int PC;
		static int SP;
		static int IR;
		static int AC;
		static int X;
		static int Y;
				
		//constructor
		public Processor(InputStream in, Scanner inpt, PrintWriter p, int timerLength) {
			
			this.in = in;
			project.Processor.inpt = inpt;
			this.p = p;
			this.timerLength = timerLength;
			
			PC = 0;
			SP = user;
			IR = 0;
			AC = 0;
			X = 0;
			Y = 0;
			timer = 0;
			
			//assume that the system automatically begins in user mode
			//false = user mode, true = system/kernel mode
			currentMode = false;				
		}
		//function to handle reading memory
		public int read(int address) throws InterruptedException, IOException {
			
			//determine the position of the memory address
			//also determine if the address is in user program or system program
			//if the address is in system code and the current mode is in system mode
			if(address > 999 && currentMode == true) {
				
				//give an error message 
				System.err.println("Error: Cannot access system memory. The system is not in kernel mode. Please enter valid address or enter kernel mode");
				
				//close and exit
				p.flush();
				p.close();
				out.close();
				in.close();
				memory.waitFor();
				System.exit(-1);
				
			//if the address is in user program and the current mode is not in system mode
			} 
			//System.out.println("here");
			//read the instruction from memory
			p.println("R");
			p.println(address);
			p.flush();
			
			//get the data and convert to integer
			if(inpt.hasNextLine()){
				
				IR = Integer.parseInt(inpt.nextLine());
				//System.out.println(IR);
				return IR;
			}
			//System.out.println(IR);
			//System.out.println("here");
			//return IR;
			return -1;
		}
		//function to handle writing to memory
		public void write(int address, int data) throws IOException, InterruptedException {
			
			//determine the position of the memory address
			//also determine if the address is in user program or system program
			//if the address is in system code and the current mode is in system mode
			if(address > 999 && currentMode == true) {
				
				//give an error message and close and exit
				System.err.println("Error: Cannot access system memory. The system is not in kernel mode. Please enter valid address or enter kernel mode");
				p.flush();
				p.close();
				out.close();
				in.close();
				memory.waitFor();
				System.exit(-1);
				
			//if the address is in user program and the current mode is not in system mode
			} else {
				
				//write the output to memory
				p.println("W");
				p.println(address);
				p.println(data);
				p.flush();
				
			}
			
		}
		//function to close and exit
		public void end() throws IOException, InterruptedException {
			
			//close pipes
			p.flush();
			p.close();
			out.close();
			in.close();

			memory.waitFor();
			
			//exit
			System.exit(0);
		}
		//function to get instruction from memory
		//the fetch portion of the instruction cycle
		public void fetch() throws InterruptedException, IOException {
			
			//read and get the value stored in the position in memory
			//also, increment PC so next instruction can be retrieved
			IR = read(PC);
			PC++;
		}
		//function to pop register from the stack
		public int pop() throws InterruptedException, IOException {
			
			//read the value and return it
			IR = read(SP++);
			return IR;
		}
		//function to push register to the stack
		public void push(int x) throws IOException, InterruptedException {
			
			//write the value to the stack
			//SP--;
			write(--SP, x);
		}
		//function to run the instruction that was retrieved from memory and was decoded in the IR
		//the execute portion of the instruction cycle
		private boolean execute() throws InterruptedException, IOException {
			
			//check the timer
			if(timer >= timerLength) {
				
				//if timer has completed
				//execute the timer interrupt/system call by doing a system call
				IR = 29;
				PC = timerInterrupt;
				
			} else {
				
				read(PC++);
			}
			//get the decoded IR to execute the instruction
			switch(IR) {
					
				//load instruction
				case 1:
					
					//fetch();
					AC = read(PC++);
					//AC = IR;
					break;
					
				//load address instruction
				case 2:
					
					//fetch();
					//read(PC++);
					//read(IR);
					AC = read(read(PC++));
					break;
					
				//load value from given address instruction
				case 3:
					
					//fetch();
					//read(IR);
					//read(IR);
					AC = read(read(PC++));
					break;
					
				//load the value at the address instruction
				case 4:
					
					//fetch();
					//read(PC++);
					AC = read(read(PC++)+ X);
					//AC = IR;
					break;
					
				//load the value at the address instruction
				case 5:
					
					//fetch();
					//read(PC++);
					AC = read(read(PC++) + Y);
					//AC = IR;
					break;
					
				//load SP + X instruction
				case 6:
					
					AC = read(SP + X);
					//AC = IR;
					break;
					
				//store instruction
				case 7:
					
					//fetch();
					AC = read(PC++);
					write(IR, AC);
					break;
					
				//give a random integer instruction
				case 8:
					
					Random random = new Random();
					int r = random.nextInt(100);
					AC = r;
					//PC++;
					break;
					
				//write AC as a certain variable instruction
				case 9:
					
					//fetch();
					IR = read(PC++);
					
					//if one, write as an integer
					if(IR == 1) {
						
						System.out.println(AC);
					}
					//if two, write as a character
					if (IR == 2) {
						
						System.out.println((char)AC);
					}
					break;
					
				//add X to AC instruction
				case 10:
					
					AC = AC + X;
					break;
					
				//add Y to AC instruction
				case 11:
					
					AC = AC + Y;
					break;
					
				//subtract X from AC instruction
				case 12:
					
					AC = AC - X;
					break;
				
				//subtract Y from AC instruction
				case 13:
					
					AC = AC - Y;
					break;
					
				//copy AC to X instruction
				case 14:
					
					X = AC;
					break;
					
				//copy X to AC instruction
				case 15:
					
					AC = X;
					break;
				
				//copy AC to Y instruction
				case 16:
					
					Y = AC;
					break;
				
				//copy Y to AC instruction
				case 17:
					
					AC = Y;
					break;
					
				//copy AC to SP instruction
				case 18:
					
					SP = AC;
					break;
					
				//copy SP to AC instruction
				case 19:
					
					AC = SP;
					break;
					
				//jump instruction
				case 20:
					
					//fetch();
					//read(PC++);
					PC = read(PC++);
					//PC = IR;
					break;
					
				//jump if the AC = 0 instruction
				case 21:
					
					//fetch();
					//AC = read(PC++);
					
					//if the AC is zero
					if(AC == 0) {
						
						//jump
						PC = IR;
					}
					break;
				
				//jump if the AC != 0 instruction
				case 22:
					
					//fetch();
					//read(PC++);
					
					//if the AC is not 0
					if(AC != 0) {
						
						PC = IR;
					}
					break;
				
				//push return address and jump to address instruction
				case 23:
					
					//fetch();
					//SP--;
					write(--SP, PC);
					read(PC++);
					//push(PC);
					PC = IR;
					break;
					
				//pop return address and jump to address instruction
				case 24:
					
					//fetch();
					PC = read(SP++);
					PC = IR;
					//PC = pop();
					break;
					
				//increment X instruction
				case 25:
					
					X++;
					break;
					
				//decrement X instruction
				case 26:
					
					X--;
					break;
				
				//push AC instruction
				case 27:
					
					//push(AC);
					write(--SP, AC);
					break;
					
				//pop AC instruction
				case 28:
					
					//AC = pop();
					AC = read(AC++);
					break;
				
				//do system call instruction
				case 29:
					
					//set the hold to the stack pointer
					int hold = SP;
					
					//set the stack pointer to the system
					SP = system;
					
					//to save the previous system state, push all registers to the stack
					push(PC);
					//push(IR);
					push(AC);
					push(X);
					push(Y);
					push(hold);
					
					//the system is now in kernel mode
					currentMode = true;
					
					PC = intInterrupt;
					
					break;
					
				//return from system call instruction
				case 30:
					
					PC = pop();
					//IR = pop();
					AC = pop();
					SP = pop();
					X = pop();
					Y = pop();
					IR = pop();
					
					currentMode = false;
					break;
					
				//end execution instruction
				case 50:
					
					end();
					return false;
					
				//if the instruction does not exist
				default:
					
					System.err.println("Error. Instruction does not exist. Enter a valid instruction.");
					return false;
			}
			//used to determine if all instructions have been executed
			return true;
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		
		//run the other java program, which will be the main memory
		Runtime run = Runtime.getRuntime();
		Process pr = run.exec("java Memory");
		
		//the pipes - main memory to CPU
		//the other is from CPU to main memory
		InputStream in = pr.getInputStream();
		
		OutputStream out = pr.getOutputStream();
		
		//use to write to main memory
		PrintWriter output = new PrintWriter(out);
		Scanner inp = new Scanner(in);
		
		//get file name from command line
		String fileName = args[0];
		File file = new File(fileName);
		
		//send the input file given by user to memory for reading
		output.printf(fileName + "\n");
		output.flush();
		
		//get the timer length from command line
		int te = Integer.parseInt(args[1]);
		//int te = 10;
		int timerLength = te;
		int timer = 0;
		
		Processor instruct = new Processor(in, inp, output, timerLength);
		boolean cont = true;
		
		while(cont) {
			
			//get the instruction 
			instruct.fetch();
			
			timer++;
			//System.out.println("here");
			
			//execute the instruction
			cont = instruct.execute();
			//System.out.println("here");
			
		}
		
	}

}
