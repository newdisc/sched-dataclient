package nd.data.stream;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CsvStreamTest {
	public static final String TESTCSV = "src/test/resources/testcsv.txt";
    @Test
    void streamColumnsTest(){
        try (StringToInputStream stis = StringToInputStream.toInputStream(TESTCSV)) {
            Assertions.assertNotNull(stis);
            Assertions.assertNotNull(stis.is);
            final CsvStream ds = new CsvStream();
            Assertions.assertNotNull(ds);
            ds.setiStream(stis);
            ds.loadStream();
            final Stream<List<String>> strm = ds.streamLines();
            Assertions.assertNotNull(strm);
            final Stream<String> strmlns = strm.map(la -> la.stream().collect(Collectors.joining(",")));
            final boolean ret = DataStream.streamToOutputStream(strmlns, System.out);
            Assertions.assertTrue(ret);
        } catch (final Exception e) {
            Assertions.assertTrue(false);//This SHould NEVER happen
        }
    }
}
