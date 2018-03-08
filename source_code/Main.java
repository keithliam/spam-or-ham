package bagofwords;

public class Main{
	public static void main(String[] args){
		SpamFilter filter = new SpamFilter(2, 50);
		GUI window = new GUI(filter);
	}
}