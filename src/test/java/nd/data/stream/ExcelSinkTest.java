package nd.data.stream;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExcelSinkTest {
	public static final String TESTXLSX = "src/test/resources/testxlsx.xlsx";
    @Test
    public void streamColumnsTest(){
        try (StringToInputStream stis = StringToInputStream.toInputStream(CsvStreamTest.TESTCSV);
        	StringToInputStream stisxl = StringToInputStream.toInputStream(TESTXLSX);) {
            Assertions.assertNotNull(stis);
            Assertions.assertNotNull(stis.is);
            final CsvStream ds = new CsvStream();
            Assertions.assertNotNull(ds);
            ds.setiStream(stis);
            ds.loadStream();
            final Stream<List<String>> strm = ds.streamLines();
            Assertions.assertNotNull(strm);
            final ExcelSink xs = new ExcelSink();
            boolean ret = xs.open(stisxl);
            Assertions.assertTrue(ret);
            ret = xs.load(strm, 1, 0);
            Assertions.assertTrue(ret);
            ret = xs.save("out.xlsx");
            Assertions.assertTrue(ret);
        } catch (final Exception e) {
            Assertions.assertTrue(false);//This SHould NEVER happen
        }
    }
}
