package stocks.collector.stooq;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import stocks.collector.XmlDataCollector;
import stocks.data.Data;
import stocks.data.DataUtils;
import stocks.data.StooqCurrentData;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * Get actual data for an asset from stooq.pl
 *
 */
public class StooqDataCollector extends XmlDataCollector {

	private String asset;

	public StooqDataCollector(String asset) {
		this.asset = asset;
	}

	@Override
	public List<Data> collectData() {
		List<Data> result = new ArrayList<Data>();
		try {
			InputStream inputStream = getInput();
			Document dom = parseXmlFile(inputStream);

			String value = null;
			NodeList nodes = XPathAPI.selectNodeList(dom, "//span[@id='aq_" + asset + "_c2|3']");
			if (nodes != null && nodes.getLength() > 0) {
				Element element = (Element) nodes.item(0);
				value = element.getFirstChild().getNodeValue();
			}
			String date = null;
			nodes = XPathAPI.selectNodeList(dom, "//span[@id='aq_" + asset + "_d2']");
			if (nodes != null && nodes.getLength() > 0) {
				Element element = (Element) nodes.item(0);
				date = element.getFirstChild().getNodeValue();
			}
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date d = df.parse(date); 
			
			String open = null;
			nodes = XPathAPI.selectNodeList(dom, "//span[@id='aq_"+asset+"_o']");
			if (nodes != null && nodes.getLength() > 0) {
				Element element = (Element) nodes.item(0);
				open = element.getFirstChild().getNodeValue();
			}

			String bid = null;
			nodes = XPathAPI.selectNodeList(dom, "//span[@id='aq_"+asset+"_b2']");
			if (nodes != null && nodes.getLength() > 0) {
				Element element = (Element) nodes.item(0);
				bid = element.getFirstChild().getNodeValue();
			}

			String ask = null;
			nodes = XPathAPI.selectNodeList(dom, "//span[@id='aq_"+asset+"_a2']");
			if (nodes != null && nodes.getLength() > 0) {
				Element element = (Element) nodes.item(0);
				ask = element.getFirstChild().getNodeValue();
			}

			String volume = null;
			nodes = XPathAPI.selectNodeList(dom, "//span[@id='aq_"+asset+"_v2']");
			if (nodes != null && nodes.getLength() > 0) {
				Element element = (Element) nodes.item(0);
				volume = element.getFirstChild().getNodeValue();
			}

			StooqCurrentData data = new StooqCurrentData(d, Float.parseFloat(value), asset);
			data.setOpen(Float.parseFloat(open));
			data.setBid(Float.parseFloat(bid));
			data.setAsk(Float.parseFloat(ask));
			data.setVolume(DataUtils.getVolume(volume));
			result.add(data);
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	protected InputStream getInput() throws IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://stooq.pl/q/?s=" + asset);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = httpclient.execute(httpget, responseHandler);
		httpclient.getConnectionManager().shutdown();
		return new ByteArrayInputStream(responseBody.getBytes());
	}
}