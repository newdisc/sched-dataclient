package nd.data.stream;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class CsvStream implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(CsvStream.class);
    protected StringToInputStream iStream;
    protected BufferedReader reader;

    public Stream<List<String>> streamLines() {
        try (final BufferedReader br = setReader();
                final CSVReader cr = new CSVReader(br);) {
            return cr.readAll().stream().map(la -> Arrays.asList(la));
        } catch (IOException | CsvException e) {
            final String msg = "Issue reading Lines " + iStream.getURI();
            logger.error(msg, e);
        }
        return Stream.empty();
    }

    public void loadStream() {
    	setReader();
    }
    
    private BufferedReader setReader() {
        final InputStreamReader is = new InputStreamReader(iStream.getInputStream());
        final BufferedReader br = new BufferedReader(is);
        reader = br;
        return br;
    }
    
    public static Map<String, Integer> headerMap(List<String> header) {
    	return IntStream.range(0,  header.size()).boxed()
    			.collect(Collectors.toMap(idx -> header.get(idx), idx -> idx));
    }

	@Override
	public void close() throws IOException {
        if (null != iStream) {
            iStream.close();
        }
	}

	public void setiStream(StringToInputStream iStream) {
		this.iStream = iStream;
	}
}
