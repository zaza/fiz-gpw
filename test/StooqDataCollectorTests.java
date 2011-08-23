import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.w3c.dom.Document;

import stocks.collector.DataCollector;
import stocks.collector.XmlDataCollector;
import stocks.collector.stooq.StooqDataCollector;
import stocks.collector.stooq.StooqHistoricalDataCollector;
import stocks.collector.stooq.StooqHistoricalDataInterval;
import stocks.collector.stooq.StooqPageHistoricalDataCollector;
import stocks.data.Data;
import stocks.data.StooqCurrentData;
import stocks.data.StooqHistoricalData;

public class StooqDataCollectorTests {

	@Test
	public void testStooqHistoricalData() throws Exception {
		DataCollector invfizInvestorsPl = new StooqHistoricalDataCollector(
				"invfiz", new Date(System.currentTimeMillis()), new Date(System
						.currentTimeMillis()),
				StooqHistoricalDataInterval.Daily) {
			@Override
			protected InputStream getInput() {
				File file = new File(
						"data/invfiz_d.csv");
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			};
		};
		List<Data> data = invfizInvestorsPl.collectData();
		assertEquals(997, data.size());
		
		StooqHistoricalData first = (StooqHistoricalData) data.get(0);
		assertEquals("invfiz", first.getName());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = df.parse("2007-1-22");
		assertEquals(d, first.getDate());
		assertEquals(2199f, first.getOpen(), 0);
		assertEquals(2199f, first.getHigh(), 0);
		assertEquals(2050f, first.getLow(), 0);
		assertEquals(2070f, first.getClose(), 0);
		assertEquals(2070f, first.getValue(), 0);
		assertEquals(7, first.getVolume());
	}

	@Test 
	public void testStooqPageHistoricalDataCollector_hasNextPage() throws FileNotFoundException, UnsupportedEncodingException {
		File file = new File("test/data/stooq-invpefiz-historia.html");
		Document doc = XmlDataCollector.parseXmlFile(new FileInputStream(file));
		assertFalse(StooqPageHistoricalDataCollector.hasNextPage(doc));
		
		file = new File("test/data/stooq-invpefiz-1-historia.html");
		doc = XmlDataCollector.parseXmlFile(new FileInputStream(file));
		assertTrue(StooqPageHistoricalDataCollector.hasNextPage(doc));
		
		file = new File("test/data/stooq-invpefiz-2-historia.html");
		doc = XmlDataCollector.parseXmlFile(new FileInputStream(file));
		assertFalse(StooqPageHistoricalDataCollector.hasNextPage(doc));
	}
	
	@Test
	public void testStooqPageHistoricalData_singlePage() throws Exception {
		DataCollector invfizInvestorsPl = new StooqPageHistoricalDataCollector(
				"invpefiz", new Date(System.currentTimeMillis()), new Date(System
						.currentTimeMillis()),
				StooqHistoricalDataInterval.Daily) {
			@Override
			protected Document[] getDocuments() throws UnsupportedEncodingException {
				File file = new File(
						"test/data/stooq-invpefiz-historia.html");
				try {
					return new Document[]{parseXmlFile(new FileInputStream(file))};
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			};
		};
		List<Data> data = invfizInvestorsPl.collectData();
		assertEquals(32, data.size());
		
		StooqHistoricalData first = (StooqHistoricalData) data.get(0);
		assertEquals("invpefiz", first.getName());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = df.parse("2011-3-14");
		assertEquals(d, first.getDate());
		assertEquals(1400f, first.getOpen(), 0);
		assertEquals(1400f, first.getHigh(), 0);
		assertEquals(1399.99f, first.getLow(), 0);
		assertEquals(1400f, first.getClose(), 0);
		assertEquals(1400f, first.getValue(), 0);
		assertEquals(49, first.getVolume());
	}
	
	@Test
	public void testStooqPageHistoricalData_twoPages() throws Exception {
		DataCollector invfizInvestorsPl = new StooqPageHistoricalDataCollector(
				"invpefiz", new Date(System.currentTimeMillis()), new Date(System
						.currentTimeMillis()),
				StooqHistoricalDataInterval.Daily) {
			@Override
			protected Document[] getDocuments() throws UnsupportedEncodingException {
				File file1 = new File(
						"test/data/stooq-invpefiz-1-historia.html");
				File file2 = new File(
						"test/data/stooq-invpefiz-2-historia.html");
				try {
					return new Document[]{
							parseXmlFile(new FileInputStream(file1)),
							parseXmlFile(new FileInputStream(file2))};
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			};
		};
		List<Data> data = invfizInvestorsPl.collectData();
		assertEquals(52, data.size());
		
		StooqHistoricalData first = (StooqHistoricalData) data.get(0);
		assertEquals("invpefiz", first.getName());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = df.parse("2011-2-14");
		assertEquals(d, first.getDate());
		assertEquals(1405.1f, first.getOpen(), 0);
		assertEquals(1405.1f, first.getHigh(), 0);
		assertEquals(1405.1f, first.getLow(), 0);
		assertEquals(1405.1f, first.getClose(), 0);
		assertEquals(1405.1f, first.getValue(), 0);
		assertEquals(5, first.getVolume());
	}
	
	@Test
	public void testArkafrn12StooqLast() throws Exception {
		DataCollector arkafrnStooq = new StooqDataCollector("arkafrn12"){
			@Override
			protected InputStream getInput() {
				File file = new File(
						"test/data/stooq-arkafrn12-wykres.html");
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			};
		};
		List<Data> data = arkafrnStooq.collectData();
		assertEquals(1, data.size());
		
		StooqCurrentData first = (StooqCurrentData) data.get(0);
		assertEquals("arkafrn12", first.getName());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = df.parse("2011-01-18");
		assertEquals(d, first.getDate());
		assertEquals(104f, first.getOpen(), 0);
		assertEquals(104f, first.getValue(), 0);
		assertEquals(104f, first.getAsk(), 0);
		assertEquals(102.5f, first.getBid(), 0);
		assertEquals(393, first.getVolume());
	}
	
	@Test
	public void testRcsilaopenStooqLast() throws Exception {
		DataCollector arkafrnStooq = new StooqDataCollector("rcsilaopen"){
			@Override
			protected InputStream getInput() {
				File file = new File(
						"test/data/stooq-rcsilaopen-wykres.html");
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			};
		};
		List<Data> data = arkafrnStooq.collectData();
		assertEquals(1, data.size());
		
		StooqCurrentData first = (StooqCurrentData) data.get(0);
		assertEquals("rcsilaopen", first.getName());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = df.parse("2011-01-21");
		assertEquals(d, first.getDate());
		assertEquals(78.5f, first.getOpen(), 0);
		assertEquals(77.8f, first.getValue(), 0);
		assertEquals(78.6f, first.getAsk(), 0);
		assertEquals(77.8f, first.getBid(), 0);
		assertEquals(7790, first.getVolume());
	}
	
	@Test
	public void testStooqPageHistoricalData_sevenPages() throws Exception {
		DataCollector invfizInvestorsPl = new StooqPageHistoricalDataCollector(
				"rcsilaopen", new Date(System.currentTimeMillis()), new Date(System
						.currentTimeMillis()),
				StooqHistoricalDataInterval.Daily) {
			@Override
			protected Document[] getDocuments() throws UnsupportedEncodingException {
				File file1 = new File(
						"test/data/stooq-rcsilaopen-20100725-20110823-1.html");
				File file2 = new File(
						"test/data/stooq-rcsilaopen-20100725-20110823-2.html");
				File file3 = new File(
						"test/data/stooq-rcsilaopen-20100725-20110823-3.html");
				File file4 = new File(
						"test/data/stooq-rcsilaopen-20100725-20110823-4.html");
				File file5 = new File(
						"test/data/stooq-rcsilaopen-20100725-20110823-5.html");
				File file6 = new File(
						"test/data/stooq-rcsilaopen-20100725-20110823-6.html");
				File file7 = new File(
						"test/data/stooq-rcsilaopen-20100725-20110823-7.html");
				
				try {
					return new Document[]{
							parseXmlFile(new FileInputStream(file1)),
							parseXmlFile(new FileInputStream(file2)),
							parseXmlFile(new FileInputStream(file3)),
							parseXmlFile(new FileInputStream(file4)),
							parseXmlFile(new FileInputStream(file5)),
							parseXmlFile(new FileInputStream(file6)),
							parseXmlFile(new FileInputStream(file7))};
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			};
		};
		List<Data> data = invfizInvestorsPl.collectData();
		assertEquals(271, data.size());
		
		StooqHistoricalData first = (StooqHistoricalData) data.get(0);
		assertEquals("rcsilaopen", first.getName());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = df.parse("2010-7-27");
		assertEquals(d, first.getDate());
		assertEquals(56.34f, first.getOpen(), 0);
		assertEquals(56.34f, first.getHigh(), 0);
		assertEquals(54.64f, first.getLow(), 0);
		assertEquals(54.64f, first.getClose(), 0);
		assertEquals(54.64f, first.getValue(), 0);
		assertEquals(150, first.getVolume());
	}
}
