package ssii.pai_1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Set;

import org.apache.commons.io.IOUtils;

/**
 * @author Juan José Valle Zarza
 * @version alfa
 * <p>Contiene algunos metodos de utilidad para el manejo de la App</p>
 * */
public class Utils {
	
	public static final File root = new File("root");
	public static final File config = new File("config.dat");
	
	public static Object byte_to_object(byte[] byte_arr) throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(byte_arr);
		ObjectInput in = new ObjectInputStream(bis);
		Object o = in.readObject();
		in.close();
		
		return o;
	}
	
	public static byte[] obj_to_bytes(Object obj) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);
		out.writeObject(obj);
		out.flush();
		byte[] arr = bos.toByteArray();
		bos.close();
		
		return arr;
	}
	
	public static void loop(File parent, Set<FileHash> src) {
		if(parent.isDirectory()) {
			for(File e : parent.listFiles()) {
				loop(e, src); }
		} else {
			src.add(FileHash.create(parent.getAbsolutePath()));
		}
	}
	
	public static String hashing(String src) throws Exception {
		File file = new File(src);
		if(!file.exists()) {
			throw new IllegalArgumentException(String.format("File %s not exist", file.getName())); }
		
		MessageDigest msg = MessageDigest.getInstance("SHA-256");
		msg.update(IOUtils.toByteArray(new FileInputStream(file)));
		
		return new BigInteger(1, msg.digest()).toString(16);
	}
}
