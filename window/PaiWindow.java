package ssii.pai_1.window;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ssii.pai_1.Configuration;
import ssii.pai_1.FileHash;

/**
 * @author Juan José Valle Zarza
 * @version alfa
 * <p>Ventana para el manejo y la configuración de la aplicación de verificación de archivos</p>
 * */
public class PaiWindow extends JFrame implements MouseListener {

	private static final long serialVersionUID = 2511705614091417889L;
	private JMenuBar menu;
	private JMenu file, configuration;
	private JMenuItem load, save, close;
	private JMenuItem edit, add_file;
	private JList<FileHash> files;
	private JSplitPane split;
	private JScrollPane scroll;
	private JPanel center;

	public PaiWindow() {
		super.setSize(800, 450);
		super.setTitle("Configuration");
		super.setLocationRelativeTo(null);
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR));
		super.setLayout(new BorderLayout());
		
		this.load = new JMenuItem("load");
		load.addActionListener(e -> load_config());
		this.save = new JMenuItem("save");
		save.setEnabled(false);
		save.addActionListener(e -> save_config());
		this.close = new JMenuItem("close");
		close.addActionListener(e -> getSelf().dispose());
		
		this.file = new JMenu("File");
		file.add(load);
		file.add(save);
		file.addSeparator();
		file.add(close);
		
		this.edit = new JMenuItem("edit");
		edit.addActionListener(e -> edit_config());
		this.add_file = new JMenuItem("add new file");
		add_file.addActionListener(e -> add_new_file());
		
		this.configuration = new JMenu("configuration");
		configuration.setEnabled(false);
		configuration.add(edit);
		configuration.add(add_file);
		
		this.menu = new JMenuBar();
		menu.add(file);
		menu.add(configuration);
		
		this.files = new JList<FileHash>();
		files.setMinimumSize(new Dimension(220, 220));
		files.addMouseListener(this);
		
		this.scroll = new JScrollPane(files);
		scroll.setMinimumSize(new Dimension(220, 220));
		scroll.setFocusable(false);
		
		this.center = new JPanel();
		center.setLayout(new BorderLayout());
		
		this.split = new JSplitPane();
		split.setDividerLocation(220);
		split.setLeftComponent(scroll);
		split.setRightComponent(center);
		
		super.add(menu, BorderLayout.NORTH);
		super.add(split, BorderLayout.CENTER);
	}
	
	void add_new_file() {
		center.removeAll();
		center.add(new ConfigurationFileAdd(), BorderLayout.CENTER);
		
		super.revalidate();
		super.repaint();
	}
	
	void edit_config() {
		center.removeAll();
		center.add(new ConfigurationEdit(), BorderLayout.CENTER);
		center.revalidate();
		center.repaint();
	}
	
	void save_config() {
		Thread hilo = new Thread(() -> {
			getSelf().setTitle("saving data...");
			
			try {
				Configuration.current.save();
			} catch (Exception e) {
				getSelf().setTitle("Error on save"); }
			
			System.out.println(String.format("Configuration file has save and code, %d files on db", Configuration.current.data.size()));
			getSelf().setTitle("Configuration"); });
		
		hilo.start();
	}
	
	private PaiWindow getSelf() {
		return this;
	}
	
	void load_config() {
		Thread hilo = new Thread(() -> {
			getSelf().setTitle("loading data...");
			Configuration config = new Configuration();
			System.out.println(String.format("Configuration file has load and decode, %d files on db", config.data.size()));
			files.setListData(new Vector<FileHash>(config.file));
			
			load.setText("reload");
			save.setEnabled(true);
			configuration.setEnabled(true);
			
			getSelf().setTitle("Configuration"); });
		
		hilo.start();
	}
	
	void viewHash() {
		if(files.getSelectedIndex() > -1) {
			center.removeAll();
			FileHash hash = files.getSelectedValue();
			center.add(new FileHashView(hash));
			center.revalidate();
			center.repaint();
		}
	}

	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2) {
			viewHash();
		}
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}
	
	private class ConfigurationFileAdd extends JPanel implements LayoutManager {
		
		private static final long serialVersionUID = -5021138222460959558L;
		private JButton add;
		private JLabel file_label;
		private JTextField path;
		
		public ConfigurationFileAdd() {
			super.setLayout(this);
			
			this.add = new JButton("add new file");
			add.setFocusPainted(false);
			add.addActionListener(e -> add_file());
			
			this.file_label = new JLabel("Path to file");
			this.path = new JTextField();
			
			super.add(add);
			super.add(file_label);
			super.add(path);
		}
		
		private void add_file() {
			if(path.getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(this, "The file is not valid!");
				return; }
			
			File file = new File(path.getText().trim());
			if(!file.exists()) {
				JOptionPane.showMessageDialog(this, "The file is not exist!");
				return; }
			
			Configuration.current.file.add(FileHash.create(file.getAbsolutePath()));
			files.setListData(new Vector<FileHash>(Configuration.current.file));
			
			center.removeAll();
			
			save_config();
		}

		public void addLayoutComponent(String name, Component comp) {}

		public void layoutContainer(Container parent) {
			file_label.setBounds(5, 5, 100, 25);
			path.setBounds(100, 5, parent.getWidth() -105, 25);
			
			add.setBounds(5, parent.getHeight() - 30, 150, 25);
		}
		
		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(220, 220); }

		public Dimension preferredLayoutSize(Container parent) {
			return new Dimension(220, 220); }

		public void removeLayoutComponent(Component comp) {}
	}
	
	private class ConfigurationEdit extends JPanel implements LayoutManager {

		private static final long serialVersionUID = -5327568063740506799L;
		private JLabel sender_mail_label, password_mail_label, mail_label;
		private JTextField sender_mail_field, mail_field;
		private JPasswordField password_field;
		private JButton save_btn;
		
		public ConfigurationEdit() {
			super.setLayout(this);
			
			this.sender_mail_label = new JLabel("Sender mail");
			this.password_mail_label = new JLabel("Sender password");
			this.mail_label = new JLabel("Mail to send");
			
			this.sender_mail_field = new JTextField(Configuration.current.sender_mail);
			this.mail_field = new JTextField(Configuration.current.mail);
			this.password_field = new JPasswordField(Configuration.current.sender_pass);
			
			this.save_btn = new JButton("Save");
			save_btn.setFocusPainted(false);
			save_btn.addActionListener(e -> save_config());
			
			super.add(sender_mail_label);
			super.add(password_mail_label);
			super.add(mail_label);
			super.add(sender_mail_field);
			super.add(mail_field);
			super.add(password_field);
			
			super.add(save_btn);
		}
		
		private void save_config() {
			Thread hilo = new Thread(() -> {
				getSelf().setTitle("saving data...");
				
				Configuration.current.sender_mail = sender_mail_field.getText();
				Configuration.current.mail = mail_field.getText();
				Configuration.current.sender_pass = new String(password_field.getPassword());
				
				try {
					Configuration.current.save();
				} catch (Exception e) {
					getSelf().setTitle("Error on save");
					return;
				}
				
				System.out.println(String.format("Configuration file has save and code, %d files on db", Configuration.current.data.size()));
				
				center.removeAll();
				getSelf().revalidate();
				getSelf().repaint();
				
				getSelf().setTitle("Configuration"); });
			
			hilo.start();
		}
		
		public void addLayoutComponent(String name, Component comp) {}

		public void layoutContainer(Container parent) {
			int x = 150;
			int x_2 = 200;
			
			sender_mail_label.setBounds(5, 5, x, 25);
			password_mail_label.setBounds(5, 35, x, 25);
			mail_label.setBounds(5, 65, x, 25);
			
			sender_mail_field.setBounds(x, 5, x_2, 25);
			password_field.setBounds(x, 35, x_2, 25);
			mail_field.setBounds(x, 65, x_2, 25);
			
			save_btn.setBounds(5, parent.getHeight() - 30, 150, 25);
		}
		
		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(220, 220); }

		public Dimension preferredLayoutSize(Container parent) {
			return new Dimension(220, 220); }

		public void removeLayoutComponent(Component comp) {}
	}
	
	private class FileHashView extends JPanel implements LayoutManager {

		private static final long serialVersionUID = -8254044986686330348L;
		private FileHash hash;
		private JTextArea text;
		private JButton recreate;
		
		public FileHashView(FileHash hash) {
			super.setLayout(this);
			
			this.hash = hash;
			this.text = new JTextArea();
			text.setEditable(false);
			text.setHighlighter(null);
			text.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			text.setBackground(center.getBackground());
			StringBuilder str = new StringBuilder(hash.toString());
			str.append("\n----------------------------------------\n");
			str.append(String.format("file: %s", hash.file)).append('\n');
			str.append(String.format("hash: %s", hash.hash));
			text.setText(str.toString());
			
			this.recreate = new JButton("recreate");
			recreate.setFocusPainted(false);
			recreate.addActionListener(e -> recreate_hash());
			
			super.add(text);
			super.add(recreate);
		}
		
		private void recreate_hash() {
			super.remove(recreate);
			
			Configuration.current.file.remove(hash);
			hash = FileHash.create(hash.file);
			
			Configuration.current.file.add(hash);
			
			files.setListData(new Vector<FileHash>(Configuration.current.file));
			
			StringBuilder str = new StringBuilder(hash.toString());
			str.append("\n----------------------------------------\n");
			str.append(String.format("file: %s", hash.file)).append('\n');
			str.append(String.format("hash: %s", hash.hash));
			text.setText(str.toString());
			
			super.revalidate();
			super.repaint();
		}

		public void addLayoutComponent(String name, Component comp) {}

		public void layoutContainer(Container parent) {
			text.setBounds(5, 5, parent.getWidth() -10, parent.getHeight() - 35);
			recreate.setBounds(5, parent.getHeight() - 30, 150, 25);
		}

		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(220, 220); }

		public Dimension preferredLayoutSize(Container parent) {
			return new Dimension(220, 220); }

		public void removeLayoutComponent(Component comp) {}
	}
}
