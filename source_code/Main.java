package bagofwords;

public class Main{
	public static void main(String[] args){
		SpamFilter filter = new SpamFilter(50);
		(new CrossValidator(filter, 80, 10, 10)).validate();
		// GUI window = new GUI(filter);
	}
}