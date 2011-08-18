package stocks.data;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Data implements Comparable<Data>{

	private Date date;
	private float value;
	private String name;

	public Data(Date date, float value, String name) {
		this.date = date;
		this.value = value;
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public float getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public int compareTo(Data o) {
		return this.date.compareTo(o.getDate());
	}
	
	public String getFormattedDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	public String toCsvString() {
		return Float.toString(getValue()).replace('.', ',');
	}

}
