import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import stocks.data.AllegroData;
import stocks.data.Data;
import stocks.data.DataUtils;


public class AllegroDataTests {
	@Test
	public void testAllegroCoinsDataSort() {
		List<AllegroData> data = new ArrayList<AllegroData>();
		AllegroData a = new AllegroData(new Date(2000, 3, 1), 1f, 4, "a");
		data.add(a);
		AllegroData b = new AllegroData(new Date(2000, 1, 1), 1f, 3, "b");
		data.add(b);
		AllegroData c = new AllegroData(new Date(2000, 1, 1), 1f, 2, "c");
		data.add(c);
		AllegroData d = new AllegroData(new Date(2001, 1, 2), 1f, 5, "d");
		data.add(d);
		Collections.sort(data);
		assertEquals(c, data.get(0));
	}
	
	@Test
	public void testGetByDate() {
		List<Data> data = new ArrayList<Data>();
		AllegroData a = new AllegroData(new Date(2000, 3, 1), 1f, 4, "a");
		data.add(a);
		AllegroData b = new AllegroData(new Date(2000, 1, 1), 1f, 3, "b");
		data.add(b);
		AllegroData c = new AllegroData(new Date(2000, 1, 1), 1f, 2, "c");
		data.add(c);
		AllegroData d = new AllegroData(new Date(2001, 1, 2), 1f, 5, "d");
		data.add(d);
		List byDate = DataUtils.getByDate(data, new Date(2000, 1, 1));
		assertEquals(2, byDate.size());
	}
}
