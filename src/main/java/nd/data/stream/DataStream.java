package nd.data.stream;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataStream implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(DataStream.class);
    protected StringToInputStream iStream;
    protected Reader reader;

    public Stream<String> streamLines() {
        final BufferedReader br = setReader();
        return br.lines();
    }
    
    public static List<Integer> adaptInterestIndex(List<String> colsInterest, Map<String, Integer> hdrToIdx){
    	return colsInterest
    			.stream()
    			.map(hdr -> hdrToIdx.get(hdr))
    			.collect(Collectors.toList());
    }
    public static Stream<List<String> > adaptInterestHeader(List<Integer> colsInterest, Stream<List<String>> original) {
    	return original.map(line -> {
    		int lsz = line.size();
    		return colsInterest.stream().map(idx -> {
    			if (null == idx || idx > lsz || idx < 0) {
    				logger.debug("Out of range index: {}, {}", idx, line);
    				return null;
    			}
    			return line.get(idx);
    		})
    		.collect(Collectors.toList());
    	});
    }

    public <T> List<T> createBeans(final Class<T> typeClass) {
        final HeaderColumnNameMappingStrategy<T> hcnms = new HeaderColumnNameMappingStrategy<>();
        hcnms.setType(typeClass);

        try (final BufferedReader br = setReader();) {
            return new CsvToBeanBuilder<T>(br).withMappingStrategy(hcnms).build().parse();
        } catch (final IOException e) {
            final String msg = "Issue reading Beans " + iStream.getURI();
            logger.error(msg, e);
        }
        return new ArrayList<>();
    }

    public static boolean streamToOutputStream(final Stream<String> sts, final OutputStream os) {
        sts.forEach(ln -> {
            try {
                os.write(ln.getBytes());
                os.write("\n".getBytes());
                // sts.close(); // Note: close has no effect on the forEach loop - no early
                // exits
            } catch (final IOException e) {
                logger.error("Could NOT write to Stream", e);
                throw new RuntimeException("Error Writing to stream", e);
            }
        });
        return true;
    }

    private BufferedReader setReader() {
        final InputStreamReader is = new InputStreamReader(iStream.getInputStream());
        final BufferedReader br = new BufferedReader(is);
        reader = br;
        return br;
    }

    @Override
    public void close() throws IOException {
        if (null != reader) {
            reader.close();
        }
        if (null != iStream) {
            iStream.close();
        }
    }

    public DataStream(final StringToInputStream is) {
        iStream = is;
    }
}
