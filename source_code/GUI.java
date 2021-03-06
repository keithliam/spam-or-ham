package bagofwords;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Scanner;

import javax.swing.table.DefaultTableModel;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;

public class GUI {
	private SpamFilter filter;
	private int index = 0, move;
	private char character;
	private File spamPath;
	private File hamPath;
	private File classifyPath;
	private JLabel totalDicSize, totalNoOfWords, spamNoOfWords, hamNoOfWords, errorText;
	private TextField kValue;
	private DefaultTableModel spamModel, hamModel, classifyModel;
	private final static int SPAM = 1;
	private final static int HAM = 2;
	private final static int CLASSIFY = 3;
	private boolean changedSpamFolder, changedHamFolder;

	public GUI(SpamFilter filter){
		this.filter = filter;
		this.changedSpamFolder = false;
		this.changedHamFolder = false;

		JFrame frame = new JFrame();
		frame.setTitle("Solution");
		frame.setPreferredSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLayout(null);
		final Container container = frame.getContentPane();

		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		frame.setLocation((screenWidth / 2) - 400, (screenHeight / 2) - 275);

		JButton spamButton = new JButton("Select Spam Folder");
		JButton hamButton = new JButton("Select Ham Folder");
		JButton classifyButton = new JButton("Select Classify Folder");
		JButton filterButton = new JButton("Filter");
		spamButton.setBounds(70, 30, 160, 40);
		hamButton.setBounds(320, 30, 160, 40);
		classifyButton.setBounds(570, 70, 160, 40);
		filterButton.setBounds(655, 115, 75, 40);
		frame.add(spamButton);
		frame.add(hamButton);
		frame.add(classifyButton);
		frame.add(filterButton);

		JLabel totalDicSizeLabel = new JLabel("Dictionary Size:");
		JLabel totalDicSize = new JLabel(Integer.toString(filter.getTotalDicSize()));
		this.totalDicSize = totalDicSize;
		totalDicSizeLabel.setBounds(500, 30, 160, 40);
		totalDicSize.setBounds(600, 30, 160, 40);
		frame.add(totalDicSizeLabel);
		frame.add(totalDicSize);

		JLabel totalNoOfWordsLabel = new JLabel("Total Words:");
		JLabel totalNoOfWords = new JLabel(Integer.toString(filter.getTotalNoOfWords()));
		JLabel spamNoOfWordsLabel = new JLabel("Total Words in Spam:");
		JLabel spamNoOfWords = new JLabel(Integer.toString(filter.getSpamNoOfWords()));	
		JLabel hamNoOfWordsLabel = new JLabel("Total Words in Ham:");
		JLabel hamNoOfWords = new JLabel(Integer.toString(filter.getHamNoOfWords()));
		JLabel outputLabel = new JLabel("Output");
		this.totalNoOfWords = totalNoOfWords;
		this.spamNoOfWords = spamNoOfWords;
		this.hamNoOfWords = hamNoOfWords;
		totalNoOfWordsLabel.setBounds(650, 30, 160, 40);
		totalNoOfWords.setBounds(730, 30, 160, 40);
		spamNoOfWordsLabel.setBounds(60, 520, 160, 40);
		spamNoOfWords.setBounds(200, 520, 160, 40);
		hamNoOfWordsLabel.setBounds(315, 520, 160, 40);
		hamNoOfWords.setBounds(450, 520, 160, 40);
		outputLabel.setBounds(550, 150, 160, 40);
		frame.add(totalNoOfWordsLabel);
		frame.add(totalNoOfWords);
		frame.add(spamNoOfWordsLabel);
		frame.add(spamNoOfWords);
		frame.add(hamNoOfWordsLabel);
		frame.add(hamNoOfWords);
		frame.add(outputLabel);

		JLabel errorText = new JLabel();
		errorText.setBounds(555, 520, 200, 40);
		errorText.setForeground(Color.RED);
		this.errorText = errorText;
		frame.add(errorText);  

    	this.spamModel = new DefaultTableModel();
    	this.hamModel = new DefaultTableModel();
    	this.classifyModel = new DefaultTableModel();
		JTable spamTable = new JTable(this.spamModel);
		JTable hamTable = new JTable(this.hamModel);
		JTable classifyTable = new JTable(classifyModel);
		spamModel.addColumn("Word");
		spamModel.addColumn("Frequency");
		hamModel.addColumn("Word");
		hamModel.addColumn("Frequency");
		classifyModel.addColumn("Filename");
		classifyModel.addColumn("Class");
		classifyModel.addColumn("P(Spam)");
		JPanel spamPanel = new JPanel();
    	JPanel hamPanel = new JPanel();
    	JPanel classifyPanel = new JPanel();
        spamTable.setPreferredScrollableViewportSize(new Dimension(250,400));
        hamTable.setPreferredScrollableViewportSize(new Dimension(250,400));
        classifyTable.setPreferredScrollableViewportSize(new Dimension(250,400));
        spamTable.getColumnModel().getColumn(0).setPreferredWidth(130);
        hamTable.getColumnModel().getColumn(0).setPreferredWidth(130);
        classifyTable.getColumnModel().getColumn(0).setPreferredWidth(65);
        classifyTable.getColumnModel().getColumn(1).setPreferredWidth(45);
		spamPanel.setBounds(50, 80, 200, 440);
		hamPanel.setBounds(300, 80, 200, 440);
		classifyPanel.setBounds(550, 180, 200, 340);
		spamPanel.setLayout(new BorderLayout());
		hamPanel.setLayout(new BorderLayout());
		classifyPanel.setLayout(new BorderLayout());
		spamPanel.add(new JScrollPane(spamTable));
		hamPanel.add(new JScrollPane(hamTable));
		classifyPanel.add(new JScrollPane(classifyTable));
		frame.add(spamPanel);
		frame.add(hamPanel);
		frame.add(classifyPanel);

		JLabel kValueLabel = new JLabel("k");
		this.kValue = new TextField();
		kValueLabel.setBounds(580, 115, 10, 40);
		kValue.setBounds(595, 115, 50, 40);
		frame.add(kValueLabel);
		frame.add(kValue);

		frame.pack();
		frame.setVisible(true);

		GUI thisGUI = this;
		spamButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				thisGUI.chooseFolder(thisGUI.SPAM);
				thisGUI.changedSpamFolder = true;
			}
		});
		hamButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				thisGUI.chooseFolder(thisGUI.HAM);
				thisGUI.changedHamFolder = true;
			}
		});
		classifyButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				thisGUI.chooseFolder(thisGUI.CLASSIFY);
			}
		});
		filterButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String k = thisGUI.kValue.getText();
				if(thisGUI.spamPath != null && thisGUI.hamPath != null && thisGUI.classifyPath != null && !k.isEmpty() && k.matches("\\d*(\\.\\d+)?")){
					if(!thisGUI.changedSpamFolder && !thisGUI.changedHamFolder) thisGUI.filter.filter(null, null, thisGUI.classifyPath, Float.parseFloat(k));
					else if(!thisGUI.changedHamFolder) thisGUI.filter.filter(thisGUI.spamPath, null, thisGUI.classifyPath, Float.parseFloat(k));
					else if(!thisGUI.changedSpamFolder) thisGUI.filter.filter(null, thisGUI.hamPath, thisGUI.classifyPath, Float.parseFloat(k));
					else thisGUI.filter.filter(thisGUI.spamPath, thisGUI.hamPath, thisGUI.classifyPath, Float.parseFloat(k));
					thisGUI.updateWindow();
					thisGUI.changedSpamFolder = false;
					thisGUI.changedHamFolder = false;
					thisGUI.errorText.setText("");
				} else {
					if(thisGUI.spamPath == null) thisGUI.errorText.setText("Please select a Spam Folder.");
					else if(thisGUI.hamPath == null) thisGUI.errorText.setText("Please select a Ham Folder.");
					else if(thisGUI.classifyPath == null) thisGUI.errorText.setText("Please select a Classify Folder.");
					else if(k.isEmpty() || !k.matches("\\d*(\\.\\d+)?")) thisGUI.errorText.setText("Please enter a valid k value.");
				}
			}
		});
	}

	public void chooseFolder(int type){
		String emailType = new String();
		// from https://www.mkyong.com/swing/java-swing-jfilechooser-example/
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setCurrentDirectory(new File("."));
		if(type == this.SPAM) emailType = "Spam";
		else if(type == this.HAM) emailType = "Ham";
		else if(type == this.CLASSIFY) emailType = "Classify";
		jfc.setDialogTitle("Select " + emailType + " Folder");
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.setAcceptAllFileFilterUsed(false);

		if(jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			if(type == this.SPAM) this.spamPath = jfc.getSelectedFile();
			else if(type == this.HAM) this.hamPath = jfc.getSelectedFile();
			else if(type == this.CLASSIFY) this.classifyPath = jfc.getSelectedFile();
		}
	}

	public void updateWindow(){
		this.totalDicSize.setText(Integer.toString(filter.getTotalDicSize()));
		this.totalNoOfWords.setText(Integer.toString(filter.getTotalNoOfWords()));
		this.spamNoOfWords.setText(Integer.toString(filter.getSpamNoOfWords()));
		this.hamNoOfWords.setText(Integer.toString(filter.getHamNoOfWords()));
		this.spamModel.setRowCount(0);
		this.hamModel.setRowCount(0);
		this.classifyModel.setRowCount(0);
		String[][] data = this.filter.getSpamData();
		for(int i = 0; i < data.length; i++){
			this.spamModel.addRow(data[i]);
		}
		data = this.filter.getHamData();
		for(int i = 0; i < data.length; i++){
			this.hamModel.addRow(data[i]);
		}
		data = this.filter.getClassifyData();
		for(int i = 0; i < data.length; i++){
			this.classifyModel.addRow(data[i]);
		}
	}
}