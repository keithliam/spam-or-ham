package bagofwords;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;
import java.util.Arrays;

import java.util.HashMap;
import java.util.Scanner;

public class SpamFilter{
	private HashMap<String, Integer> bagOfHams;
	private HashMap<String, Integer> bagOfSpams;
	private HashMap<String, Integer> bagOfEmail;
	private int hamSize;
	private int spamSize;

	public SpamFilter(){
		EmailIO hamIO = new EmailIO(true);
		EmailIO spamIO = new EmailIO(false);
		this.bagOfHams = hamIO.getHamSpam();
		this.bagOfSpams = spamIO.getHamSpam();
		// hamIO.writeFile();
		// spamIO.writeFile();
		this.hamSize = hamIO.getWordSize();
		this.spamSize = spamIO.getWordSize();
	}

	public void filterAll(){
		EmailIO emailIO = new EmailIO(false);
		String filename, hamOrSpam;
		double emailSpamProbability;

		for(int j = 1; j <= 300; j++){
			if(j < 10) filename = "00" + Integer.toString(j);
			else if(j < 100) filename = "0" + Integer.toString(j);
			else filename = Integer.toString(j);

			this.bagOfEmail = emailIO.getEmail("./classify/" + filename);

			emailSpamProbability = this.getEmailSpamProbability();
			hamOrSpam = (emailSpamProbability <= 0.50)? "HAM" : "SPAM";

			System.out.printf(filename + "\t%s", hamOrSpam);
			System.out.printf("\t%.32f\n", emailSpamProbability);
		}
	}

	private double getEmailSpamProbability(){
		double spamProbability = this.getSpamProbability();
		double hamProbability = this.getHamProbability();
		double numerator = spamProbability;
		double messageSpamProbabality = 1;
		double messageHamProbabality = 1;

		for(String key : this.bagOfEmail.keySet()){
																							// System.out.printf("KEY: " + key + "\n\tNUM: " + numerator + "\n\t\tMUL: %.32f\n", this.getWordSpamProbability(key));
			numerator = numerator * this.getWordSpamProbability(key);
																							// System.out.printf("\t\t\t\tNUM: %.32f", numerator);
																							// System.out.printf("\n\t\t\t\t\t\tMH: %.32f", messageHamProbabality);
																							// System.out.printf("\n\t\t\t\t\t\t\t\tMS: %.32f\n", messageSpamProbabality);

			messageSpamProbabality = messageSpamProbabality * this.getWordSpamProbability(key);
			messageHamProbabality = messageHamProbabality * this.getWordHamProbability(key);
			if(numerator == 0) return 0;
		}

		double denominator = (messageSpamProbabality * spamProbability) + (messageHamProbabality * hamProbability);
		if(denominator == 0) return 0;

		System.out.println("dNUM: " + numerator + "\tDEN: " + denominator + "\tRES: " + ((double) numerator/denominator));

		return (double) numerator / denominator;
	}

	private double getSpamProbability(){
		// System.out.println(this.bagOfSpams.size() + " and " + this.bagOfHams.size());
		// return (double) this.bagOfSpams.size() / (this.bagOfSpams.size() + this.bagOfHams.size());
		return (double) 1 / 2;
	}

	private double getHamProbability(){
		// return (double) this.bagOfHams.size() / (this.bagOfSpams.size() + this.bagOfHams.size());
		return (double) 1 / 2;
	}

	private double getWordSpamProbability(String key){
		if(!this.bagOfSpams.containsKey(key)) return 0;
		return (double) this.bagOfSpams.get(key) / this.spamSize;
	}

	private double getWordHamProbability(String key){
		if(!this.bagOfHams.containsKey(key)) return 0;
		return (double) this.bagOfHams.get(key) / this.hamSize;
	}
}

		