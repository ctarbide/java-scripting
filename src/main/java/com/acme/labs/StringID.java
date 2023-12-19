
package com.acme.labs;

import java.math.BigInteger;
import java.nio.charset.Charset;

public class StringID extends BigInteger implements Cloneable {
    private static final long serialVersionUID = 4637531275879698456L;

    public static final Charset charsetUTF8 = Charset.forName("UTF-8");

    private String _id;

    public static final int RADIX = Character.MAX_RADIX; /* this is expected to be 36 */

    static {
        if (RADIX != Math.abs(36)) { // to avoid dead code warning
            throw new IllegalStateException();
        }
    }

    public
    StringID(byte b[]) {
        super(1 /* signum, 1=always positive */, _verify(b) /* magnitude */);
        _id = super.toString(RADIX);
    }

    public
    StringID(String s) {
        this(s.getBytes(charsetUTF8));
    }

    private
    StringID(BigInteger i) {
        super(_verify(i).toByteArray());
        _id = super.toString(RADIX);
    }

    private static byte[]
    _verify(byte b[]) {
        if (b.length == 0) {
            throw new IllegalArgumentException("empty byte array");
        }

        return b;
    }

    private static BigInteger
    _verify(BigInteger i) {
        if (i.signum() < 0) {
            throw new IllegalArgumentException("negative value");
        }

        return i;
    }

    @Override
    public String
    toString() {
        return _id;
    }

    @Override
    public String
    toString(int radix) {
        throw new RuntimeException("not available");
    }

    public String
    getSource() {
        byte[] b = toByteArray();

        if (b.length > 1 && b[0] == 0) {
            /*
             * discards the zero most significant byte that ensure the value is positive (we
             * don't use negative values), see StringID(byte b[]) constructor
             */
            return new String(b, 1, b.length - 1, charsetUTF8);
        }

        return new String(b, charsetUTF8);
    }

    public static String
    toString(String s) {
        return new StringID(s).toString();
    }

    public static StringID
    valueOf(String s) {
        return new StringID((new BigInteger(s, RADIX)));
    }

    public static StringID
    valueOf(StringID s) {
        return new StringID((BigInteger) s);
    }

    @Override
    protected StringID
    clone() throws CloneNotSupportedException {
        return new StringID(toByteArray());
    }
}
