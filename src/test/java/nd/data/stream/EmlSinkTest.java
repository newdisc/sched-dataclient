package nd.data.stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EmlSinkTest {
	public static final String TESTEML = "src/test/resources/testeml.eml";
	@Test
	public void testLoad() {
		final EmlSink emlsink = new EmlSink();
		boolean ret = emlsink.load(TESTEML);
		Assertions.assertTrue(ret);
	}
	@Test
	public void testLoadSave() {
		final EmlSink emlsink = new EmlSink();
		boolean ret = emlsink.load(TESTEML);
		Assertions.assertTrue(ret);
		ret = emlsink.save("out.eml");
		Assertions.assertTrue(ret);
	}
}
