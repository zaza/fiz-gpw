package stocks.collector.stooq;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.TransformerException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import stocks.collector.DataCollector;
import stocks.collector.XmlDataCollector;
import stocks.data.Data;
import stocks.data.DataUtils;
import stocks.data.StooqHistoricalData;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * Gather historical data from stooq.pl pages available online. 
 *
 */
public class StooqPageHistoricalDataCollector extends XmlDataCollector {
	
	final private String asset;
	final private Date start;
	final private Date end;
	final private StooqHistoricalDataInterval interval;

	public StooqPageHistoricalDataCollector(String asset, Date start, Date end,
			StooqHistoricalDataInterval interval) {
		this.asset = asset;
		this.start = start;
		this.end = end;
		this.interval = interval;
	}

	@Override
	public List<Data> collectData(boolean includeStart) {
		List<Data> result = new ArrayList<Data>();
		try {
			Document[] documents = getDocuments();
			for (Document doc : documents) {
				NodeList nodes = XPathAPI.selectNodeList(doc, "//table/tbody[@id='f13' and @style='background-color:ffffff' and @align='right']/tr");
				
				if (nodes != null && nodes.getLength() > 0) {
					for (int i = 0; i < nodes.getLength(); i++) {
						Element element = (Element) nodes.item(i);
						NodeList childNodes = element.getChildNodes();
						// getTextContent() is not supported!
						String date = childNodes.item(1).getFirstChild().getNodeValue();
						DateFormat df = new SimpleDateFormat("dd MMM yyyy", detectLocale(date));
						Date d = df.parse(date);
						float open =  Float.parseFloat(childNodes.item(2).getFirstChild().getNodeValue());
						float high =  Float.parseFloat(childNodes.item(3).getFirstChild().getNodeValue());
						float low =  Float.parseFloat(childNodes.item(4).getFirstChild().getNodeValue());
						float close =  Float.parseFloat(childNodes.item(5).getFirstChild().getNodeValue());
						int volume =  DataUtils.getVolume(childNodes.item(6).getFirstChild().getNodeValue());
						StooqHistoricalData data = new StooqHistoricalData(d, open, high, low, close, volume, asset);
						result.add(data);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Collections.sort(result);
		if (includeStart)
			checkFirst(result);
		return result;
	}

	/**
	 * Checks if the collection contains an entry for the start date. If not, it
	 * fetches data from the week before the date and adds data from a day which
	 * is closest to it.
	 *
	 * @param data
	 *            the list of Data objects to check
	 */
	private void checkFirst(List<Data> data) {
		StooqHistoricalData first = (StooqHistoricalData) data.get(0);
		if (first.getDate().after(start)) {
			// collect data from the week before the start date
			DataCollector collector = new StooqPageHistoricalDataCollector(
					asset, DataUtils.weekBefore(start), start,
					StooqHistoricalDataInterval.Daily);
			List<Data> lastWeekData = collector.collectData(false);
			Data toAdd = lastWeekData.get(lastWeekData.size() - 1);
			data.add(0, toAdd);
		}
	}

	protected Document[] getDocuments() throws IOException {
		List<Document> documents = new ArrayList<Document>();
		
		HttpClient httpclient = new DefaultHttpClient();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		HttpGet httpget = new HttpGet("http://stooq.pl/q/d/?s=" + asset
				+ "&c=0&d1=" + sdf.format(start) + "&d2=" + sdf.format(end) + "&i="
				+ interval.toString());
		HttpResponse response = httpclient.execute(httpget);
		Document document = parseXmlFile(response.getEntity().getContent());
		response.getEntity().consumeContent();
		int l = 1;
		do {
			documents.add(document);
			l++;
			httpget = new HttpGet("http://stooq.pl/q/d/?s=" + asset
					+ "&c=0&d1=" + sdf.format(start) + "&d2=" + sdf.format(end) + "&i="
					+ interval.toString()+"&l="+l);
			response = httpclient.execute(httpget);
			document = parseXmlFile(response.getEntity().getContent());
			response.getEntity().consumeContent();
		} while (hasNextPage(document));

		return documents.toArray(new Document[0]);
	}

	public static boolean hasNextPage(Document document) {
		try {
			NodeList nodes = XPathAPI.selectNodeList(document, "//a[text()='>']");
			if (nodes.getLength() == 2)
				return true;
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return false;
	}

	private Locale detectLocale(String date) {
		String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Nov", "Oct", "Dec"};
		for (String month : months)
			if (date.indexOf(month)!=-1)
				return Locale.ENGLISH;
		return new Locale("pl");
	}
}