package nd.data.stream;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EmlSinkTest {
	public static final String TESTEML = "src/test/resources/testeml.eml";
	@Test
	public void testLoad() {
		try (final EmlSink emlsink = new EmlSink();){
		boolean ret = emlsink.load(TESTEML);
		Assertions.assertTrue(ret);
		} catch (IOException e) {
			Assertions.assertTrue(false);
		}
	}
	@Test
	public void testLoadSave() {
		try (final EmlSink emlsink = new EmlSink();){
		boolean ret = emlsink.load(TESTEML);
		Assertions.assertTrue(ret);
		ret = emlsink.save("out.eml");
		Assertions.assertTrue(ret);
		} catch (IOException e) {
			Assertions.assertTrue(false);
		}
	}
}
