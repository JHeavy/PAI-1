package ssii.pai_1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

/**
 * @author Juan José Valle Zarza
 * @version alfa
 * <p>Esta clase contiene la informacion de la configuración y informacion necesaria para generar un informe mensual</p>
 * */
public class Configuration implements Serializable {
	
	private static final long serialVersionUID = 204312874872420962L;
	public static Configuration current = null;
	public String sender_mail;
	public String sender_pass;
	public String mail;
	public String report_file;
	public boolean is_now_reading = false;
	public Set<FileHash> file;
	public Set<ReportData> data;
	
	public Configuration(String mail, String report_file) {
		this.mail = mail;
		this.report_file = report_file;
		this.file = new HashSet<FileHash>();
		this.data = new HashSet<ReportData>();
		current = this;
	}
	
	public Configuration() {
		if(!Utils.config.exists()) {
			this.mail = "mail@mail.com";
			this.report_file = "report.pdf";
			this.sender_mail = "sender@sender.com";
			this.sender_pass = "123456789";
			this.file = new HashSet<FileHash>();
			this.data = new HashSet<ReportData>();
			recreate();
			
			current = this;
			save();
			
			return; }
		
		try {
			File file = new File(Utils.config.getAbsolutePath());
			
			byte[] decodedBytes = Base64.decodeBase64(IOUtils.toByteArray(new FileInputStream(file)));
			Object obj = Utils.byte_to_object(decodedBytes);
			
			Configuration bean = (Configuration) obj;
			
			this.mail = bean.mail;
			this.report_file = bean.report_file;
			this.file = bean.file;
			this.sender_mail = bean.sender_mail;
			this.sender_pass = bean.sender_pass;
			this.data = bean.data;
			
			current = this;
		} catch (Exception e) {
			this.mail = "mail@mail.com";
			this.report_file = "report.pdf";
			this.file = new HashSet<FileHash>();
			this.sender_mail = "sender@sender.com";
			this.sender_pass = "123456789";
			this.data = new HashSet<ReportData>();
			
			recreate();
			
			current = this;
			
			save();
		}
		
		current = this;
	}
	
	/**
	 * Recarga la configuracion a partir del fichero binario
	 * */
	public static void reload() {
		byte[] decodedBytes;
		try {
			URL location = Configuration.class.getProtectionDomain().getCodeSource().getLocation();
			File file = new File(new File(location.getFile()).getParentFile().getPath() + File.separator + Utils.config.getName());
			decodedBytes = Base64.decodeBase64(IOUtils.toByteArray(new FileInputStream(file)));
			Object obj = Utils.byte_to_object(decodedBytes);
			Configuration bean = (Configuration) obj;
			
			Configuration.current = bean;
		} catch (Exception e) {
			Configuration conf = new Configuration();
			conf.recreate();
			Configuration.current = conf;
			Configuration.current.save();
			
			e.printStackTrace();
		}
	}
	
	/**
	 * Guarda el estado de la Configuracion en un fichero binario encriptado
	 * */
	public void save() {
		try {
			byte[] bytes = Base64.encodeBase64(Utils.obj_to_bytes(this));
			IOUtils.write(bytes, new FileOutputStream(Utils.config));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reasigna a cada fichero el hash que tengan en ese momento
	 * */
	public void recreate() {
		file.clear();
		Utils.loop(Utils.root, file);
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + ((mail == null) ? 0 : mail.hashCode());
		result = prime * result + ((report_file == null) ? 0 : report_file.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Configuration other = (Configuration) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (mail == null) {
			if (other.mail != null)
				return false;
		} else if (!mail.equals(other.mail))
			return false;
		if (report_file == null) {
			if (other.report_file != null)
				return false;
		} else if (!report_file.equals(other.report_file))
			return false;
		return true;
	}
	
}
