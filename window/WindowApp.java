package ssii.pai_1.window;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.UIManager;

import ssii.pai_1.Configuration;

public class WindowApp {
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		JPasswordField pwd = new JPasswordField(10);
		int action = JOptionPane.showConfirmDialog(null, pwd, "Enter Password", JOptionPane.OK_CANCEL_OPTION);

		if(action == JOptionPane.YES_NO_OPTION) {
			Configuration conf = new Configuration();
			if(conf.is_now_reading) {
				JOptionPane.showMessageDialog(null, "The file is reading, wait some seconds...");
				return;
			}
			
			if("Passw0rd".equals(new String(pwd.getPassword()))) {
				PaiWindow window = new PaiWindow();
				window.setVisible(true);
			} else {
				JOptionPane.showMessageDialog(null, "Password are not valid");
			}
		}
	}

}
