package bagofwords;

public class Main{
	public static void main(String[] args){
		SpamFilter filter = new SpamFilter(50);
		GUI window = new GUI(filter);
	}
}