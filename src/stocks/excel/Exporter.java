package stocks.excel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.DateTime;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import stocks.data.AllegroData;
import stocks.data.Data;
import stocks.data.DataUtils;
import stocks.data.StooqHistoricalData;

public class Exporter {

	private String inputFile;
	private List<Data[]> datas;

	public static void toCsvFile(String filePath, List<Data[]> matched) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(filePath));
		for (Iterator<Data[]> iterator = matched.iterator(); iterator.hasNext();) {
			Data[] datas = (Data[]) iterator.next();
			String value1 = datas[0].toCsvString();
			String value2 = datas[1] != null ? datas[1].toCsvString() : "";
			out.write(datas[0].getFormattedDate() + ";" + value1 + ";"	+ value2);
			if (iterator.hasNext())
				out.newLine();
		}
		out.close();
	}

	public static void toXlsFile(String filePath, List<Data[]> matched)
			throws IOException {
		Exporter exporter = new Exporter();
		exporter.setOutputFile(filePath);
		exporter.setData(matched);
		try {
			exporter.write();
		} catch (WriteException e) {
			System.err.println("Cannot write: " + filePath);
			e.printStackTrace();
		}
	}

	public void write() throws IOException, WriteException {
		File file = new File(inputFile);
		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("pl", "PL"));

		WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
		workbook.createSheet("Data", 0);
		WritableSheet excelSheet = workbook.getSheet(0);
		createContent(excelSheet);

		workbook.write();
		workbook.close();
	}

	private void createContent(WritableSheet sheet) throws WriteException,
			RowsExceededException {
		
		boolean low = false;
		
		final String ratio = "C%d/B%d";
		final String ratioLow = "E%d/B%d";
		for (int i = 0; i < datas.size(); i++) {
			Data[] d = datas.get(i);
			addDateTime(sheet, 0 /* A */, i, d[0].getDate());
			// benchmark
			addFloat(sheet, 1 /* B */, i, d[0].getValue());
			if (d[1] != null) {
				addFloat(sheet, 2 /* C */, i, d[1].getValue());
				addFormula(sheet, 3 /* D */, i, String.format(ratio, i + 1, i + 1));
				if (d[1] instanceof StooqHistoricalData) {
					low = true;
					StooqHistoricalData shd = (StooqHistoricalData) d[1];
					addFloat(sheet, 4 /* E */, i, shd.getLow());
					addFormula(sheet, 5 /* F */, i, String.format(ratioLow, i + 1, i + 1));
				} else if (d[1] instanceof AllegroData) {
					low = false;
					AllegroData ad = (AllegroData) d[1];
					if (ad.getId() > 0) {
						addInteger(sheet, 4 /* E */, i, ad.getId());
						addLabel(sheet, 5 /* F */, i, ad.getName());
					}
				}
			}
		}
		
		// MIN
		StringBuilder sb = new StringBuilder();
		sb.append("B").append(datas.size()).append("*D").append(datas.size() + 1);
		Formula f = new Formula(2 /* C */, datas.size(), sb.toString());
		sheet.addCell(f);

		sb.setLength(0);
		sb.append("MIN(D1:D").append(datas.size()).append(")");
		f = new Formula(3 /* D */, datas.size(), sb.toString());
		sheet.addCell(f);

		if (low) {
			sb.setLength(0);
			sb.append("B").append(datas.size()).append("*F").append(datas.size() + 1);
			f = new Formula(4 /* E */, datas.size(), sb.toString());
			sheet.addCell(f);

			sb.setLength(0);
			sb.append("MIN(F1:F").append(datas.size()).append(")");
			f = new Formula(5 /* F */, datas.size(), sb.toString());
			sheet.addCell(f);
		}
		
		// MEDIAN
		sb.setLength(0);
		sb.append("B").append(datas.size()).append("*D").append(datas.size() + 2);
		f = new Formula(2 /* C */, datas.size()+1, sb.toString());
		sheet.addCell(f);

		sb.setLength(0);
		sb.append("MEDIAN(D1:D").append(datas.size()).append(")");
		f = new Formula(3 /* D */, datas.size()+1, sb.toString());
		sheet.addCell(f);

		if (low) {
			sb.setLength(0);
			sb.append("B").append(datas.size()).append("*F").append(datas.size() + 2);
			f = new Formula(4 /* E */, datas.size()+1, sb.toString());
			sheet.addCell(f);

			sb.setLength(0);
			sb.append("MEDIAN(F1:F").append(datas.size()).append(")");
			f = new Formula(5 /* F */, datas.size()+1, sb.toString());
			sheet.addCell(f);
		}
	}

	private void addDateTime(WritableSheet s, int c, int r,
			Date date) throws RowsExceededException, WriteException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		DateTime dateTime = new DateTime(c, r, DataUtils.adjustTimezone(cal).getTime());
		s.addCell(dateTime);
	}
	
	private void addFloat(WritableSheet s, int c, int r,
			float f) throws WriteException, RowsExceededException {
		WritableCellFormat floatFormat = new WritableCellFormat (NumberFormats.FLOAT); 
		Number number = new Number(c, r, f, floatFormat);
		s.addCell(number);
	}
	
	private void addInteger(WritableSheet s, int c, int r,
			float f) throws WriteException, RowsExceededException {
		WritableCellFormat floatFormat = new WritableCellFormat (NumberFormats.INTEGER); 
		Number number = new Number(c, r, f, floatFormat);
		s.addCell(number);
	}
	
	private void addFormula(WritableSheet s, int c, int r,
			String f) throws WriteException, RowsExceededException {
		Formula formula = new Formula(c, r, f);
		s.addCell(formula);
	}
	
	private void addLabel(WritableSheet s, int c, int r,
			String l) throws WriteException, RowsExceededException {
		Label label = new Label(c, r, l);
		s.addCell(label);
	}

	public void setOutputFile(String inputFile) {
		this.inputFile = inputFile;
	}
	
	public void setData(List<Data[]> datas) {
		this.datas = datas;
	}
}