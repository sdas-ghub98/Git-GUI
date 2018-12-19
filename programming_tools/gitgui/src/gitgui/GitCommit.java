/**
 * Class implementing Git commit Functionality
 *
 * @author Saloni Jain 
 * @date 11/11/2018
 *
 */

package gitgui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class GitCommit extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1145962955197766911L; // Serial id for JFrame
	private JPanel contentPane; //Frame body
	private JFrame frame; //JFrame instance
	private String directory; //directory where command needs to be carried out
	private PrintStream printstream; //PrintStream object; used to direct console output
	private JTextField textField; //Text field to get commit message
	
	/**
	 * Create the frame.
	 */
	public GitCommit(String directory) {
		setTitle("Git GUI");
		try {
			frame = this;
			this.directory = directory;
			printstream = new PrintStream(new FileOutputStream(new File("./src/logs/GitCommit-"+System.currentTimeMillis()+".txt")), true);
			System.setOut(printstream);
		} catch(Exception e) {
			e.printStackTrace();
		}
		initialize();
	}
	
	//Function to initialize the JFrame
	public void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 411, 246);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		frame.setLocationRelativeTo(null);
		
		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textField.setBounds(48, 62, 298, 30);
		contentPane.add(textField);
		textField.setColumns(10);
		
		//Button implementing git commit function
		JButton btnCommit = new JButton("Commit");
		btnCommit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean isWindows = System.getProperty("os.name")
						.toLowerCase().startsWith("windows");
				try {				
					ProcessBuilder builder = new ProcessBuilder();
					builder.directory(new File(directory)); 
					if (isWindows) {
						builder.command("cmd", "/c", "git commit -m \"" + textField.getText() + "\"");
					}
					else {
						builder.command("sh", "-c", "git commit -m \"" + textField.getText() + "\"");
					}
					
					Process process = builder.start();
					if (process.getInputStream().read() == 'O') {
						JOptionPane.showMessageDialog(null, "Please stage files first", "Git GUI", 1);
					}
					else {
						JOptionPane.showMessageDialog(null, "All files commited", "Git GUI", 1);
					}
					int exitCode = process.waitFor();
					assert exitCode == 0;
					process.destroy();
					frame.dispose();
				} catch (Exception e) {
					e.printStackTrace(printstream);
				}
			}
		});
		btnCommit.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnCommit.setBounds(85, 134, 88, 30);
		contentPane.add(btnCommit);
		
		//Button to cancel operation
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnCancel.setBounds(211, 134, 88, 30);
		contentPane.add(btnCancel);
		
		JLabel lblAddMessageFor = new JLabel("Add message for commit");
		lblAddMessageFor.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblAddMessageFor.setBounds(48, 27, 176, 25);
		contentPane.add(lblAddMessageFor);
	}
}
