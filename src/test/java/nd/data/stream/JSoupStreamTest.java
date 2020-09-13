package nd.data.stream;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSoupStreamTest {
    private static final Logger logger = LoggerFactory.getLogger(JSoupStreamTest.class);
    public static final String WIKIFILE = "classpath:/testhtml.html";
    public static final String WMCVDFILE = "classpath:/testhtmlWMCVD.html";
    private static final String TABLESELECTOR = "table";
    private static final String HEADERSELECTOR = "th";
    private static final String ROWSSELECTOR = "tr";
    private static final String COLUMNSELECTOR = "td";
    
    @Test
    public void streamLinesWikiTest() {
        try (StringToInputStream stis = StringToInputStream.toInputStream(
        		WIKIFILE)) {
                //DataStreamTest.WMCVDHTML)) {
            Assertions.assertNotNull(stis);
            Assertions.assertNotNull(stis.is);
            final JSoupStream ds = new JSoupStream();
            ds.setiStream(stis);
            Assertions.assertNotNull(ds);
            ds.setTableSelector("table.infobox");
            ds.setHeaderSelector(HEADERSELECTOR);
            ds.setRowsSelector(ROWSSELECTOR);
            ds.setColumnSelector(COLUMNSELECTOR);
            ds.loadStream();
            Stream<List<String>> lines = ds.streamLines();
            Assertions.assertNotNull(lines);
            lines.forEach(l -> {
                    logger.info("Line : {}", l);
                });
            } catch (final Exception e) {
                Assertions.assertTrue(false);//This SHould NEVER happen
            }        
    }
    
    @Test
    public void streamLinesCVDTest() {
        try (StringToInputStream stis = StringToInputStream.toInputStream(
                WMCVDFILE)) {
                //DataStreamTest.WMCVDHTML)) {
            Assertions.assertNotNull(stis);
            Assertions.assertNotNull(stis.is);
            final JSoupStream ds = new JSoupStream();
            ds.setiStream(stis);
            Assertions.assertNotNull(ds);
            ds.setTableSelector(TABLESELECTOR);
            ds.setHeaderSelector(HEADERSELECTOR);
            ds.setRowsSelector(ROWSSELECTOR);
            ds.setColumnSelector(COLUMNSELECTOR);
            ds.loadStream();
            Stream<List<String>> lines = ds.streamLines();
            Assertions.assertNotNull(lines);
            lines.forEach(l -> {
                    logger.info("Line : {}", l);
                });
            } catch (final Exception e) {
                Assertions.assertTrue(false);//This SHould NEVER happen
            }        
    }
}
