package br.scpl.view;

import java.util.Scanner;
import java.util.Stack;

import br.scpl.exception.UnknownCommandException;

public class Client {
		
	public static void main(String[] args) {
		
        Scanner scanner = new Scanner(System.in);
        
        while(true){
            //getting user input
            System.out.println("\nEntre com o comando:");
            String userInput = scanner.nextLine();
            
            String[] splitedInput = userInput.split("[ ]+");

            //preparing data for creating and executing new command
            String commandName = splitedInput[0];
            String[] commandArgs = new String[splitedInput.length-1];
            
            System.arraycopy(splitedInput,1,commandArgs,0,commandArgs.length);

            //executing command
            try {
            	Invoker.invoke(commandName, commandArgs);            	
            } catch (UnknownCommandException e) {
				System.out.println(e.getMessage());
			}
        }
    }
}
