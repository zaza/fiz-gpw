package stocks.collector.allegro;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import stocks.collector.DataCollector;
import stocks.data.AllegroData;
import stocks.data.Data;
import webapi.IOUtils;

public class AllegroCoinsDataCollector extends DataCollector {

	@Override
	public List<Data> collectData() {
		List<Data> result = new ArrayList<Data>();
		try {
			// TODO: run LostAuctionsImporter
			String[] lines = IOUtils.readLines(new File("../webapi-client/output/merged-raw.csv"));
			for (String line : lines) {
				String[] split = line.split(";");
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date d = df.parse(split[0]);
				float price = Float.parseFloat(split[1].replace(',', '.'));
				int id = (split.length > 2 && split[2] != null) ? Integer
						.parseInt(split[2]) : 0;
				String name = (split.length > 3 && split[3] != null)
						? split[3]
						: null;
				AllegroData data = new AllegroData(d, price, id, name);
				result.add(data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Collections.sort(result);
		return result;
	}
}
