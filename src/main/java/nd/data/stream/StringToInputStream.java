package nd.data.stream;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringToInputStream implements Closeable {
    private static Logger logger = LoggerFactory.getLogger(StringToInputStream.class);
    protected InputStream is;
    protected String uri;

    public static StringToInputStream toInputStream(String uristr) throws Exception {
        final StringToInputStream stis = new StringToInputStream();
        logger.info("searching for : {}", uristr);
        if (2 > uristr.indexOf(":")) {
            uristr = "file:" + uristr;
        }
        try {
            URI uri = new URI(uristr);
            stis.uri = uristr;
            if ("classpath".equals(uri.getScheme())) {
                stis.is = StringToInputStream.class.getResourceAsStream(uri.getPath());
                return stis;
            }
            stis.is = uri.toURL().openStream();
        } catch (URISyntaxException | IOException e1) {
            logger.error("Problem accessing URI: {}", uristr, e1);
            throw e1;
        }
        return stis;
    }

    @Override
    public void close() throws IOException {
        if (null != is) {
            is.close();
        }
    }

    public InputStream getInputStream(){
        return is;
    }
    public String getURI(){
        return uri;
    }
}