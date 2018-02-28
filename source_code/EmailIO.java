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
	private int wordSize;
	private boolean isHam;
	private HashMap<String, Integer> bagOfWords = new HashMap<String, Integer>();

	public EmailIO(boolean isHam){
		this.isHam = isHam;
	}

	public HashMap<String, Integer> getHamSpam(){
		String filename;
		String file = new String();
		String filenumber;

		for(int j = 1; j <= 300; j++){
			if(j < 10) filenumber = "00" + Integer.toString(j);
			else if(j < 100) filenumber = "0" + Integer.toString(j);
			else filenumber = Integer.toString(j);
			
			filename = (this.isHam)? ("./ham/" + filenumber) : ("./spam/" + filenumber);
			System.out.println(j);

			try{
				BufferedReader reader = new BufferedReader(new FileReader(filename));
				String line = new String();
				while((line = reader.readLine()) != null) {
					file = file.concat(line + " ");
				}
			} catch(FileNotFoundException e){
				System.out.println("File " + filename + " not found");
			} catch(Exception e){
				System.out.println(e.getMessage());
			}

			this.extractWords(file);
		}
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

		this.extractWords(file);

		return this.bagOfWords;
	}

	private void extractWords(String file){
		String[] words;

		words = file.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+"); 
		this.wordSize = words.length;

		for(int i = 0; i < words.length; i++){
			if(this.bagOfWords.containsKey(words[i])) this.bagOfWords.replace(words[i], this.bagOfWords.get(words[i]) + 1);
			else this.bagOfWords.put(words[i], 1);
		}
	}

	public void writeFile(){
		try{
			String file = "Dictionary Size: " + this.bagOfWords.size() + "\nTotal Number of Words: " + this.wordSize;
			for(String key : this.bagOfWords.keySet()){
				file = file + "\n" + key + ": " + this.bagOfWords.get(key);
			}
			PrintWriter writer = (this.isHam)? new PrintWriter("ham.txt", "UTF-8") : new PrintWriter("spam.txt", "UTF-8");
			writer.println(file);
			writer.close();
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	public int getWordSize(){
		return this.wordSize;
	}
}