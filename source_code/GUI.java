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
	private JLabel emailDicSize, emailNoOfWords, spamNoOfWords, hamNoOfWords, errorText;
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
		classifyButton.setBounds(570, 80, 160, 40);
		filterButton.setBounds(570, 130, 160, 40);
		frame.add(spamButton);
		frame.add(hamButton);
		frame.add(classifyButton);
		frame.add(filterButton);

		JLabel emailDicSizeLabel = new JLabel("Dictionary Size:");
		JLabel emailDicSize = new JLabel(Integer.toString(filter.getEmailDicSize()));
		this.emailDicSize = emailDicSize;
		emailDicSizeLabel.setBounds(500, 30, 160, 40);
		emailDicSize.setBounds(600, 30, 160, 40);
		frame.add(emailDicSizeLabel);
		frame.add(emailDicSize);

		JLabel emailNoOfWordsLabel = new JLabel("Total Words:");
		JLabel emailNoOfWords = new JLabel(Integer.toString(filter.getEmailNoOfWords()));
		JLabel spamNoOfWordsLabel = new JLabel("Total Words in Spam:");
		JLabel spamNoOfWords = new JLabel(Integer.toString(filter.getSpamNoOfWords()));	
		JLabel hamNoOfWordsLabel = new JLabel("Total Words in Ham:");
		JLabel hamNoOfWords = new JLabel(Integer.toString(filter.getHamNoOfWords()));
		this.emailNoOfWords = emailNoOfWords;
		this.spamNoOfWords = spamNoOfWords;
		this.hamNoOfWords = hamNoOfWords;
		emailNoOfWordsLabel.setBounds(655, 30, 160, 40);
		emailNoOfWords.setBounds(735, 30, 160, 40);
		spamNoOfWordsLabel.setBounds(60, 520, 160, 40);
		spamNoOfWords.setBounds(200, 520, 160, 40);
		hamNoOfWordsLabel.setBounds(315, 520, 160, 40);
		hamNoOfWords.setBounds(450, 520, 160, 40);
		frame.add(emailNoOfWordsLabel);
		frame.add(emailNoOfWords);
		frame.add(spamNoOfWordsLabel);
		frame.add(spamNoOfWords);
		frame.add(hamNoOfWordsLabel);
		frame.add(hamNoOfWords);

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
				if(thisGUI.spamPath != null && thisGUI.hamPath != null && thisGUI.classifyPath != null){
					thisGUI.errorText.setText("");
					if(!thisGUI.changedSpamFolder && !thisGUI.changedHamFolder) thisGUI.filter.filter(null, null, thisGUI.classifyPath);
					else if(!thisGUI.changedHamFolder) thisGUI.filter.filter(thisGUI.spamPath, null, thisGUI.classifyPath);
					else if(!thisGUI.changedSpamFolder) thisGUI.filter.filter(null, thisGUI.hamPath, thisGUI.classifyPath);
					else thisGUI.filter.filter(thisGUI.spamPath, thisGUI.hamPath, thisGUI.classifyPath);
					thisGUI.updateWindow();
					thisGUI.changedSpamFolder = false;
					thisGUI.changedHamFolder = false;
				} else {
					if(thisGUI.spamPath == null) thisGUI.errorText.setText("Please select a Spam Folder.");
					else if(thisGUI.hamPath == null) thisGUI.errorText.setText("Please select a Ham Folder.");
					else if(thisGUI.classifyPath == null) thisGUI.errorText.setText("Please select a Classify Folder.");
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
		this.emailDicSize.setText(Integer.toString(filter.getEmailDicSize()));
		this.emailNoOfWords.setText(Integer.toString(filter.getEmailNoOfWords()));
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