package nd.data.stream;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataStreamTest {
    private static final Logger logger = LoggerFactory.getLogger(DataStreamTest.class);
    public static final String WIKIFILE = "classpath:/testhtml.html";
    @Test
    public void streamToOutputStreamFileTest(){
        try (StringToInputStream stis = StringToInputStream.toInputStream("src/test/resources/testcsv.txt")) {
            Assertions.assertNotNull(stis);
            Assertions.assertNotNull(stis.is);
            final DataStream ds = new DataStream(stis);
            Assertions.assertNotNull(ds);
            final Stream<String> strm = ds.streamLines();
            Assertions.assertNotNull(strm);
            final boolean ret = DataStream.streamToOutputStream(strm, System.out);
            Assertions.assertTrue(ret);
        } catch (final Exception e) {
            Assertions.assertTrue(false);//This SHould NEVER happen
        }        
    }
    
    @Test
    public void streamToOutputStreamWebTest(){
        try (StringToInputStream stis = StringToInputStream.toInputStream(StringToInputStreamTest.WIKIURL)) {
            Assertions.assertNotNull(stis);
            Assertions.assertNotNull(stis.is);
            final DataStream ds = new DataStream(stis);
            Assertions.assertNotNull(ds);
            final Stream<String> strm = ds.streamLines();
            Assertions.assertNotNull(strm);
            //final FileOutputStream os = new FileOutputStream("testout.txt");
            final boolean ret = DataStream.streamToOutputStream(strm, 
            //os);
            System.out);
            //os.close();
            Assertions.assertTrue(ret);
        } catch (final Exception e) {
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
            final DataStream ds = new DataStream(stis);
            Assertions.assertNotNull(ds);
            final Elements strm = ds.streamElements("table");
            Assertions.assertNotNull(strm);
            strm.stream().forEach(e -> {
                final Element e1 = e;
                final String msg = "Table: ." + e1.id() + "#" + e1.className();
                logger.info("");
                logger.info("Processing : {}", msg);
                System.out.println(msg);
                System.out.println(e1.html());
            });
        } catch (final Exception e) {
            Assertions.assertTrue(false);//This SHould NEVER happen
        }        
    }

    @Test
    public void streamColumnsTest(){
        try (StringToInputStream stis = StringToInputStream.toInputStream("src/test/resources/testcsv.txt")) {
            Assertions.assertNotNull(stis);
            Assertions.assertNotNull(stis.is);
            final DataStream ds = new DataStream(stis);
            Assertions.assertNotNull(ds);
            final List<String[]> strm = ds.streamColumns();
            Assertions.assertNotNull(strm);
            final boolean ret = DataStream.streamToOutputStream(strm.stream().map(ln -> Arrays.toString(ln)), System.out);
            Assertions.assertTrue(ret);
        } catch (final Exception e) {
            Assertions.assertTrue(false);//This SHould NEVER happen
        }        
    }

    public static class TestBean{
        private String col1;
        private String col2;
        private String col3;

        public String toString(){
            return col1 + "|" + col2 + "|" + col3;
        }

        public String getCol1() {
            return col1;
        }
        public void setCol1(String col1) {
            this.col1 = col1;
        }
        public String getCol2() {
            return col2;
        }
        public void setCol2(String col2) {
            this.col2 = col2;
        }
        public String getCol3() {
            return col3;
        }
        public void setCol3(String col3) {
            this.col3 = col3;
        }
    }
    @Test
    public void createBeansTest(){
        try (StringToInputStream stis = StringToInputStream.toInputStream("src/test/resources/testcsv.txt")) {
            Assertions.assertNotNull(stis);
            Assertions.assertNotNull(stis.is);
            final DataStream ds = new DataStream(stis);
            Assertions.assertNotNull(ds);
            final List<TestBean> strm = ds.createBeans(TestBean.class);
            Assertions.assertNotNull(strm);
            final boolean ret = DataStream.streamToOutputStream(strm.stream().map(bn -> bn.toString()), System.out);
            Assertions.assertTrue(ret);
        } catch (final Exception e) {
            Assertions.assertTrue(false);//This SHould NEVER happen
        }        
    }
}