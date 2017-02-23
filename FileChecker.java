package ssii.pai_1;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
/**
 * @author Juan José Valle Zarza
 * @version alfa
 * <p>Esta clase contiene métodos que se encarga de la verficación de archivos, 
 * el envio de correo y la generacion de informes</p>
 * */
public class FileChecker {

	public static Set<FileHash> modified = new HashSet<FileHash>();

	/**
	 * <p>Verifica los archivos y mete en <b>modified</b> los archivos que han sido cambiados</p>
	 * */
	public static void verify() {
		modified.clear();
		
		int size = Configuration.current.file.size();
		System.out.println(String.format("reading %d ...", size));
		Set<FileHash> current = new HashSet<FileHash>();
		Utils.loop(Utils.root, current);
		int ac = 0;
		
		for (FileHash e : Configuration.current.file) {
			if (!current.contains(e)) {
				modified.add(e);
			} else {
				ac++;
			}
		}
		
		ReportData report = new ReportData(LocalDate.now(), modified, ac, size);
		Configuration.current.data.add(report);
	}

	/**
	 * <p>Envía un mail a la direccion de correo especificada mediante el correo que se ha configurado en Configuration</p>
	 * @see Configuration
	 * @throws Exception si el correo no se envia correctamente
	 * */
	public static void send_mail() throws Exception {
		if (modified.isEmpty()) {
			return;
		}

		Properties properties = System.getProperties();
		String host = "smtp.live.com";

		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.ssl.trust", host);
		properties.put("mail.smtp.user", Configuration.current.sender_mail);
		properties.put("mail.smtp.password", Configuration.current.sender_pass);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(properties);
		MimeMessage message = new MimeMessage(session);

		message.setFrom(new InternetAddress(Configuration.current.mail));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(Configuration.current.mail));
		message.setSubject("Report of modified files");

		StringBuilder str = new StringBuilder();
		str.append("Report\n\n");

		for (FileHash e : modified) {
			str.append(String.format("• the file %s has modified, the hash are %s and must be %s\n", e.file,
					Utils.hashing(e.file), e.hash)); }

		message.setText(str.toString());

		Transport transport = session.getTransport("smtp");

		transport.connect(host, Configuration.current.sender_mail, Configuration.current.sender_pass);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}

	/**
	 * <p>Genera un informe mensual con los ficheros que han sido modificados durante el mes, agrega ademas el ratio</p>
	 * @throws Exception si el informe no se genera correctamente
	 * */
	public static void create_report() throws Exception {
		LocalDate self = LocalDate.now();
		
		URL location = Configuration.class.getProtectionDomain().getCodeSource().getLocation();
		File file = new File(new File(location.getFile()).getParentFile().getPath() + File.separator + String.format("report%s.pdf", self.toString()));
		
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		
		Paragraph p = new Paragraph(String.format("Report of %s", self.getMonth().name()), new Font(FontFamily.HELVETICA, 22f, Font.BOLD));
		document.add(p);
		document.add(Chunk.NEWLINE);
		
		for(ReportData data : Configuration.current.data) {
			document.add(new Paragraph(String.format("%s", data.data), new Font(FontFamily.HELVETICA, 12f, Font.UNDERLINE)));
			for(FileHash e : data.hash) {
				document.add(new Paragraph(String.format("• The file %s has modified, the hash are %s and must be %s\n", e.file,
						Utils.hashing(e.file), e.hash))); }
			
			document.add(new Paragraph(String.format("Ratio: %.2f", data.succesfully * 1d / data.total_files), new Font(FontFamily.HELVETICA, 12f, Font.BOLD)));
		}
		
		System.out.println("document generated in " + file.getAbsolutePath());
		
		document.close();
	}
}
