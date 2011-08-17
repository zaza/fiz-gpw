package stocks.collector.investors;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import stocks.collector.XmlDataCollector;
import stocks.data.Data;

public class InvestorsPlDataCollector extends XmlDataCollector {

	public enum Fund {
		Invfiz("Investors FIZ", "invfiz"), InvGold("Investors Gold FIZ",
				"invgldfiz"), InvCee("Investors CEE FIZ", "invceefiz"), InvPe(
				"Investors PE FIZ", "invpefiz"), InvProperty(
				"Investors Property FIZ", "invprfiz");
		private String fullName;
		private String stooq;

		Fund(String fullName, String stooq) {
			this.fullName = fullName;
			this.stooq = stooq;
		}
		
		public String getStooq() {
			return stooq;
		}
		
		@Override
		public String toString() {
			return fullName;
		}
	}
	
	private Fund fund;

	public InvestorsPlDataCollector(Fund asset) {
		this.fund = asset;
	}

	@Override
	public List<Data> collectData() {
		List<Data> result = new ArrayList<Data>();
		
		try {
			InputStream inputStream = getInput();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = bufferedReader.readLine(); // skip first line
			while ((line = bufferedReader.readLine()) != null
					&& !line.equals("")) {
				String[] split = line.split(";");
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date d = df.parse(split[0]);
				String priceString = null;
				
				switch (fund) {
				case Invfiz:
					if (split.length > 1 && !split[1].isEmpty())
						priceString = split[1];
					break;
				case InvGold:
					if (split.length > 2 && !split[2].isEmpty())
						priceString = split[2];
					break;
				case InvCee:
					if (split.length > 3 && !split[3].isEmpty())
						priceString = split[3];
					break;
				case InvPe:
					if (split.length > 4 && !split[4].isEmpty())
						priceString = split[4];
					break;
				case InvProperty:
					if (split.length > 5 && !split[5].isEmpty())
						priceString = split[5];
					break;
				}
				if (priceString != null) {
					float price = Float.parseFloat(priceString.replace(',', '.'));
					Data data = new Data(d, price, fund.stooq);
					result.add(data);
				}
			}
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		Collections.sort(result);
		return result;
	}
	
	protected InputStream getInput() throws IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://investors.pl/wyceny.csv,fiz,1");
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody = httpclient.execute(httpget, responseHandler);
		httpclient.getConnectionManager().shutdown();
		return new ByteArrayInputStream(responseBody.getBytes());
	}
	
	/*
	protected InputStream getInput() {
		HttpClient client = new HttpClient();

		GetMethod method = new GetMethod("http://tfi.investors.pl/" + asset
				+ "/wyniki.html");
		try {
			System.out.print("Executing GET... ");
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
				// TODO: return null
			}
			System.out.println("done.");

			// byte[] responseBody = method.getResponseBody();
			// System.out.println(new String(responseBody));

			return method.getResponseBodyAsStream();
		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			method.releaseConnection();
		}
		return null;
	}
	*/
}
