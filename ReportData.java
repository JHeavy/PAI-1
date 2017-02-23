package ssii.pai_1;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Juan José Valle Zarza
 * @version alfa
 * <p>Clase que guarda datos para el informe mensual</p>
 * */
public class ReportData implements Comparable<ReportData>, Serializable {
	
	private static final long serialVersionUID = -3660568103892717090L;
	public Set<FileHash> hash;
	public int succesfully = 0;
	public int total_files = 0;
	public String data;

	@Deprecated
	public ReportData() {}
	
	public ReportData(LocalDate date, Set<FileHash> f, int sucess, int total) {
		this.hash = new HashSet<FileHash>(f);
		this.data = date.toString();
		this.succesfully = sucess;
		this.total_files = total;
	}

	public int compareTo(ReportData o) {
		return LocalDate.parse(data).compareTo(LocalDate.parse(o.data));
	}
	
	public String toString() {
		return String.format("%s:%s", data, hash.toString());
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportData other = (ReportData) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
	
}
