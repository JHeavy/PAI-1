package ssii.pai_1;

import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Juan José Valle Zarza
 * @version alfa
 * <p>Ejecuta el servicio de verificacion una vez cada día</p>
 * */
public class ExecuteThread implements Runnable {
	
	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public ExecuteThread() {
		scheduler.scheduleAtFixedRate(this, 0, 1, TimeUnit.DAYS);
	}
	
	public void run() {
		Configuration.reload();
		
		Configuration.current.is_now_reading = true;
		long ini = System.currentTimeMillis();
		System.out.println(String.format("Verification is now running, %d files to scan --- %s", Configuration.current.file.size(), LocalDate.now().toString()));
		
		FileChecker.verify();
		
		try {
			FileChecker.send_mail();
		} catch (Exception e) {
			System.err.println("Error on send the mail"); }
		
		
		if(LocalDate.now().getDayOfMonth() == 1) {
			try {
				FileChecker.create_report();
			} catch (Exception e) {
				System.err.println("Error on create the report"); } }
		
		Configuration.current.save();
		Configuration.current.is_now_reading = false;
		
		long end = System.currentTimeMillis() -ini;
		System.out.println(String.format("Verification is terminated and we send a mail to %s --- %.2f seconds", Configuration.current.mail, end / 1000d));
		Configuration.current.save();
	}
}
