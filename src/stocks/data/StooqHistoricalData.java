package stocks.data;
import java.util.Date;

public class StooqHistoricalData extends Data {

	private float open;
	private float high;
	private float low;
	private float close;
	private int volume;

	public StooqHistoricalData(Date date, float open, float high, float low,
			float close, int volume, String name) {
		super(date, -1, name);
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}

	public float getOpen() {
		return this.open;
	}

	public float getHigh() {
		return this.high;
	}

	public float getLow() {
		return this.low;
	}

	public float getClose() {
		return this.close;
	}

	public int getVolume() {
		return this.volume;
	}

	@Override
	public float getValue() {
		return getClose();
	}

}
