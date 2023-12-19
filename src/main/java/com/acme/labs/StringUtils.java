
package com.acme.labs;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String
    join(Collection<?> s, String delimiter) {
        if (s.isEmpty()) {
            return "";
        }

        Iterator<?> iter = s.iterator();
        StringBuilder buffer = new StringBuilder(String.valueOf(iter.next()));

        while (iter.hasNext()) {
            buffer.append(delimiter).append(iter.next());
        }

        return buffer.toString();
    }

    public static String
    join(Map<?, ?> m, String delimiter, String afterKey, String afterValue) {
        if (m.isEmpty()) {
            return "";
        }

        Iterator<? extends Map.Entry<?, ?>> iter = m.entrySet().iterator();
        Map.Entry<?, ?> i = iter.next();
        StringBuilder buffer = new StringBuilder();
        buffer.append(i.getKey()).append(afterKey).append(i.getValue()).append(afterValue);

        while (iter.hasNext()) {
            i = iter.next();
            buffer.append(delimiter)
            .append(i.getKey()).append(afterKey).append(i.getValue()).append(afterValue);
        }

        return buffer.toString();
    }

    public static String
    bothEnds(String s, String sep, int len) {
        assert len > 0;
        int requiredLen = len * 2;

        if (s.length() <= requiredLen) {
            return s;
        }

        String a = s.substring(0, len);
        String b = s.substring(s.length() - len);
        return (new StringBuilder(requiredLen + sep.length())).append(a).append(sep).append(b).toString();
    }

    public static String
    shortStringID(StringID id) {
        return bothEnds(id.toString(), "-", 5);
    }

    public static String
    shortStringID(String s) {
        return bothEnds(StringID.toString(s), "-", 5);
    }

    public static StringBuilder
    slurp(Reader in) throws IOException {
        StringBuilder res = new StringBuilder();
        char[] buf = new char[8192];
        int got;

        while ((got = in.read(buf, 0, buf.length)) > 0) {
            res.append(buf, 0, got);
        }

        return res;
    }

    public static StringBuilder
    slurp(String path, Charset charset) throws IOException {
        Reader r = null;

        try {
            r = FileUtils.getFileReader(path, charset);
            return slurp(r);
        } finally {
            if (r != null) {
                r.close();
            }
        }
    }

    public static String
    formatTiming(long ns) {
        // unit, milli, micro, nano
        //    1,  10e3,  10e6, 10e9
        StringBuilder res = new StringBuilder();

        if (ns >= (1000000000L * 60L * 60L)) { // ge 1 hour
            long secs = ns / 1000000000L;
            long mins = secs / 60L;
            res.append(mins / 60L) // hours
            .append("h")
            .append(mins % 60L) // mins
            .append("m")
            .append(secs % 60L) // secs
            .append("s");
        } else if (ns >= (1000000000L * 60L)) { // ge 1 min
            long secs = ns / 1000000000L;
            res.append(secs / 60L) // mins
            .append("m")
            .append(secs % 60L) // secs
            .append("s");
        } else if (ns >= 1000000000L) { // ge 1 second
            long x = (ns % 1000000000L) / 1000000L;
            res.append(ns / 1000000000L) // seconds
            .append(x > 0 ? String.format(".%03ds", x) : ".0s"); // millis
        } else if (ns >= 1000000L) { // ge 1 milli
            long x = (ns % 1000000L) / 1000L;
            res.append(ns / 1000000L) // millis
            .append(x > 0 ? String.format(".%03dms", x) : ".0ms"); // micros
        } else if (ns >= 1000L) { // ge 1 micro
            long x = ns % 1000L;
            res.append(ns / 1000L) // micros
            .append(x > 0 ? String.format(".%03dus", x) : ".0us"); // nanos
        } else {
            res.append(ns).append("ns");
        }

        return res.toString();
    }

    /* reference: http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
     */
    private static final char[] _hexChars = "0123456789abcdef".toCharArray();
    public static char[]
    bytesToHex(byte[] x) {
        char[] res = new char[x.length * 2];

        for (int i = 0, j = x.length; i < j; i++) {
            int k = x[i] & 0xff;
            res[i * 2] = _hexChars[k >>> 4];
            res[i * 2 + 1] = _hexChars[k & 0x0f];
        }

        return res;
    }

    public static String
    toString(CharSequence input) {
        if (input instanceof String) {
            return (String)input;
        }

        if (input instanceof StringBuilder) {
            return ((StringBuilder)input).toString();
        }

        throw new IllegalArgumentException("unknown instance of char sequence");
    }

    /* reference: http://stackoverflow.com/questions/6972769/indexof-on-charsequence-implementations-that-support-it
     */
    public static int
    indexOf(CharSequence input, String needle, int fromIndex) {
        if (input instanceof String) {
            return ((String)input).indexOf(needle, fromIndex);
        }

        if (input instanceof StringBuilder) {
            return ((StringBuilder)input).indexOf(needle, fromIndex);
        }

        throw new IllegalArgumentException("unknown instance of char sequence");
    }

    public static CharSequence
    singlespace(CharSequence line) {
        int k1 = 0;
        int len = line.length();
        StringBuilder buf = null;
        char cp0 = 0;

        for (int k = 0; k < len; k++) {
            char cp = line.charAt(k);

            switch (cp) {
            case ' ':
            case '\t':
            case '\r':
            case '\n':
                if (k > k1) {
                    if (buf == null) {
                        buf = new StringBuilder(len);
                    }

                    buf.append(line, k1, k); // save so far, k is exclusive
                }

                k1 = k + 1;
                cp0 = cp;
                break;

            default:
                if (k == k1) { /* cp0 is the previous space char */
                    switch (cp0) {
                    case ' ':
                    case '\t':
                    case '\r':
                    case '\n':
                        if (buf == null) {
                            buf = new StringBuilder(len);
                        }

                        buf.append(' ');
                        break;

                    default:
                        break;
                    }
                }
            }
        }

        if (buf == null) {
            return line;
        }

        if (k1 < len) {
            buf.append(line, k1, len);
        }

        return buf;
    }

    /*
    public static void
    main(String[] args) {
        assert formatTiming(1).equals("1ns");
        assert formatTiming(10).equals("10ns");
        assert formatTiming(100).equals("100ns");
        assert formatTiming(999).equals("999ns");
        assert formatTiming(1000).equals("1.0us");
        assert formatTiming(1001).equals("1.001us");
        assert formatTiming(1000000).equals("1.0ms");
        assert formatTiming(1000999).equals("1.0ms");
        assert formatTiming(1001000).equals("1.001ms");
        assert formatTiming(999999999L).equals("999.999ms");
        assert formatTiming(1000000000L).equals("1.0s");
        assert formatTiming(1000000000L * 60L).equals("1m0s");
        assert formatTiming(1000000000L * 61L).equals("1m1s");
        assert formatTiming(1000000000L * 3599L).equals("59m59s");
        assert formatTiming(1000000000L * 3599L + 1L).equals("59m59s");
        assert formatTiming(1000000000L * 3599L + 999999999L).equals("59m59s");
        assert formatTiming(1000000000L * 3600L).equals("1h0m0s");
        assert formatTiming(1000000000L * 3661L).equals("1h1m1s");
    }
    */
}
