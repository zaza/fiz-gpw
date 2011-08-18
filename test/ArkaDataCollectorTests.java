import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import stocks.collector.DataCollector;
import stocks.collector.arka.ArkaDataCollector;
import stocks.data.Data;

public class ArkaDataCollectorTests {

	@Test
	public void testArkafrn12() throws Exception {
		DataCollector invfizInvestorsPl = new ArkaDataCollector(
				"arka-bz-wbk-fundusz-rynku-nieruchomosci-fiz") {
			@Override
			protected InputStream getInput() {
				File file = new File(
						"test/data/arka-bz-wbk-fundusz-rynku-nieruchomosci-fiz.html");
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			};
		};
		List<Data> data = invfizInvestorsPl.collectData();
		assertEquals(26, data.size());

		Data first = data.get(0);
		assertEquals("arka-bz-wbk-fundusz-rynku-nieruchomosci-fiz", first
				.getName());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = df.parse("2004-9-30");
		assertEquals(d, first.getDate());
		assertEquals(98.33f, first.getValue(), 0);
	}

}