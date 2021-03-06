package org.makumba.parade.tools;

/**
 * This class provides utility methods to display numeric values in a nicer way to the user.
 * 
 * @author Cristian Bogdan
 * @author Manuel Gay
 * @version $id
 */
public class DisplayFormatter {

    /** Format byte size in nice format. */
    public static String readableBytes(long byteSize) {
        if (byteSize < 0l)
            return ("invalid");
        if (byteSize < 1l)
            return ("empty");

        float byteSizeF = (new java.lang.Float(byteSize)).floatValue();
        String unit = "bytes";
        float factor = 1f;
        String[] desc = { "B", "kB", "MB", "GB", "TB" };

        java.text.DecimalFormat nf = new java.text.DecimalFormat();
        nf.setMaximumFractionDigits(1);
        nf.setGroupingUsed(true);

        String value = nf.format(byteSizeF);

        int i = 0;
        while (i + 1 < desc.length && (value.length() > 4 || (value.length() > 3 && value.indexOf('.') < 0))) {
            i++;
            factor = factor * 1024l;
            value = nf.format(byteSizeF / factor);
        }
        if (value.charAt(0) == '0' && i > 0) { // go one back if a too-big
            // scale is used
            value = nf.format(java.lang.Math.round(1024 * byteSizeF / factor));
            i--;
        }

        if (value.length() > 3 && value.indexOf('.') > 0) // sut decimals on
            // large numbers
            value = value.substring(0, value.indexOf('.'));

        unit = desc[i];
        return (value + " " + unit);
    }

    /** lentgh of time periods in nice format. */
    public static String readableTime(long milis) {
        // simplest implementation:
        // return((new Long(secs)).toString())+" seconds";
        long secs = milis / 1000l;

        if (secs < 2l)
            return ("1 second");
        if (secs == 2l)
            return ("2 seconds");

        // default:
        long value = secs; // new Long(secs);
        String unit = "seconds";

        // now try to give it a meaning:

        long[] breaks = { 31536000, 2628000, 604800, 86400, 3600, 60, 1 };
        String[] desc = { "year", "month", "week", "day", "hour", "minute", "second" };

        int i = 0;
        while (i <= breaks.length && secs <= (2 * breaks[i])) {
            i++;
        }
        // i=i-1;
        // long break=breaks[i];
        value = secs / breaks[i];
        unit = desc[i];
        if (value >= 2)
            unit = unit + "s";

        String retval = value + " " + unit;

        // if...

        return (retval);
    }

}
