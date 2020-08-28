package nd.data.stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringToInputStreamTest {
    public static final String WIKIURL = "https://en.wikipedia.org/wiki/Slashdot";
    @Test
    public void toInputStreamFileExistsTest() {
        try (StringToInputStream stis = StringToInputStream.toInputStream("src/test/resources/testcsv.txt")) {
            Assertions.assertNotNull(stis);
            Assertions.assertNotNull(stis.is);
        } catch (Exception e) {
            Assertions.assertTrue(false);//This SHould NEVER happen
        }
    }
    @Test
    public void toInputStreamFileNotExistsTest() {
        try (StringToInputStream stis = StringToInputStream.toInputStream("testcsv.txt")) {
            Assertions.assertTrue(false);//This SHould NEVER happen
        } catch (Exception e) {
            Assertions.assertTrue(true);//This SHould ALWAYS happen
        }
    }
    @Test
    public void toInputStreamResourceExistsTest() {
        try (StringToInputStream stis = StringToInputStream.toInputStream("classpath:/testcsv.txt")) {
            Assertions.assertNotNull(stis);
            Assertions.assertNotNull(stis.is);
        } catch (Exception e) {
            Assertions.assertTrue(false);//This SHould NEVER happen
        }
    }
    @Test
    public void toInputStreamResourceNotExistsTest() {
        try (StringToInputStream stis = StringToInputStream.toInputStream("classpath:/testcsv1.txt")) {
            //Assertions.assertNotNull(stis);
        } catch (Exception e) {
            Assertions.assertTrue(true);//This SHould ALWAYS happen
        }
    }
    @Test
    public void toInputStreamHttpsNotExistsTest() {
        try (StringToInputStream stis = StringToInputStream.toInputStream(WIKIURL + "123")) {
            //Assertions.assertNotNull(stis);
        } catch (Exception e) {
            Assertions.assertTrue(true);//This SHould ALWAYS happen
        }
    }
    @Test
    public void toInputStreamHttpsExistsTest() {
        try (StringToInputStream stis = StringToInputStream.toInputStream(WIKIURL)) {
            Assertions.assertNotNull(stis);
            Assertions.assertNotNull(stis.is);
        } catch (Exception e) {
            Assertions.assertTrue(false);//This SHould NEVER happen
        }
    }
}