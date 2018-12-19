/**
 * Class Implementing Git Checkout functionality
 *
 * @author Sourav Upadhya
 * @date 11/11/2018
 *
 */

package gitgui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;

public class GitCheckout extends JFrame {

	private static final long serialVersionUID = -4075373052103424577L; //Serial ID for this JFrame
	private JPanel contentPane; //Frame body
	private String directory; //directory where command needs to be carried out
	private String[] branches; //Array to store local repository branches
	private JFrame frame; //JFrame instance
	private PrintStream printstream; //PrintStream object; used to direct console output
	private JLabel branch_name; //variable to display branch name
	private JTextField textField; //variable to get branch name

	/**
	 * Create the frame.
	 */
	public GitCheckout(String directory, JLabel branch_name) {
		this.directory=directory;
		this.branch_name = branch_name;
		
		setTitle("Git GUI");
		try {
			this.directory = directory;
			frame = this;
			printstream = new PrintStream(
					new FileOutputStream(new File("./src/logs/GitCheckout-" + System.currentTimeMillis() + ".txt")), true);
			System.setOut(printstream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initialize();	
	}
	
	//Initializing the JFrame Contents
	private void initialize(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 423, 287);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		frame.setLocationRelativeTo(null);
		
		//Storing branch names
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
			branches = message.split("\n");
			process.destroy();
		} catch (Exception e) {
			e.printStackTrace(printstream);
		}
		
		//Component to display branch names
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
		for (String x : branches) {
			comboBox.addItem(x);
		}
		comboBox.setBounds(76, 64, 259, 30);
		contentPane.add(comboBox);
		
		//Button to implement git checkout function
		JButton btnCheckout = new JButton("Checkout");
		btnCheckout.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnCheckout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean isWindows = System.getProperty("os.name")
						.toLowerCase().startsWith("windows");
				try {
					String status = "", temp;
					
					ProcessBuilder builder = new ProcessBuilder();
					builder.directory(new File(directory)); 
					if (isWindows) {
						if (textField.getText().isEmpty())
							builder.command("cmd", "/c", "git checkout " + comboBox.getSelectedItem().toString());
						else
							builder.command("cmd", "/c", "git checkout " + textField.getText());
					}
					else {
						if (textField.getText().isEmpty())
							builder.command("sh", "-c", "git checkout " + comboBox.getSelectedItem().toString());
						else
							builder.command("sh", "-c", "git checkout " + textField.getText());
					}
					
					Process process = builder.start();
					JOptionPane.showMessageDialog(null, "Branch has been checked out", "Git GUI", 1);
					BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
					while ((temp = reader.readLine()) != null) {
						status += temp;
					}
					
					Pattern p = Pattern.compile("/[a-zA-Z0-9-[!@#$%^&*(),.?\\\":{}|<>_]]+\'");
					Matcher m = p.matcher(status);
					
					if (m.find()) {
						branch_name.setText("On branch " + m.group().substring(1, m.group().length()-1));
					}else {
						System.out.println("Wrong " + status);
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
		btnCheckout.setBounds(76, 189, 109, 30);
		contentPane.add(btnCheckout);	
		
		JLabel lblSelectBranch = new JLabel("Select branch");
		lblSelectBranch.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblSelectBranch.setBounds(76, 35, 118, 19);
		contentPane.add(lblSelectBranch);
		
		//Button to cancel operation
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnCancel.setBounds(226, 189, 109, 30);
		contentPane.add(btnCancel);
		
		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textField.setBounds(76, 128, 259, 30);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblEnterBranch = new JLabel("Enter branch");
		lblEnterBranch.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblEnterBranch.setBounds(76, 104, 118, 19);
		contentPane.add(lblEnterBranch);
	}
}