/**
 * Class Implementing Git merge functionality
 *
 * @author Ronald tony
 * @date 11/11/2018
 *
 */

package gitgui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GitMerge extends JFrame {

	private static final long serialVersionUID = -8696635368838908217L; //Serial ID for this JFrame
	private JPanel contentPane; //Frame body
	private PrintStream printstream; //PrintStream object; used to direct console output
	private JFrame frame; //JFrame instance
	private String directory; //directory where command needs to be carried out
	private String branches[]; //array of branches in local repository

	/**
	 * Create the frame.
	 */
	public GitMerge(String directory) {
		setTitle("Git GUI");
		try {
			frame = this;
			this.directory = directory;
			printstream = new PrintStream(new FileOutputStream(new File("./src/logs/GitMerge-"+System.currentTimeMillis()+".txt")), true);
			System.setOut(printstream);
		} catch(Exception e) {
			e.printStackTrace();
		}
		initialize();
	}

	//Initializing the JFrame Contents
	private void initialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 476, 231);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblFrom = new JLabel("");
		lblFrom.setHorizontalAlignment(SwingConstants.CENTER);
		lblFrom.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblFrom.setBounds(10, 54, 185, 35);
		contentPane.add(lblFrom);
		frame.setLocationRelativeTo(null);
		
		// Getting the branches available in the local repository
		boolean isWindows = System.getProperty("os.name")
				.toLowerCase().startsWith("windows");
		try {				
			ProcessBuilder builder = new ProcessBuilder();
			builder.directory(new File(directory)); 
			if (isWindows) {
				builder.command("cmd", "/c", "git branch");
			}
			else {
				builder.command("sh", "-c", "git branch");
			}
			
			Process process = builder.start();
			int exitCode = process.waitFor();
			assert exitCode == 0;
			
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String message, temp;
			message = "";
			
			while ((temp = br.readLine()) != null) {
				message += temp + "\n";
			}
			br.close();
			process.destroy();
			
			branches = message.split("\n");
			for (int i = 0; i < branches.length; i ++) {
				if (branches[i].contains("*")) {
					branches[i] = branches[i].replace("*", " ");
					lblFrom.setText(branches[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace(printstream);
		}
		
		//Element to show different branches
		JComboBox<String> comboBox_1 = new JComboBox<String>();
		comboBox_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		comboBox_1.setBounds(260, 54, 192, 35);
		contentPane.add(comboBox_1);
		
		for (String x : branches) {
			comboBox_1.addItem(x);
		}
		
		JLabel lblTo = new JLabel("to");
		lblTo.setHorizontalAlignment(SwingConstants.CENTER);
		lblTo.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblTo.setBounds(205, 67, 45, 13);
		contentPane.add(lblTo);
		
		JLabel lblFromBranch = new JLabel("Branch 1");
		lblFromBranch.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblFromBranch.setBounds(10, 30, 115, 13);
		contentPane.add(lblFromBranch);
		
		JLabel lblToBranch = new JLabel("Branch 2");
		lblToBranch.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblToBranch.setBounds(260, 32, 115, 13);
		contentPane.add(lblToBranch);
		
		//Button to implement git merge
		JButton btnMerge = new JButton("Merge");
		btnMerge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean isWindows = System.getProperty("os.name")
						.toLowerCase().startsWith("windows");
				try {
					ProcessBuilder builder = new ProcessBuilder();
					builder.directory(new File(directory)); 
					if (isWindows) {
						builder.command("cmd", "/c", "git merge " + comboBox_1.getSelectedItem().toString() + " " + lblFrom.getText());
					}
					else {
						builder.command("sh", "-c", "git merge " + comboBox_1.getSelectedItem().toString() + " " + lblFrom.getText());
					}
					
					Process process = builder.start();
					if ((new BufferedReader(new InputStreamReader(process.getInputStream())).readLine()) != null) {
						JOptionPane.showMessageDialog(null, "Branches have been merged", "Git GUI", 1);
					}else if ((new BufferedReader(new InputStreamReader(process.getErrorStream())).readLine()) != null) {
						JOptionPane.showMessageDialog(null, "Problem merging branches", "Git GUI", 0);
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
		btnMerge.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnMerge.setBounds(117, 125, 87, 29);
		contentPane.add(btnMerge);
		
		//Button to cancel operation
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnCancel.setBounds(260, 125, 94, 29);
		contentPane.add(btnCancel);
		
		
	}
}
