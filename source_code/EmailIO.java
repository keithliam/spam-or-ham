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

public class EmailIO{
	private int wordSize, noOfEmails;
	private boolean isHam;
	private HashMap<String, Integer> bagOfWords = new HashMap<String, Integer>();
	private File path;

	public EmailIO(File path, boolean isHam){
		this.path = path;
		this.isHam = isHam;
		this.noOfEmails = path.list().length;
		this.wordSize = 0;
	}

	public HashMap<String, Integer> getHamSpam(){
		String filename;
		String file = new String();
		String filenumber;
		boolean error;
		int j = 0;

		if(this.isHam) System.out.println("Eating Hams:");
		else System.out.println("Cooking Spams:");

		for(String filepath : this.path.list()){
			j++;
			file = "";
			error = true;
			try{
				BufferedReader reader = new BufferedReader(new FileReader(this.path + "/" + filepath));
				String line = new String();
				while((line = reader.readLine()) != null) {
					file = file.concat(line + " ");
				}
				error = false;
			} catch(FileNotFoundException e){
				System.out.println("\nFile " + this.path + filepath + " not found");
			} catch(Exception e){
				System.out.println(e.getMessage());
			}

			if(!error) this.extractWords(file);

			if(j != 1) this.clear(30);
			this.loadingBar(j, 30);
		}
		System.out.println();
		return this.bagOfWords;
	}

	public HashMap<String, Integer> getEmail(String path){
		String file = new String();

		try{
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = new String();
			while((line = reader.readLine()) != null) {
				file = file.concat(line + " ");
			}
		} catch(FileNotFoundException e){
			System.out.println("File " + path + " not found");
		} catch(Exception e){
			System.out.println(e.getMessage());
		}

		this.bagOfWords.clear();
		this.extractWords(file);

		return this.bagOfWords;
	}

	private void extractWords(String file){
		String[] words;

		words = file.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+"); 
		this.wordSize += words.length;

		for(int i = 0; i < words.length; i++){
			if(this.bagOfWords.containsKey(words[i])) this.bagOfWords.replace(words[i], this.bagOfWords.get(words[i]) + 1);
			else this.bagOfWords.put(words[i], 1);
		}
	}

	public void loadingBar(int num, int length){
		int i;
		int limit = (num * (length - 2)) / this.noOfEmails;
		float percent = (float) (num * 100) / this.noOfEmails;
		System.out.print("[");
		for(i = 1; i < length - 1; i++){
			if(i == limit) System.out.print(">");
			else if(i <= limit) System.out.print("#");
			else System.out.print(" ");
		}
		System.out.printf("] %.2f", percent);
		System.out.print("%");
	}

	public void clear(int length){
		int i;
		for(i = 0; i < (length + 7); i++){
			System.out.print("\b");
		}
	}

	public int getNoOfEmails(){
		return this.noOfEmails;
	}

	public void writeFile(String filename){
		try{
			String file = "Dictionary Size: " + this.bagOfWords.size() + "\nTotal Number of Words: " + this.wordSize;
			for(String key : this.bagOfWords.keySet()){
				file = file + "\n" + key + ": " + this.bagOfWords.get(key);
			}
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			writer.println(file);
			writer.close();
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	public int getNoOfWords(){
		return this.wordSize;
	}

	public int getDicSize(){
		return this.bagOfWords.size();
	}
}