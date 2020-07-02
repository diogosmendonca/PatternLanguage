package br.scpl.view;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class Main {
	
	private Main() {}
	
	public static void main(String[] args) {
		CLIView.parserCLI(args);
	}
		
}
