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
import stocks.collector.investors.InvestorsPlDataCollector;
import stocks.collector.investors.InvestorsPlDataCollector.Fund;
import stocks.data.Data;

public class InvestorsDataCollectorTests {

	@Test
	public void testInvestors() throws Exception {
		DataCollector invfizInvestorsPl = new InvestorsPlDataCollector(
				Fund.Invfiz) {
			protected InputStream getInput() {
				File file = new File("test/data/wyceny-fiz.csv");
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			};
		};
		List<Data> data = invfizInvestorsPl.collectData();
		assertEquals(7 /*2011*/ + 12 /* 2010 */+ 12 /* 2009 */+ 12 /* 2008 */+ 12 /* 2007 */
				+ 12 /* 2006 */+ 6/* 2005 */, data.size());

		Data first = data.get(0);
		assertEquals(Fund.Invfiz.getStooq(), first.getName());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = df.parse("2005-9-2");
		assertEquals(d, first.getDate());
		assertEquals(1000, first.getValue(), 0);
	}

	@Test
	public void testInvestorsGold() throws Exception {
		DataCollector invfizInvestorsPl = new InvestorsPlDataCollector(
				Fund.InvGold) {
			protected InputStream getInput() {
				File file = new File("test/data/wyceny-fiz.csv");
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			};
		};
		List<Data> data = invfizInvestorsPl.collectData();
		assertEquals(7 /*2011*/ + 12 /* 2010 */+ 12 /* 2009 */+ 12 /* 2008 */+ 12 /* 2007 */
				+ 5/* 2006 */, data.size());

		Data first = data.get(0);
		assertEquals(Fund.InvGold.getStooq(), first.getName());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = df.parse("2006-9-24");
		assertEquals(d, first.getDate());
		assertEquals(1000, first.getValue(), 0);
	}

	@Test
	public void testInvestorsCee() throws Exception {
		DataCollector invfizInvestorsPl = new InvestorsPlDataCollector(
				Fund.InvCee) {
			protected InputStream getInput() {
				File file = new File("test/data/wyceny-fiz.csv");
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			};
		};
		List<Data> data = invfizInvestorsPl.collectData();
		assertEquals(7 /*2011*/ + 12 /* 2010 */+ 12 /* 2009 */+ 12 /* 2008 */
		+ 9/* 2007 */, data.size());

		Data first = data.get(0);
		assertEquals(Fund.InvCee.getStooq(), first.getName());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = df.parse("2007-5-21");
		assertEquals(d, first.getDate());
		assertEquals(1000, first.getValue(), 0);
	}

	@Test
	public void testInvestorsPe() throws Exception {
		DataCollector invfizInvestorsPl = new InvestorsPlDataCollector(
				Fund.InvPe) {
			protected InputStream getInput() {
				File file = new File("test/data/wyceny-fiz.csv");
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			};
		};
		List<Data> data = invfizInvestorsPl.collectData();
		assertEquals(17, data.size());

		Data first = data.get(0);
		assertEquals(Fund.InvPe.getStooq(), first.getName());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = df.parse("2007-9-21");
		assertEquals(d, first.getDate());
		assertEquals(1000, first.getValue(), 0);
	}

	@Test
	public void testInvestorsProperty() throws Exception {
		DataCollector invfizInvestorsPl = new InvestorsPlDataCollector(
				Fund.InvProperty) {
			protected InputStream getInput() {
				File file = new File("test/data/wyceny-fiz.csv");
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				return null;
			};
		};
		List<Data> data = invfizInvestorsPl.collectData();
		assertEquals(6, data.size());

		Data first = data.get(0);
		assertEquals(Fund.InvProperty.getStooq(), first.getName());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d = df.parse("2010-7-9");
		assertEquals(d, first.getDate());
		assertEquals(1000, first.getValue(), 0);
	}
}
