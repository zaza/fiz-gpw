package stocks.collector;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

public abstract class XmlDataCollector extends DataCollector {
	
	public static Document parseXmlFile(InputStream in) throws UnsupportedEncodingException {
		Tidy tidy = new Tidy();
		tidy.setShowWarnings(false);
		tidy.setQuiet(true);
		return tidy.parseDOM(new InputStreamReader(in, "UTF-8"), null);
	}
}
