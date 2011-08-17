package stocks.collector;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import stocks.data.Data;

public abstract class XmlDataCollector extends DataCollector {
	
	@Override
	public abstract List<Data> collectData();

	public static Document parseXmlFile(InputStream in) throws UnsupportedEncodingException {
		Tidy tidy = new Tidy();
		tidy.setShowWarnings(false);
		tidy.setQuiet(true);
		return tidy.parseDOM(new InputStreamReader(in, "UTF-8"), null);
	}
}
