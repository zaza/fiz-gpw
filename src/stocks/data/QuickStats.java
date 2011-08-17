package stocks.data;

public class QuickStats {
	float lowest;
	float median;
	float medianLowerThan1;
	float last;

	public QuickStats(float lowest, float median, float medianLowerThan1,
			float last) {
		this.lowest = lowest;
		this.median = median;
		this.medianLowerThan1 = medianLowerThan1;
		this.last = last;
	}
	
	public float getLowest() {
		return lowest;
	}
	
	public float getMedian() {
		return median;
	}
	
	public float getMedianLowerThan1() {
		return medianLowerThan1;
	}
	
	public float getLast() {
		return last;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Quick stats:\n");
		sb.append("Lowest:   " + lowest).append("\n");
		sb.append("Median:   " + median).append("\n");
		sb.append("Last:     " + last).append("\n");
		return sb.toString();
	}
}
