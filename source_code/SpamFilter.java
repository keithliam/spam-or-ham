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
	private EmailIO hamIO;
	private EmailIO spamIO;
	private int noOfHams, noOfSpams;
	private final static int smoothingFactor = 2;

	public SpamFilter(){
		this.hamIO = new EmailIO(300, true);
		this.spamIO = new EmailIO(300, false);
		this.bagOfHams = this.hamIO.getHamSpam();
		this.bagOfSpams = this.spamIO.getHamSpam();
		this.noOfHams = this.hamIO.getNoOfEmails();
		this.noOfSpams = this.spamIO.getNoOfEmails();
		this.hamIO.writeFile("ham.txt");
		this.spamIO.writeFile("spam.txt");
	}

	public void filterAll(){
		EmailIO emailIO = new EmailIO(300, false);
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
			if(emailSpamProbability <= 0.50) addEmailsToHashMap(true);
			else addEmailsToHashMap(false);
		}
		emailIO.writeFile("email.txt");
	}

	private void addEmailsToHashMap(boolean isHam){
		for(String key : this.bagOfEmail.keySet()){
			if(isHam){
				this.noOfHams++;
				if(this.bagOfHams.containsKey(key)){
					this.bagOfHams.put(key, this.bagOfHams.get(key) + this.bagOfEmail.get(key));
				} else {
					this.bagOfHams.put(key, this.bagOfEmail.get(key));
				}
			} else if(!isHam){
				this.noOfSpams++;
				if(this.bagOfSpams.containsKey(key)){
					this.bagOfSpams.put(key, this.bagOfSpams.get(key) + this.bagOfEmail.get(key));
				} else {
					this.bagOfSpams.put(key, this.bagOfEmail.get(key));
				}
			}
		}
	}

	private double getEmailSpamProbability(){
		double messageSpamProbability = 1;
		double messageHamProbability = 1;

		for(String key : this.bagOfEmail.keySet()){
			messageSpamProbability = messageSpamProbability * this.getWordSpamProbability(key);
			messageHamProbability = messageHamProbability * this.getWordHamProbability(key);
		}

		double spamProbability = this.getSpamProbability();
		double hamProbability = this.getHamProbability();

		double numerator = messageSpamProbability * spamProbability;
		double denominator;
		if(messageHamProbability == 0) return 1;	// floating point too small
		else denominator = numerator + (messageHamProbability * hamProbability);
		return (double) numerator / denominator;
	}

	private double getSpamProbability(){
		return (double) (this.noOfSpams + this.smoothingFactor) / ((this.noOfHams + this.noOfSpams) + (2 * this.smoothingFactor));
	}

	private double getHamProbability(){
		return (double) (this.noOfHams + this.smoothingFactor) / ((this.noOfHams + this.noOfSpams) + (2 * this.smoothingFactor));
	}

	private double getWordSpamProbability(String key){
		if(!this.bagOfSpams.containsKey(key)) return (double) this.smoothingFactor / (this.spamIO.getNoOfWords() + (this.smoothingFactor * this.spamIO.getDicSize()));
		return (double) (this.bagOfSpams.get(key) + this.smoothingFactor) / (this.spamIO.getNoOfWords() + (this.smoothingFactor * this.spamIO.getDicSize()));
	}

	private double getWordHamProbability(String key){
		if(!this.bagOfHams.containsKey(key)) return (double) this.smoothingFactor / (this.hamIO.getNoOfWords() + (this.smoothingFactor * this.hamIO.getDicSize()));
		return (double) (this.bagOfHams.get(key) + this.smoothingFactor) / (this.hamIO.getNoOfWords() + (this.smoothingFactor * this.hamIO.getDicSize()));
	}
}

		