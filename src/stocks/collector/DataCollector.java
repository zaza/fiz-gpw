package stocks.collector;

import java.util.List;

import stocks.data.Data;

public abstract class DataCollector {
	final public List<Data> collectData() {
		return collectData(false);
	}

	/**
	 * Collects data.
	 *
	 * @param includeStart
	 *            <code>true</code> if data for a start date has to be
	 *            collected. If the date is not there e.g. it was Sunday that
	 *            day, and the parameter is set to <code>true</code> a data for
	 *            a day before the start date will be added to the collection.
	 * @return the collected data
	 */
	public abstract List<Data> collectData(boolean includeStart);
}
