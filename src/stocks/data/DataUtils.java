package stocks.data;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateUtils;


public class DataUtils {
	public static List<Data> getByDate(List<Data> list, Date date) {
		List<Data> result = new ArrayList<Data>(); 
		for (Iterator<Data> iterator = list.iterator(); iterator.hasNext();) {
			Data data = (Data) iterator.next();
			if (data.getDate().equals(date)) {
				result.add(data);
			}
		}
		return result;
	}

	public static Data getOneByDate(List<Data> list, Date date) {
		List<Data> byDate = getByDate(list, date);
		return byDate.isEmpty() ? null : byDate.get(0);
	}
	
	public static List<Data[]> matchByDate(List<Data> usePrevWhenNull, List<Data> mayBeNull) {
		Date date1 = usePrevWhenNull.get(0).getDate();
		Date date2 = mayBeNull.get(0).getDate();
		Date start = date1;
		if (date1.after(date2))
			throw new IllegalStateException();
		
		date1 = usePrevWhenNull.get(usePrevWhenNull.size() - 1).getDate();
		date2 = mayBeNull.get(mayBeNull.size() - 1).getDate();
		Date end = date1;
		if (date1.before(date2))
			end = date2;

		long diff = end.getTime() - start.getTime();
		int days = (int) (diff / (1000 * 60 * 60 * 24));
		days++;

		List<Data[]> result = new ArrayList<Data[]>(days);
		for (int i = 0; i < days; i++) {
			Date d = DateUtils.addDays(start, i);
			Data data1 = getOneByDate(usePrevWhenNull, d);
			if (data1 == null) {
				// use previous
				Data previous = result.get(result.size() - 1)[0];
				data1 = new Data(d, previous.getValue(), previous.getName());
			}
			List<Data> data2 = getByDate(mayBeNull, d);
			if (data2.isEmpty()) {
				result.add(new Data[] {data1, null});
			} else {
				for (Iterator<Data> iterator = data2.iterator(); iterator.hasNext();) {
					Data data = (Data) iterator.next();
					result.add(new Data[] {data1, data});
				}
			}
		} 
		return result;
	}

	public static QuickStats computeDiscount(List<Data[]> matched) {
		if (matched.isEmpty())
			return null;
		float lowest = 0;
		float last = 0;
		List<Float> discounts = new ArrayList<Float>();
		List<Float> discountsLowerThan1 = new ArrayList<Float>();
		for (Iterator<Data[]> it = matched.iterator(); it.hasNext();) {
			Data[] datas = (Data[]) it.next();
			if (datas[1] != null) {
				float discount = datas[1].getValue() / datas[0].getValue();
				last = discount;
				if (lowest == 0 || discount < lowest)
					lowest = discount;
				discounts.add(Float.valueOf(discount));
				if (discount < 1)
					discountsLowerThan1.add(Float.valueOf(discount));
			}
		}
		float median = getMedian(discounts);
		float medianLowerThan1 = getMedian(discountsLowerThan1);
		return new QuickStats(lowest, median, medianLowerThan1, last);
	}

	private static float getMedian(List<Float> floats) {
		if (floats.isEmpty())
			return 0;
		Collections.sort(floats);
		if (floats.size() % 2 == 0) {
			float f1 = floats.get(floats.size() / 2 - 1);
			float f2 = floats.get(floats.size() / 2);
			return (f1 + f2) / 2;
		} else {
			return floats.get(floats.size() / 2);
		}
	}
	
	public static int getVolume(String v) {
		v = v.replaceAll(",", "");
		char c = v.charAt(v.length()-1);
		int m = 1;
		switch (c) {
			case 'k' :
				m = 1000;
				v = v.substring(0, v.length()-1);
				break;
			case 'm' :
				m = 1000000;
				v = v.substring(0, v.length()-1);
				break;
			default :
				break;
		}
		float f = Float.parseFloat(v);
		f = f * m;
		return (int) f;
	}
	
	public static Date weekBefore(final Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, -7);
		return cal.getTime();
	}

	public static Calendar adjustTimezone(Calendar cal) {
		Date date = cal.getTime();
		TimeZone tz = cal.getTimeZone();

		// returns the number of ms since January 1, 1970, 00:00:00 GMT
		long msFromEpochGmt = date.getTime();

		// gives you the current offset in ms from GMT at the current date
		int offsetFromUTC = tz.getOffset(msFromEpochGmt);

		// creates a new calendar in GMT, set to this date and add the offset
		Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		gmtCal.setTime(date);
		gmtCal.add(Calendar.MILLISECOND, offsetFromUTC);

		return gmtCal;
	}
}
