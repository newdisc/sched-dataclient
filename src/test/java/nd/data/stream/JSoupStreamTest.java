package nd.data.stream;

import java.util.List;
import java.util.stream.Stream;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
    public void streamToOutputStreamWebJsoupRowsTest(){
        try (StringToInputStream stis = StringToInputStream.toInputStream(
        	WMCVDFILE)) {
            Assertions.assertNotNull(stis);
            Assertions.assertNotNull(stis.is);
            final JSoupStream ds = new JSoupStream();
            ds.setiStream(stis);
            Assertions.assertNotNull(ds);
            ds.setTableSelector(TABLESELECTOR);
            ds.setHeaderSelector(HEADERSELECTOR);
            ds.setRowsSelector(ROWSSELECTOR);
            ds.setColumnSelector(COLUMNSELECTOR);
            ds.printRows();
        } catch (final Exception e) {
        	logger.error("Issue with StreamWebJsoupRowsTest: {}", e);
            Assertions.assertTrue(false);//This SHould NEVER happen
        }        
    }

    @Test
    public void streamToOutputStreamWebJsoupTest(){
        try (StringToInputStream stis = StringToInputStream.toInputStream(
            WIKIFILE)) {
            //StringToInputStreamTest.WIKIURL)) {
            Assertions.assertNotNull(stis);
            Assertions.assertNotNull(stis.is);
            final JSoupStream ds = new JSoupStream();
            ds.setiStream(stis);
            Assertions.assertNotNull(ds);
            final Elements strm = ds.streamElements("table.infobox tr");
            Assertions.assertNotNull(strm);
            strm.stream().forEach(e -> {
                final Element e1 = e;
                final String msg = "Table: #" + e1.id() + "." + e1.className();
                logger.info("");
                logger.info("Processing : {}", msg);
                System.out.println(msg);
                System.out.println(e1.outerHtml());
                //System.out.println(e1.html());
            });
        } catch (final Exception e) {
            Assertions.assertTrue(false);//This SHould NEVER happen
        }        
    }

    @Test
    public void streamToOutputStreamWebJsoupWMCVDTest(){
        try (StringToInputStream stis = StringToInputStream.toInputStream(
            WMCVDFILE)) {
            //DataStreamTest.WMCVDHTML)) {
            Assertions.assertNotNull(stis);
            Assertions.assertNotNull(stis.is);
            final JSoupStream ds = new JSoupStream();
            ds.setiStream(stis);
            Assertions.assertNotNull(ds);
            final Elements strm = ds.streamElements("table#main_table_countries_today");
            Assertions.assertNotNull(strm);
            strm.stream().forEach(e -> {
                final Element e1 = e;
                final String msg = "Table: #" + e1.id() + "." + e1.className();
                logger.info("");
                logger.info("Processing : {}", msg);
                System.out.println(msg);
                System.out.println(e1.outerHtml());
                //System.out.println(e1.html());
            });
        } catch (final Exception e) {
            Assertions.assertTrue(false);//This SHould NEVER happen
        }        
    }
    
    @Test
    public void streamLinesTest() {
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
