package stocks.collector.arka;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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

import com.sun.org.apache.xpath.internal.XPathAPI;

public class ArkaDataCollector extends XmlDataCollector {

	private String asset;

	public ArkaDataCollector(String asset) {
		this.asset = asset;
	}

	@Override
	public List<Data> collectData() {
		List<Data> result = new ArrayList<Data>();
		try {
			InputStream inputStream = getInput();
			Document dom = parseXmlFile(inputStream);
			NodeList nodes = XPathAPI.selectNodeList(dom, "//table[@class='krytable']/tbody/tr[position()>1]");
			if (nodes != null && nodes.getLength() > 0) {
				for (int i = 0; i < nodes.getLength(); i++) {
					Element element = (Element) nodes.item(i);
					NodeList childNodes = element.getChildNodes();
					// getTextContent() is not supported!
					String date = childNodes.item(0).getFirstChild().getNodeValue();
					String value = childNodes.item(1).getFirstChild().getNodeValue();
					value = value.substring(0, value.indexOf(' '));
					value = value.replace(',', '.');
					DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
					Date d = df.parse(date); 
					Data data = new Data(d, Float.parseFloat(value), asset);
					result.add(data);
				}
			}
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collections.sort(result);
		return result;
	}

	protected InputStream getInput() throws IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(
				"http://arka.pl/produkty/fundusze-inwestycyjne-zamkniete/"
						+ asset + "/" + asset + ".html");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = httpclient.execute(httpget, responseHandler);
		httpclient.getConnectionManager().shutdown();
		return new ByteArrayInputStream(responseBody.getBytes());
	}

}
