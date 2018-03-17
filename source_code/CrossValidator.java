package bagofwords;

import java.util.Scanner;
import java.util.Random;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileFilter;

public class CrossValidator {
	private SpamFilter filter;
	private float trainingPercent, tuningPercent, testPercent;

	private HashMap<String, Integer> bagOfHams;
	private HashMap<String, Integer> bagOfSpams;
	private File spamPath, hamPath;
	private File[] spamFiles, hamFiles;
	private boolean[] doneSpams, doneHams;
	private int noOfFiles;
	private int noOfSpamWords, noOfHamWords;
	private float threshold;

	private static boolean IS_HAM = true;
	private static boolean IS_SPAM = false;

	public CrossValidator(SpamFilter filter, float trainingPercent, float tuningPercent, float testPercent){
		if(trainingPercent + tuningPercent + testPercent != 100){ System.out.println("Data allocation percentage of cross validator not equal to 100."); System.exit(0); };
		this.filter = filter;
		this.threshold = this.filter.getThreshold();
		this.trainingPercent = trainingPercent;
		this.tuningPercent = tuningPercent;
		this.testPercent = testPercent;
		this.bagOfHams = new HashMap<String, Integer>();
		this.bagOfSpams = new HashMap<String, Integer>();

		Scanner sc = new Scanner(System.in);
		System.out.print("Enter ham folder path: ");
		String hamFolderPath = sc.nextLine();
		System.out.print("Enter spam folder path: ");
		String spamFolderPath = sc.nextLine();

		this.hamPath = new File(hamFolderPath);
		this.spamPath = new File(spamFolderPath);
	}

	public void validate(){
		if(this.train()){
			float bestK = this.tune(tuningPercent, 90, 500);
			if(bestK >= 0) System.out.printf("An accuracy of %2.2f%% is achieved with k = %2.2f.\n", this.test(testPercent, bestK), bestK);
		}
	}

	public boolean train(){
		try {
			if(this.spamPath.exists() && this.hamPath.exists()){
				FileFilter noHiddenFiles = new FileFilter(){
					@Override
					public boolean accept(File file){
						return !(file.isHidden() || file.getName().charAt(0) == '.');
					}
				};

				this.spamFiles = this.spamPath.listFiles(noHiddenFiles);
				this.hamFiles = this.hamPath.listFiles(noHiddenFiles);
				this.noOfFiles = this.spamFiles.length;
				if(this.spamFiles.length == this.hamFiles.length){
					this.doneSpams = new boolean[this.noOfFiles];
					this.doneHams = new boolean[this.noOfFiles];
					Arrays.fill(this.doneSpams, false);
					Arrays.fill(this.doneHams, false);
					int limit = (int) (this.trainingPercent * this.noOfFiles) / 100;
					for(int i = 0; i < limit; i++){
						this.extractSpamHam(this.getContents(this.getNextFile(this.IS_SPAM)), this.getContents(this.getNextFile(this.IS_HAM)));
					}
					return true;
				} else {
					System.out.println("No. of spam files and ham files are not equal.");
					return false;
				}
			} else {
				if(!spamPath.exists()) System.out.println("Spam folder path does not exist.");
				else System.out.println("Ham folder path does not exist.");
				return false;
			}
		} catch (SecurityException e) {
			System.out.println(e);
			return false;
		}
	}
	
	public float tune(float percent, float targetAccuracy, int limit){
		float accuracy = 0;
		int correctFiles;

		int noOfFiles = (int) (percent * this.noOfFiles) / 100;
		ArrayList<HashMap<String, Integer>> spamFiles = new ArrayList<HashMap<String, Integer>>();
		ArrayList<HashMap<String, Integer>> hamFiles = new ArrayList<HashMap<String, Integer>>();
		for(int i = 0; i < noOfFiles; i++){
			spamFiles.add(this.getBagOfWords(this.getContents(this.getNextFile(this.IS_SPAM))));
			hamFiles.add(this.getBagOfWords(this.getContents(this.getNextFile(this.IS_HAM))));
		}
		this.filter.setValues(this.bagOfSpams, this.bagOfHams, this.noOfSpamWords, this.noOfHamWords, this.noOfFiles, this.noOfFiles);
		for(float smoothingFactor = 0; smoothingFactor < limit; smoothingFactor++){
			// System.out.print("Smoothing Factor: " + smoothingFactor + "\t");
			correctFiles = 0;
			for(HashMap<String, Integer> bagOfWords : spamFiles){
				this.filter.setNewEmail(bagOfWords, smoothingFactor);
				if(this.filter.getEmailSpamProbability() >= this.threshold) correctFiles++;
			}
			for(HashMap<String, Integer> bagOfWords : hamFiles){
				this.filter.setNewEmail(bagOfWords, smoothingFactor);
				if(this.filter.getEmailSpamProbability() < this.threshold) correctFiles++;
			}

			accuracy = (float) (correctFiles * 100) / (noOfFiles * 2);
			// System.out.printf("\tAccuracy: %2.2f\n", accuracy);
			if(accuracy >= targetAccuracy) return smoothingFactor;
		}
		System.out.println("Target accuracy not reached with the given smoothing factor limit.");
		return -1;
	}

	private float test(float percent, float smoothingFactor){
		int noOfFiles = (int) (percent * this.noOfFiles) / 100;
		int correctFiles = 0;
		ArrayList<HashMap<String, Integer>> spamFiles = new ArrayList<HashMap<String, Integer>>();
		ArrayList<HashMap<String, Integer>> hamFiles = new ArrayList<HashMap<String, Integer>>();
		for(int i = 0; i < noOfFiles; i++){
			spamFiles.add(this.getBagOfWords(this.getContents(this.getNextFile(this.IS_SPAM))));
			hamFiles.add(this.getBagOfWords(this.getContents(this.getNextFile(this.IS_HAM))));
		}
		for(HashMap<String, Integer> bagOfWords : spamFiles){
			this.filter.setNewEmail(bagOfWords, smoothingFactor);
			if(this.filter.getEmailSpamProbability() >= this.threshold) correctFiles++;
		}
		for(HashMap<String, Integer> bagOfWords : hamFiles){
			this.filter.setNewEmail(bagOfWords, smoothingFactor);
			if(this.filter.getEmailSpamProbability() < this.threshold) correctFiles++;
		}
		return (float) (correctFiles * 100) / (noOfFiles * 2);
	}

	private String getContents(File filepath){
		String file = new String();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filepath));
			String line = new String();
			while((line = reader.readLine()) != null){
				file = file.concat(line + " ");
			}
		} catch(Exception e){
			System.out.println(e.getMessage());
		}
		return file;
	}

	private File getNextFile(boolean isHam){
		int num;
		boolean exists;
		Random rand = new Random();
		do {
			num = rand.nextInt(this.noOfFiles);
			exists = (isHam)? this.doneHams[num] : this.doneSpams[num];
		} while(exists);
		if(isHam) this.doneHams[num] = true;
		else this.doneSpams[num] = true;
		return (isHam)? this.hamFiles[num] : this.spamFiles[num];
	}

	private void extractSpamHam(String spamContents, String hamContents){
		String[] sWords = spamContents.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
		String[] hWords = hamContents.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+");
		this.noOfSpamWords += sWords.length;
		this.noOfHamWords += hWords.length;
		for(int i = 0; i < sWords.length; i++){
			if(this.bagOfSpams.containsKey(sWords[i])) this.bagOfSpams.replace(sWords[i], this.bagOfSpams.get(sWords[i]) + 1);
			else this.bagOfSpams.put(sWords[i], 1);
		}
		for(int i = 0; i < hWords.length; i++){
			if(this.bagOfHams.containsKey(hWords[i])) this.bagOfHams.replace(hWords[i], this.bagOfHams.get(hWords[i]) + 1);
			else this.bagOfHams.put(hWords[i], 1);
		}
	}

	private HashMap<String, Integer> getBagOfWords(String file){
		HashMap<String, Integer> bagOfWords = new HashMap<String, Integer>();
		String[] words;

		words = file.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+"); 

		for(int i = 0; i < words.length; i++){
			if(bagOfWords.containsKey(words[i])) bagOfWords.replace(words[i], bagOfWords.get(words[i]) + 1);
			else bagOfWords.put(words[i], 1);
		}

		return bagOfWords;
	}












}