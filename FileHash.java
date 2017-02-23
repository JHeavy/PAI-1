package ssii.pai_1;

import java.io.File;
import java.io.Serializable;

/**
 * @author Juan José Valle Zarza
 * @version alfa
 * <p>Contiene el metodo para hashear un fichero, se crea una clase FileHash</p>
 * */
public class FileHash implements Serializable {

	private static final long serialVersionUID = -2902851727231550412L;
	public String file;
	public String hash;
	
	public static FileHash create(String file) {
		FileHash res = new FileHash();
		res.file = file;
		try {
			res.hash = Utils.hashing(file);
		} catch (Exception e) {
			e.printStackTrace(); }
		
		return res;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileHash other = (FileHash) obj;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		return true;
	}

	public String toString() {
		return String.format("%s", new File(file).getName());
	}
}
