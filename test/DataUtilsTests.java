import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import stocks.data.Data;
import stocks.data.DataUtils;
import stocks.data.QuickStats;

public class DataUtilsTests {

	@Test
	public void testGetFirst() {
		List<Data> data = new ArrayList<Data>();
		Data a = new Data(new Date(2000, 3, 1), 1f, "a");
		data.add(a);
		Data b = new Data(new Date(2001, 1, 1), 1f, "b");
		data.add(b);
		Data c = new Data(new Date(2000, 1, 1), 1f, "c");
		data.add(c);
		Data d = new Data(new Date(2001, 1, 2), 1f, "d");
		data.add(d);
		Collections.sort(data);
		assertEquals(c, data.get(0));
	}

	@Test
	public void testGetByDate() {
		List<Data> data = new ArrayList<Data>();
		Data a = new Data(new Date(2000, 3, 1), 1f, "a");
		data.add(a);
		Data b = new Data(new Date(2001, 1, 1), 1f, "b");
		data.add(b);
		Data c = new Data(new Date(2000, 1, 1), 1f, "c");
		data.add(c);
		Data d = new Data(new Date(2001, 1, 2), 1f, "d");
		data.add(d);
		Data byDate = DataUtils.getOneByDate(data, new Date(2001, 1, 1));
		assertEquals(b, byDate);
	}

	@Test(expected = IllegalStateException.class)
	public void testStockBeforeFund() {
		List<Data> fiz = new ArrayList<Data>();
		Data b = new Data(new Date(2011-1900, 1-1, 8), 110f, "fiz");
		fiz.add(b);
		Data c = new Data(new Date(2011-1900, 1-1, 15), 120f, "fiz");
		fiz.add(c);
		Data d = new Data(new Date(2011-1900, 1-1, 22), 130f, "fiz");
		fiz.add(d);
		Collections.sort(fiz);

		List<Data> stooq = new ArrayList<Data>();
		Data s = new Data(new Date(2011-1900, 1-1, 2), 99f, "stooq");
		stooq.add(s);
		Collections.sort(stooq);
		
		DataUtils.matchByDate(fiz, stooq);
	}
	
	@Test
	public void testMatchByDate() {
		List<Data> fiz = new ArrayList<Data>();
		Data a = new Data(new Date(2011-1900, 1-1, 1), 100f, "fiz");
		fiz.add(a);
		Data b = new Data(new Date(2011-1900, 1-1, 8), 110f, "fiz");
		fiz.add(b);
		Data c = new Data(new Date(2011-1900, 1-1, 15), 120f, "fiz");
		fiz.add(c);
		Data d = new Data(new Date(2011-1900, 1-1, 22), 130f, "fiz");
		fiz.add(d);
		Collections.sort(fiz);

		List<Data> stooq = new ArrayList<Data>();
		for (int i = 3; i <= 31; i++) { // two days later
			if (i == 10 || i == 15 || i == 25)
				continue;
			// todo skip 15, exists in fiz
			Data s = new Data(new Date(2011-1900, 1-1, i), 100f+i, "stooq");
			stooq.add(s);
		}
		Collections.sort(stooq);
		
		List<Data[]> matched = DataUtils.matchByDate(fiz, stooq);
		assertEquals(31, matched.size());
		for (int i = 1; i < 3; i++) {
			Data[] datas = matched.get(i-1);
			assertEquals(new Date(2011-1900, 1-1, i), datas[0].getDate());
			assertNull(datas[1]);
			assertEquals("fiz", datas[0].getName());
			assertEquals(100f, datas[0].getValue(), 0);
		}
		for (int i = 3; i < 8; i++) {
			Data[] datas = matched.get(i-1);
			assertEquals(new Date(2011-1900, 1-1, i), datas[0].getDate());
			assertEquals(new Date(2011-1900, 1-1, i), datas[1].getDate());
			assertEquals("fiz", datas[0].getName());
			assertEquals("stooq", datas[1].getName());
			assertEquals(100f, datas[0].getValue(), 0);
			assertEquals(100f+i, datas[1].getValue(), 0);
		}
		for (int i = 8; i < 15; i++) {
			Data[] datas = matched.get(i-1);
			assertEquals(new Date(2011-1900, 1-1, i), datas[0].getDate());
			assertEquals("fiz", datas[0].getName());
			assertEquals(110f, datas[0].getValue(), 0);
			if (i==10) {
				assertNull(datas[1]);
			} else {
				assertEquals(new Date(2011-1900, 1-1, i), datas[1].getDate());
				assertEquals("stooq", datas[1].getName());
				assertEquals(100f+i, datas[1].getValue(), 0);
			}
		}
		for (int i = 15; i < 22; i++) {
			Data[] datas = matched.get(i-1);
			assertEquals(new Date(2011-1900, 1-1, i), datas[0].getDate());
			if (i==15) {
				assertNull(datas[1]);
			} else {
				assertEquals(new Date(2011-1900, 1-1, i), datas[1].getDate());
				assertEquals("stooq", datas[1].getName());
				assertEquals(100f+i, datas[1].getValue(), 0);
			}
			assertEquals("fiz", datas[0].getName());
			assertEquals(120f, datas[0].getValue(), 0);
		}
		for (int i = 22; i <= 31; i++) {
			Data[] datas = matched.get(i-1);
			assertEquals(new Date(2011-1900, 1-1, i), datas[0].getDate());
			assertEquals("fiz", datas[0].getName());
			assertEquals(130f, datas[0].getValue(), 0);
			if (i==25) {
				assertNull(datas[1]);
			} else {
				assertEquals(new Date(2011-1900, 1-1, i), datas[1].getDate());
				assertEquals("stooq", datas[1].getName());
				assertEquals(100f+i, datas[1].getValue(), 0);
			}
		}
	}
	
	@Test
	public void testComputeDiscountEmpty() {
		List<Data[]> matched = new ArrayList<Data[]>();
		QuickStats result = DataUtils.computeDiscount(matched);
		assertNull(result);
	}

	@Test
	public void testComputeDiscountNoMatch() {
		List<Data[]> matched = new ArrayList<Data[]>();
		matched.add(new Data[] {new Data(new Date(2000, 3, 1), 1f, "a"), null});
		QuickStats result = DataUtils.computeDiscount(matched);
		assertEquals(0, result.getLowest(), 0);
		assertEquals(0, result.getMedian(), 0);
		assertEquals(0, result.getMedianLowerThan1(), 0);
		assertEquals(0, result.getLast(), 0);
	}
	
	@Test
	public void testComputeDiscount1f() {
		List<Data[]> matched = new ArrayList<Data[]>();
		matched.add(new Data[] {new Data(new Date(2000, 3, 1), 1f, "a"), new Data(new Date(2000, 3, 1), 1f, "a")});
		QuickStats result = DataUtils.computeDiscount(matched);
		assertEquals(1f, result.getLowest(), 0);
		assertEquals(1f, result.getMedian(), 0);
		assertEquals(0f, result.getMedianLowerThan1(), 0);
		assertEquals(1f, result.getLast(), 0);
	}
	
	@Test
	public void testComputeDiscountSecondNull() {
		List<Data[]> matched = new ArrayList<Data[]>();
		matched.add(new Data[] {new Data(new Date(2000, 3, 1), 1f, "a"), new Data(new Date(2000, 3, 1), 1f, "a")});
		matched.add(new Data[] {new Data(new Date(2000, 3, 2), 1f, "a"), null});
		QuickStats result = DataUtils.computeDiscount(matched);
		assertEquals(1f, result.getLowest(), 0);
		assertEquals(1f, result.getMedian(), 0);
		assertEquals(0f, result.getMedianLowerThan1(), 0);
		assertEquals(1f, result.getLast(), 0);
	}
	
	@Test
	public void testComputeDiscountTwo() {
		List<Data[]> matched = new ArrayList<Data[]>();
		matched.add(new Data[] {new Data(new Date(2000, 3, 1), 1f, "a"), new Data(new Date(2000, 3, 1), 1f, "a")});
		matched.add(new Data[] {new Data(new Date(2000, 3, 2), 1f, "a"), new Data(new Date(2000, 3, 2), 0.9f, "a")});
		QuickStats result = DataUtils.computeDiscount(matched);
		assertEquals(0.9f, result.getLowest(), 0);
		assertEquals(0.95f, result.getMedian(), 0);
		assertEquals(0.9f, result.getMedianLowerThan1(), 0);
		assertEquals(0.9f, result.getLast(), 0);
	}
	
	@Test
	public void testComputeDiscountThree() {
		List<Data[]> matched = new ArrayList<Data[]>();
		matched.add(new Data[] {new Data(new Date(2000, 3, 1), 1f, "a"), new Data(new Date(2000, 3, 1), 1f, "a")});
		matched.add(new Data[] {new Data(new Date(2000, 3, 2), 1f, "a"), new Data(new Date(2000, 3, 2), 0.9f, "a")});
		matched.add(new Data[] {new Data(new Date(2000, 3, 3), 1f, "a"), new Data(new Date(2000, 3, 3), 1.1f, "a")});
		QuickStats result = DataUtils.computeDiscount(matched);
		assertEquals(0.9f, result.getLowest(), 0);
		assertEquals(1f, result.getMedian(), 0);
		assertEquals(0.9f, result.getMedianLowerThan1(), 0);
		assertEquals(1.1f, result.getLast(), 0);
	}
}
