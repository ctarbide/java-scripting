
package com.acme.labs;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Objects;

public class StringID extends BigInteger implements Cloneable {
    private static final long serialVersionUID = 4637531275879698456L;

    public static final Charset charsetUTF8 = Charset.forName("UTF-8");

    private String _id;

    public static final int RADIX = 36;

    public
    StringID(byte b[]) {
        super(1 /* signum, 1=always positive */, _verify(b) /* magnitude */);
        _id = super.toString(RADIX);
    }

    private static byte[]
    _validateAndGetBytes(String s) {
        Objects.requireNonNull(s, "Input string cannot be null");
        return s.getBytes(charsetUTF8);
    }

    public
    StringID(String s) {
        this(_validateAndGetBytes(s));
    }

    private
    StringID(BigInteger i) {
        super(_verify(i).toByteArray());
        _id = super.toString(RADIX);
    }

    private static byte[]
    _verify(byte b[]) {
        if (b.length == 0) {
            throw new IllegalArgumentException("Input is empty");
        }
        if (b.length > 1024) {
            throw new IllegalArgumentException("Input bytes exceeds maximum length of 1024");
        }

        return b;
    }

    private static BigInteger
    _verify(BigInteger i) {
        if (i.signum() < 0) {
            throw new IllegalArgumentException("Negative value");
        }

        return i;
    }

    @Override
    public String
    toString() {
        if (_id == null) { /* _id maybe null after deserialization */
            _id = super.toString(RADIX);
        }
        return _id;
    }

    @Override
    public String
    toString(int radix) {
        throw new UnsupportedOperationException("Custom radix not supported, StringID uses a fixed RADIX of 36 for a one-to-one representation");
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
