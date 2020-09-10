package nd.data.stream;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvException;

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

    public List<String[]> streamColumns() {
        try (final BufferedReader br = setReader();
                final CSVReader cr = new CSVReader(br);) {
            return cr.readAll();
        } catch (IOException | CsvException e) {
            final String msg = "Issue reading Lines " + iStream.getURI();
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

    private BufferedReader setReader() {
        final InputStreamReader is = new InputStreamReader(iStream.getInputStream());
        final BufferedReader br = new BufferedReader(is);
        reader = br;
        return br;
    }
}
