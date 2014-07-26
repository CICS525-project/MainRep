package Testing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileSystemView;

public class FileChooserSample {
	public static void main(String[] args) {
		
		JPopupMenu jp = new JPopupMenu();
		jp.add(new JMenuItem("Menu 1"));
		
		
		JFrame frame = new JFrame("Hello There");
		
		
		FileSystemView fsv = new SingleRootFileSystemView(new File("C:/Watcher"));
		
		//new File DirectoryRestrictedFileSystemView(new File("C:\\"));
		JFileChooser chooser= new JFileChooser("Choose File To Share");
		//chooser.setCurrentDirectory(new File("C:/Watcher"));
		chooser.setMultiSelectionEnabled(true);
		//chooser.setControlButtonsAreShown(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setApproveButtonText("Share Files");
		chooser.setFileSystemView(fsv);
		chooser.updateUI();
		chooser.setComponentPopupMenu(jp);	
		chooser.setCurrentDirectory(new File("C:/Watcher"));
		
		int choice = chooser.showOpenDialog(frame);

		if (choice != JFileChooser.APPROVE_OPTION) return;

		
		File[] chosenFile = chooser.getSelectedFiles();
		for(File f:chosenFile) {
			System.out.println("The name of the files is " + f.getName());
		}
		
		
		JButton fileChooseButton = new JButton("Choose File");
		fileChooseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				
			}}); 
		
		
		frame.add(fileChooseButton);
		frame.setVisible(true);
		
		
		//.addActionListener( new ActionListener() {
		//    public void actionPerformed(ActionEvent e){

		        // File chooser code goes here usually
	//	    }
//	});
		
		

	}	
}
