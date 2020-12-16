package nd.data.stream;

import java.io.Closeable;
import java.io.IOException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class ThymeleafSink implements Closeable{
	private static final Logger logger = LoggerFactory.getLogger(ThymeleafSink.class);
	private TemplateEngine engine = new TemplateEngine();
	private Context context = new Context(Locale.US);
	
	public String load(final String templateName) {
		logger.info("Processing Template: {}", templateName);
		return engine.process(templateName, context);
	}
	
	@Override
	public void close() throws IOException {
	}
}
