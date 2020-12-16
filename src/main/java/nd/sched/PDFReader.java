package nd.sched;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PDFReader extends PDFTextStripper{
    private final static Logger logger = LoggerFactory.getLogger(PDFReader.class);
    private int gapRows;
    private final String start;
    private final String end;
    private boolean print = false;
    private double endY = 99999999;
    private int nCols = -1;
    private List<TextNPosition> curRow = new ArrayList<>();

    public PDFReader(final String s, final String e) throws IOException {
        super();
        start = s;
        end = e;
    }
    @Override
    protected void endPage(PDPage page) throws IOException {
        if (curRow.size() >= nCols) {
            completeLine();
        }
    }

    @Override
    protected void startPage(PDPage page) throws IOException {
        endY = 99999999;
        //curRow.forEach(r -> r.position.);//Y has to be reset - this is TO DO - need own member
    }

    @Override
    protected void writeString(String str, List<TextPosition> curRowitions) throws IOException {
        final int YDIFFROW = gapRows;//2;
        TextPosition tpz = curRowitions.get(0);
        float cey = tpz.getEndY();
        float fh = tpz.getFontSizeInPt();
        if (Math.abs(endY - cey) > (fh + YDIFFROW)
            ) {//New Row
            if (curRow.size() >= nCols) {
                completeLine();
            }
            endY = cey;
        }
        curRow.add(new TextNPosition(str, tpz));

        curRowitions.forEach(pos -> {
            //logger.info("Position: {}, {}", pos.getEndX(), pos.getEndY(), pos.getFontSizeInPt());
        });
    }


    private void completeLine() {
        final int XDIFFCONCAT = 5;
        final String SEP = ",";
        Collections.sort(curRow, TextNPosition::nearbyText);
        StringBuilder sb = new StringBuilder();
        if (!curRow.isEmpty()) {
        sb.append(curRow.get(0).text);
        for ( int i = 1; i < curRow.size(); i++) {
            float r1x = curRow.get(i).position.getX();
            float r2x = curRow.get(i-1).position.getX();
            if (Math.abs(r2x - r1x ) > XDIFFCONCAT) { // close
                sb.append(SEP);
            } else {
                sb.append(" ");
            }
            sb.append(curRow.get(i).text);
        }
        }
        final String curLine = sb.toString();
        if (!print && curLine.contains(start)) {
            print = true;
        }
        if (print) {
            logger.info(curLine);
            if (-1 == nCols && 0 != curRow.size()) {
                nCols = curRow.size();
            }
        }
        if (print && curLine.contains(end)) {
            print = false;
            nCols = -1;//TO DO - reset here?
        }
        final StringBuilder sb1 = new StringBuilder();
        IntStream.range(0, curRow.size()).boxed().forEach(idx -> {
            sb1.append("\n Text: ");
            sb1.append(curRow.get(idx).text);
            sb1.append(" X: ");
            sb1.append(curRow.get(idx).position.getX());
            sb1.append(" Y: ");
            sb1.append(curRow.get(idx).position.getY());
            sb1.append(" EX: ");
            sb1.append(curRow.get(idx).position.getEndX());
            sb1.append(" EY: ");
            sb1.append(curRow.get(idx).position.getEndY());
            sb1.append("|||");
        });
        if (print) {
            //logger.info(sb1.toString());
        }
        //logger.info(sb.toString());
        curRow.clear();
    }

    protected static void processFile(final String filepath, final String start, final String end) {
    try (PDDocument document = PDDocument.load(new File(filepath));
        Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
    ) {
        PDFReader stripper = new PDFReader(start, end);
        stripper.setSortByPosition( true );
        stripper.setStartPage( 0 );
        stripper.setEndPage( document.getNumberOfPages() + 1);
        stripper.setGapRows(
            //5);
            2);

        stripper.writeText(document, dummy);        
    } catch (IOException e) {
        logger.error("Problem reading file: {}", filepath, e);
    }
    }

    public static void main(String[] args) throws IOException {
        final String filepath = args[0];
        processFile(filepath, 
        "Settlement Date", "Total");
        //"Description", "INVALID");
    }

    public int getGapRows() {
        return gapRows;
    }

    public PDFReader setGapRows(int gapRows) {
        this.gapRows = gapRows;
        return this;
    }
}