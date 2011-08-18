package stocks.data;
import java.util.Date;

public class StooqCurrentData extends Data {

	private float open;
	private int volume;
	private float bid;
	private float ask;

	public StooqCurrentData(Date date, float value, String name) {
		super(date, value, name);
	}

	public void setOpen(float open) {
		this.open = open;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public void setBid(float bid) {
		this.bid = bid;
	}

	public void setAsk(float ask) {
		this.ask = ask;
	}

	public float getOpen() {
		return open;
	}

	public int getVolume() {
		return volume;
	}

	public float getAsk() {
		return ask;
	}

	public float getBid() {
		return bid;
	}

}
