package stocks.collector;

import java.util.List;

import stocks.data.Data;

public abstract class DataCollector {
	final public List<Data> collectData() {
		return collectData(false);
	}

	public abstract List<Data> collectData(boolean includeStart);
}
