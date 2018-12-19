/**
 * Class Implementing Git status functionality
 *
 * @author Sabyasachi
 * @date 11/11/2018
 *
 */

package gitgui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
// Git status class implementing runnable for thread 
public class GitStatus implements Runnable{
	
	private InputStream ip; // InputStream instance to retrieve status 
	private JTextArea textArea; // TextArea instance for  printing status 
	private PrintStream printstream; // PrintStream object; used to direct console output
	
	// Constructor for initializing the instance variable 
	public GitStatus(InputStream ip, JTextArea textArea) {
		super();
		this.ip = ip;
		this.textArea = textArea;
		try {
			printstream = new PrintStream(new FileOutputStream(new File("./src/logs/GitStatus-"+System.currentTimeMillis()+".txt")), true);
			System.setOut(printstream);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	//Thread for running the status function
	@Override
	public void run() {
		String status = "", temp;
		BufferedReader reader = new BufferedReader(new InputStreamReader(ip));
		
		try {
			while ((temp = reader.readLine()) != null) {
				status += temp;
				status += "\n";
			}
			reader.close();
			
			//regex for taking the important data
			
			Pattern p = Pattern.compile("nothing to commit, working tree clean"); 
			Pattern p1 = Pattern.compile("Changes not staged for commit");
			Pattern p2 = Pattern.compile("Untracked files");
			Pattern p3 = Pattern.compile("Changes to be committed");
			Pattern p4 = Pattern.compile("On branch.+\\n.+\\.");
			Pattern p5 = Pattern.compile("\\(.+\\)");
			Pattern p6 = Pattern.compile("On branch.+\\n");
			
			Matcher m = p.matcher(status);
			Matcher m1 = p1.matcher(status);
			Matcher m2 = p2.matcher(status);
			Matcher m3 = p3.matcher(status);
			Matcher m4 = p4.matcher(status);
			Matcher m5 = p5.matcher(status);
			Matcher m6 = p6.matcher(status);
			
			// if-else ladder to print message 
			
			if (m.find()) {
				JOptionPane.showMessageDialog(null, "Nothing to commit, working tree clean", "Git GUI", 1);
				if (status.contains("git push"))
					textArea.setText("\nLocal commits present to be pushed");
				else
					textArea.setText("");
			}	
			else if (m1.find() || m2.find() || m3.find()) {
				JOptionPane.showMessageDialog(null, "Changes present to be staged/committed", "Git GUI", 1);
				if(m4.find()) {
					status = status.replaceAll(m4.group(), " ");
				}	
				else if (m6.find()) {	
					status = status.replaceAll(m6.group(), " ");
				}
				while(m5.find()) {
					status = status.replaceAll(m5.group(), "");
					status = status.replaceAll("\\(\\)", " ");
					status = status.replaceAll(" \\n", "");
					textArea.setText(status);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace(printstream);
		}
	}

}
