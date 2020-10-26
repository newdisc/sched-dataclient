package nd.data.stream;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSoupStream implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(JSoupStream.class);

    protected StringToInputStream iStream;
    protected String tableSelector;
    protected String headerSelector;
    protected String rowsSelector;
    protected String columnSelector;
    protected Elements currentElements;
    protected Map<String, Integer> header;
    
    public void loadStream() {
    	currentElements = streamElements(tableSelector);
    	if (null != headerSelector && ! headerSelector.isEmpty()) {
    		processFirstHeader();
    	}
    }
    
    public Stream<List<String>> streamLines() {
    	return streamRows()
    		.map(e  -> e.select(columnSelector))
    		.map(es -> es.stream().map(Element::text).collect(Collectors.toList()));
    }
    
    public Stream<Element> streamRows(){
    	return currentElements.stream()
    		.map(e -> e.select(rowsSelector).stream())
    		.reduce(Stream.empty(), Stream::concat);
    }
    
    protected void processFirstHeader() {
        final Element e1 = currentElements.get(0);
        final Elements hdrs = e1.select(headerSelector);
        header = IntStream.range(0, hdrs.size())
            .boxed()
            .collect(Collectors.toMap(idx -> hdrs.get(idx).text(), idx -> idx));
    }

    private Elements streamElements(final String selector){
        try (final InputStream is = iStream.getInputStream();) {
            Document d = Jsoup.parse(is, "UTF-8", "");
            return d.select(selector);
        } catch (IOException e) {
            final String msg = "Issue reading Elements " + iStream.getURI();
            logger.error(msg, e);
            return null;
        }
    }

	@Override
	public void close() throws IOException {
		//currentElements = null;//Allow access after try-resource-close
        if (null != iStream) {
            iStream.close();
        }
	}

	public void setTableSelector(String tableSelector) {
		this.tableSelector = tableSelector;
	}
	public void setHeaderSelector(String headerSelector) {
		this.headerSelector = headerSelector;
	}
	public void setRowsSelector(String rowsSelector) {
		this.rowsSelector = rowsSelector;
	}
	public void setColumnSelector(String columnSelector) {
		this.columnSelector = columnSelector;
	}
	public void setiStream(StringToInputStream iStream) {
		this.iStream = iStream;
	}
	public Map<String, Integer> getHeader() {
		return header;
	}
}
