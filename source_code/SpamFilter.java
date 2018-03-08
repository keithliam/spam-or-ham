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

public class SpamFilter {
	private HashMap<String, Integer> bagOfHams;
	private HashMap<String, Integer> bagOfSpams;
	private HashMap<String, Integer> bagOfEmail;
	private EmailIO hamIO, spamIO, emailIO;
	private int noOfUnknownSpam, noOfUnknownHam;
	private int noOfHams, noOfSpams;
	private int smoothingFactor;
	private String[][] emailData;

	public SpamFilter(int smoothingFactor){
		this.smoothingFactor = smoothingFactor;
	}

	public void filter(File spamPath, File hamPath, File classifyPath){
		if(hamPath != null){
			this.hamIO = new EmailIO(hamPath, true);
			this.bagOfHams = this.hamIO.getHamSpam();
			this.noOfHams = this.hamIO.getNoOfEmails();
			this.hamIO.writeFile("ham.txt");
		}
		if(spamPath != null){
			this.spamIO = new EmailIO(spamPath, false);
			this.bagOfSpams = this.spamIO.getHamSpam();
			this.noOfSpams = this.spamIO.getNoOfEmails();
			this.spamIO.writeFile("spam.txt");
		}
		this.emailIO = new EmailIO(classifyPath, false);
		this.filterAll(classifyPath);
	}

	public void filterAll(File classifyPath){
		String filename, hamOrSpam;
		double emailSpamProbability;
		int i = 0;

		this.emailData = new String[classifyPath.list().length][3];

		System.out.println("Classifying Emails:");
		for(String file : classifyPath.list()){
			this.bagOfEmail = emailIO.getEmail(classifyPath + "/" + file);

			this.noOfUnknownSpam = 0;
			this.noOfUnknownHam = 0;
			for(String key : this.bagOfEmail.keySet()){
				if(!this.bagOfSpams.containsKey(key)) this.noOfUnknownSpam++;
				if(!this.bagOfHams.containsKey(key)) this.noOfUnknownHam++;
			}

			emailSpamProbability = this.getEmailSpamProbability();
			hamOrSpam = (emailSpamProbability <= 0.50)? "HAM" : "SPAM";

			this.emailData[i][0] = file;
			this.emailData[i][1] = hamOrSpam;
			this.emailData[i][2] = Float.toString((float) emailSpamProbability);

			i++;
			if(i != 1) this.emailIO.clear(30);
			this.emailIO.loadingBar(i, 30);

			// System.out.printf(file + "\t%s", hamOrSpam);
			// System.out.printf("\t%.32f\n", emailSpamProbability);
			// if(emailSpamProbability <= 0.50) addEmailsToHashMap(true);	// for learning
			// else addEmailsToHashMap(false);
		}
		System.out.println();
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
		return (double) this.noOfSpams / (this.noOfHams + this.noOfSpams);
	}

	private double getHamProbability(){
		return (double) this.noOfHams / (this.noOfHams + this.noOfSpams);
	}

	private double getWordSpamProbability(String key){
		if(!this.bagOfSpams.containsKey(key)) return (double) this.smoothingFactor / (this.spamIO.getNoOfWords() + (this.smoothingFactor * this.noOfUnknownSpam));
		return (double) (this.bagOfSpams.get(key) + this.smoothingFactor) / (this.spamIO.getNoOfWords() + (this.smoothingFactor * this.noOfUnknownSpam));
	}

	private double getWordHamProbability(String key){
		if(!this.bagOfHams.containsKey(key)) return (double) this.smoothingFactor / (this.hamIO.getNoOfWords() + (this.smoothingFactor * this.noOfUnknownHam));
		return (double) (this.bagOfHams.get(key) + this.smoothingFactor) / (this.hamIO.getNoOfWords() + (this.smoothingFactor * this.noOfUnknownHam));
	}

	public int getEmailDicSize(){
		if(this.emailIO == null) return 0;
		else return this.emailIO.getDicSize();
	}

	public int getEmailNoOfWords(){
		if(this.emailIO == null) return 0;
		else return this.emailIO.getNoOfWords();
	}

	public int getHamNoOfWords(){
		if(this.hamIO == null) return 0;
		return this.hamIO.getNoOfWords();
	}

	public int getSpamNoOfWords(){
		if(this.spamIO == null) return 0;
		return this.spamIO.getNoOfWords();
	}

	public String[][] getSpamData(){
		if(this.bagOfSpams.size() == 0) return null;
		String[][] data = new String[this.bagOfSpams.size()][2];
		int i = 0;
		for(String key : this.bagOfSpams.keySet()){
			data[i][0] = key;
			data[i][1] = Integer.toString(this.bagOfSpams.get(key));
			i++;
		}
		return data;
	}

	public String[][] getHamData(){
		String[][] data = new String[this.bagOfHams.size()][2];
		int i = 0;
		for(String key : this.bagOfHams.keySet()){
			data[i][0] = key;
			data[i][1] = Integer.toString(this.bagOfHams.get(key));
			i++;
		}
		return data;
	}

	public String[][] getClassifyData(){
		return this.emailData;
	}
}

		