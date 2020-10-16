package nd.data.stream;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelSink implements Closeable {
	private static final Logger logger = LoggerFactory.getLogger(ExcelSink.class);
	private Workbook workbook;
	private Sheet sheet;
	// Only for Streams indexing
	private int index;
	private Row row;
	
	public boolean open(final StringToInputStream stis) {
		try (final InputStream inp = stis.getInputStream()) {
			workbook = WorkbookFactory.create(inp);
			sheet = workbook.getSheetAt(0);
		} catch (IOException e) {
            logger.error("Problem using stream: {}", stis.getURI(), e);
            return false;
		}
		return true;
	}
	
	public boolean load(Stream<List<String>> lines, int rowOff, int colOff) {
		index = rowOff;
		lines.forEach(ln -> {
			row = sheet.getRow(index);
			if (null == row) {
				row = sheet.createRow(index);
			}
			IntStream.range(colOff, colOff + ln.size()).boxed().forEach(col -> {
				Cell cell = row.getCell(col);
				if (null == cell) {
					cell = row.createCell(col);
				}
				cell.setCellValue(ln.get(col));
			});
			index++;
		});
		return true;
	}
	
	public boolean save(final String fname) {
	    try (final OutputStream fileOut = new FileOutputStream(fname)) {
	        workbook.write(fileOut);
	    } catch (IOException e) {
            logger.error("Problem saving workbook: {}", fname, e);
			return false;
		}
	    return true;
	}

	@Override
	public void close() throws IOException {

	}
	public Workbook getWorkbook() {
		return workbook;
	}
	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}
	public Sheet getSheet() {
		return sheet;
	}
	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
	}
}
