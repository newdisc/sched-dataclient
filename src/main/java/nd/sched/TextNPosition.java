package nd.sched;

import org.apache.pdfbox.text.TextPosition;

public class TextNPosition{
    String text;
    TextPosition position;
    public TextNPosition(String s, TextPosition p) {
            text = s;
            position = p;
    }
    public static int nearbyText(TextNPosition r1, TextNPosition r2) {
        final int XDIFFCONCAT = 5;
        TextPosition r1p = r1.position;
        TextPosition r2p = r2.position;
        float r1x = r1p.getX();
        float r2x = r2p.getX();
        if (Math.abs(r2x - r1x ) < XDIFFCONCAT) { // close
            return Float.valueOf(r2p.getEndY())
                .compareTo(Float.valueOf(r1p.getEndY())); // Y scale is inverted
        }
        return Float.valueOf((r1x))
            .compareTo(Float.valueOf(r2x));
    }
}