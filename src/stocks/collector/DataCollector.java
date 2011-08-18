package stocks.collector;

import java.util.List;

import stocks.data.Data;

public abstract class DataCollector {
	public abstract List<Data> collectData();
}
