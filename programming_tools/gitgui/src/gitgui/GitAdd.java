/**
 * Class Implementing Git Add functionality
 *
 * @author Sabyasachi Mishra
 * @date 11/11/2018
 *
 */

package gitgui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;

public class GitAdd extends JFrame {

	private static final long serialVersionUID = 1L; //Serial ID for this JFrame
	private JPanel contentPane; //Frame body
	private JTextField textField; //Text field to get specific file names
	private PrintStream printstream; //PrintStream object; used to direct console output
	private JFrame frame; //JFrame instance
	private String total[], directory; //Total files to be added; directory where command needs to be carried out
	
	/**
	 * Create the frame.
	 */
	public GitAdd(String directory) {
		setTitle("Git GUI");
		try {
			frame = this;
			this.directory = directory;
			printstream = new PrintStream(new FileOutputStream(new File("./src/logs/GitAdd-"+System.currentTimeMillis()+".txt")), true);
			System.setOut(printstream);
		} catch(Exception e) {
			e.printStackTrace();
		}
		initialize();
	}
	
	//Initializing the JFrame Contents
	private void initialize() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 480, 213);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		frame.setLocationRelativeTo(null);
		
		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textField.setForeground(Color.BLACK);
		textField.setBounds(10, 56, 446, 41);
		contentPane.add(textField);
		textField.setColumns(10);
		
		PromptSupport.setPrompt("Separate files by ;", textField);
		PromptSupport.setFocusBehavior(PromptSupport.FocusBehavior.SHOW_PROMPT, textField);
		
		//Button to implement tracking of specific files
		JButton btnAdd = new JButton("Add");
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				total = textField.getText().split(";");
				
				String add = "";
				for (String x : total) {
					add += x + " ";
				}
				
				boolean isWindows = System.getProperty("os.name")
						.toLowerCase().startsWith("windows");
				try {				
					ProcessBuilder builder = new ProcessBuilder();
					builder.directory(new File(directory)); 
					if (isWindows) {
						builder.command("cmd", "/c", "git add " + add);
					}
					else {
						builder.command("sh", "-c", "git add " + add);
					}
					
					Process process = builder.start();
					int exitCode = process.waitFor();
					JOptionPane.showMessageDialog(null, "All files added for tracking", "Git GUI", 1);
					assert exitCode == 0;
					process.destroy();
					frame.dispose();
				} catch (Exception e) {
					e.printStackTrace(printstream);
				}
			}
		});
		btnAdd.setBounds(63, 126, 85, 40);
		contentPane.add(btnAdd);
		
		//Button to add all files for tracking
		JButton btnAddAll = new JButton("Add All");
		btnAddAll.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnAddAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean isWindows = System.getProperty("os.name")
						.toLowerCase().startsWith("windows");
				try {				
					ProcessBuilder builder = new ProcessBuilder();
					builder.directory(new File(directory)); 
					if (isWindows) {
						builder.command("cmd", "/c", "git add .");
					}
					else {
						builder.command("sh", "-c", "git add .");
					}
					
					Process process = builder.start();
					int exitCode = process.waitFor();
					JOptionPane.showMessageDialog(null, "All files added for tracking", "Git GUI", 1);
					assert exitCode == 0;
					process.destroy();
					frame.dispose();
				} catch (Exception e) {
					e.printStackTrace(printstream);
				}
			}
		});
		btnAddAll.setBounds(198, 126, 85, 40);
		contentPane.add(btnAddAll);
		
		//Button to cancel operation
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		btnCancel.setBounds(333, 126, 85, 40);
		contentPane.add(btnCancel);
		
		JLabel lblAddFiles = new JLabel("Add files");
		lblAddFiles.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblAddFiles.setBounds(10, 23, 85, 23);
		contentPane.add(lblAddFiles);
	}
}