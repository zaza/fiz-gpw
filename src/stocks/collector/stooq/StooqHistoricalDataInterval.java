package stocks.collector.stooq;

public enum StooqHistoricalDataInterval {
	Daily("d"), Weekly("w"), Monthly("m"), Quarterly("q"), Yearly("y");
	private String internal;

	StooqHistoricalDataInterval(String internal) {
		this.internal = internal;
	}

	@Override
	public String toString() {
		return internal;
	}

}
