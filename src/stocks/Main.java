package stocks;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import stocks.collector.DataCollector;
import stocks.collector.allegro.AllegroCoinsDataCollector;
import stocks.collector.arka.ArkaDataCollector;
import stocks.collector.investors.InvestorsPlDataCollector;
import stocks.collector.investors.InvestorsPlDataCollector.Fund;
import stocks.collector.stooq.StooqDataCollector;
import stocks.collector.stooq.StooqHistoricalDataInterval;
import stocks.collector.stooq.StooqPageHistoricalDataCollector;
import stocks.data.Data;
import stocks.data.DataUtils;
import stocks.data.QuickStats;
import stocks.excel.Exporter;

public class Main {
	public static void main(String[] args) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar c1 = Calendar.getInstance(); // today

		Date today = new Date(System.currentTimeMillis());
		Date end = DateUtils.truncate(today, Calendar.DAY_OF_MONTH);

		File output = new File("output");
		if (!output.exists())
			output.mkdir();
		
		// === investors.pl
		for (Fund fund : Fund.values()) {
			if (proceed(args, fund.getStooq())) {
				System.out.println("Processing '" + fund + "'...");
				DataCollector invfizInvestorsCollector = new InvestorsPlDataCollector(fund);
				List<Data> invfizPl = invfizInvestorsCollector.collectData();
				Date start = invfizPl.get(0).getDate();
				DataCollector invfiz = new StooqPageHistoricalDataCollector(fund.getStooq(), start, end, StooqHistoricalDataInterval.Daily);
				List<Data> stooqHistData = invfiz.collectData();

				// add latest
				DataCollector latestFromStooq = new StooqDataCollector(fund.getStooq());
				List<Data> stooqData = latestFromStooq.collectData();
				if (stooqData.get(0).getDate().after(stooqHistData.get(stooqHistData.size() - 1).getDate()))
					stooqHistData.add(stooqData.get(0));

				List<Data[]> matched = DataUtils.matchByDate(invfizPl, stooqHistData);

				QuickStats qa = DataUtils.computeDiscount(matched);
				System.out.println(qa.toString());
				System.out.println("=> buy for: "
						+ invfizPl.get(invfizPl.size() - 1).getValue()
						* qa.getLowest() + ", is "
						+ stooqData.get(0).getValue());
				System.out.println("");

				String file = "output/" + fund.getStooq() + "_" + sdf.format(c1.getTime());
				Exporter.toCsvFile(file + ".csv", matched);
				Exporter.toXlsFile(file + ".xls", matched);
			}
		}

		// === silver
		if (proceed(args, "silver")) {
			System.out.println("Processing 'silver'...");
			AllegroCoinsDataCollector allegroCoinsCollector = new AllegroCoinsDataCollector();
			List<Data> allegroCoins = allegroCoinsCollector.collectData();
			Date start = allegroCoins.get(0).getDate();
			DataCollector rcsilaopenHistory = new StooqPageHistoricalDataCollector("rcsilaopen", start, end, StooqHistoricalDataInterval.Daily);
			List<Data> stooqHistData = rcsilaopenHistory.collectData(true);
			if (today.after(stooqHistData.get(stooqHistData.size() - 1).getDate())) {
				// add latest
				DataCollector latestFromStooq = new StooqDataCollector("rcsilaopen");
				List<Data> stooqData = latestFromStooq.collectData();
				stooqHistData.add(stooqData.get(0));
			}

			List<Data[]> matched = DataUtils.matchByDate(stooqHistData, allegroCoins);
			QuickStats qa = DataUtils.computeDiscount(matched);
			System.out.println(qa.toString());
			String file = "output/" + "silver" + "_" + sdf.format(c1.getTime());
			Exporter.toCsvFile(file + ".csv", matched);
			Exporter.toXlsFile(file + ".xls", matched);
		}

		// === arkafrn12
		if (proceed(args, "arkafrn12")) {
			System.out.println("Processing 'arkafrn12'...");
			DataCollector arkafrn12Collector = new ArkaDataCollector("arka-bz-wbk-fundusz-rynku-nieruchomosci-fiz");
			List<Data> arkafrn = arkafrn12Collector.collectData();
			Date start = arkafrn.get(0).getDate();
			DataCollector arkafrn12History = new StooqPageHistoricalDataCollector("arkafrn12", start, end, StooqHistoricalDataInterval.Daily);
			List<Data> stooqHistData = arkafrn12History.collectData();
			// add latest
			StooqDataCollector latestFromStooq = new StooqDataCollector("arkafrn12");
			List<Data> stooqData = latestFromStooq.collectData();
			stooqHistData.add(stooqData.get(0));

			List<Data[]> matched = DataUtils.matchByDate(arkafrn, stooqHistData);
			QuickStats qa = DataUtils.computeDiscount(matched);
			System.out.println(qa.toString());
			String file = "output/" + "arkafrn12" + "_" + sdf.format(c1.getTime());
			Exporter.toCsvFile(file + ".csv", matched);
			Exporter.toXlsFile(file + ".xls", matched);
		}
		System.out.println("Done.");
	}

	private static boolean proceed(String[] args, String string) {
		if (args.length == 0)
			return true;
		for (String arg : args) {
			if (arg.equalsIgnoreCase(string)) 
				return true;
		}
		return false;
	}
}
