package pack;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Insets;

public class Terminal {

	private JFrame frmChangeDir;
	private JTextField dest;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Terminal window = new Terminal();
					window.frmChangeDir.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Terminal() {
		 try {
			 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		 }catch(Exception ex) {
			 ex.printStackTrace();
		 }
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmChangeDir = new JFrame();
		frmChangeDir.setTitle("Change DIR");
		frmChangeDir.setResizable(false);
		frmChangeDir.setBounds(100, 100, 836, 500);
		frmChangeDir.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmChangeDir.getContentPane().setLayout(null);
		
		dest = new JTextField();
		dest.setMargin(new Insets(2, 10, 2, 2));
		dest.setBounds(576, 182, 236, 43);
		frmChangeDir.getContentPane().add(dest);
		dest.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 556, 223);
		frmChangeDir.getContentPane().add(scrollPane);
		
		JTextArea output = new JTextArea();
		output.setEditable(false);
		output.setMargin(new Insets(2, 10, 2, 2));
		scrollPane.setViewportView(output);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 243, 556, 210);
		frmChangeDir.getContentPane().add(scrollPane_1);
		
		JTextArea output2 = new JTextArea();
		output.setEditable(false);
		output.setMargin(new Insets(2, 10, 2, 2));
		scrollPane_1.setViewportView(output2);
		
		JLabel lblEnterDestination = new JLabel("Enter Destination");
		lblEnterDestination.setHorizontalAlignment(SwingConstants.CENTER);
		lblEnterDestination.setFont(new Font("Times New Roman", Font.BOLD, 18));
		lblEnterDestination.setBounds(576, 144, 166, 28);
		frmChangeDir.getContentPane().add(lblEnterDestination);
		
		JButton btnChangeDir = new JButton("Change DIR");
		btnChangeDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean isWindows = System.getProperty("os.name")
						.toLowerCase().startsWith("windows");
				
				try {
					JFileChooser chooser = new JFileChooser();
					if (dest.getText().equals("")) {
						chooser.setCurrentDirectory(new java.io.File("."));
					}
					else {
						chooser.setCurrentDirectory(new java.io.File(dest.getText()));
					}
					chooser.setDialogTitle("Open Directory");
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					chooser.setAcceptAllFileFilterUsed(true);
					
					if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					  System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
					  System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
					} else {
					  System.out.println("No Selection ");
					}
					
					output2.setText("");
					String destination = chooser.getSelectedFile().toString();
					dest.setText(chooser.getSelectedFile().toString());
					destination = destination.replace("\\", "\\\\");
					ProcessBuilder builder = new ProcessBuilder();
					
					builder.directory(new File(destination));
					if (isWindows) {
					    builder.command("cmd.exe", "/c", "dir");
					} else {
					    builder.command("sh", "-c", "ls");
					}
					
					Process process = builder.start();
					StreamGobbler streamGobbler = 
					new StreamGobbler(process.getInputStream(), output);		
					Executors.newSingleThreadExecutor().submit(streamGobbler);
					
					int exitCode = process.waitFor();
					assert exitCode == 0;
				} 
				catch (NullPointerException nu) {
				}
				catch (Exception e) {
					e.printStackTrace();
					output2.setText("Invalid Directory");
				}
			}
		});
		btnChangeDir.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		btnChangeDir.setBounds(636, 235, 120, 43);
		frmChangeDir.getContentPane().add(btnChangeDir);
		
		JButton btnGitStatus = new JButton("Git Status");
		btnGitStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean isWindows = System.getProperty("os.name")
						.toLowerCase().startsWith("windows");
				
				try {
					output2.setText("");
					String destination = dest.getText();
					destination = destination.replace("\\", "\\\\");
					ProcessBuilder builder = new ProcessBuilder();
					
					builder.directory(new File(destination));
					if (isWindows) {
					    builder.command("cmd.exe", "/c", "git status");
					} else {
					    builder.command("sh", "-c", "git status");
					}
					
					Process process = builder.start();
					StreamGobbler streamGobbler;
					InputStream ip = process.getErrorStream();
					streamGobbler = new StreamGobbler(ip, output2);
					Executors.newSingleThreadExecutor().submit(streamGobbler);
					int exitCode = process.waitFor();
					assert exitCode == 0;
					process.destroy();
					
					process = builder.start();
					streamGobbler = new StreamGobbler(process.getInputStream(), output);
					Executors.newSingleThreadExecutor().submit(streamGobbler);
					exitCode = process.waitFor();
					assert exitCode == 0;
					process.destroy();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnGitStatus.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnGitStatus.setBounds(636, 288, 120, 43);
		frmChangeDir.getContentPane().add(btnGitStatus);
	}
}
