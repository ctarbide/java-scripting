
package com.acme.labs;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {
    public static ByteBuffer
    getByteBufferFromFile(FileChannel fc) throws IOException {
        if (fc.size() > Integer.MAX_VALUE) {
            throw new IOException("file too large");
        }

        ByteBuffer b = ByteBuffer.allocate((int) fc.size());
        fc.read(b); // read from channel and write to buffer
        fc.close();
        b.flip();
        return b;
    }

    public static ByteBuffer
    getByteBufferFromFile(FileInputStream fis) throws IOException {
        return getByteBufferFromFile(fis.getChannel());
    }

    public static ByteBuffer
    getByteBufferFromFile(String file) throws IOException {
        return getByteBufferFromFile(new File(file));
    }

    public static ByteBuffer
    getByteBufferFromFile(File file) throws IOException {
        return getByteBufferFromFile(new FileInputStream(file));
    }

    public static ByteBuffer
    getByteBufferFromFile(URL file) throws IOException {
        return getByteBufferFromStream(file.openStream());
    }

    public static ByteBuffer
    getByteBufferFromStream(InputStream is) throws IOException {
        if (is instanceof FileInputStream) {
            return getByteBufferFromFile((FileInputStream) is);
        } else {
            ByteArrayOutputStream res = new ByteArrayOutputStream();
            byte[] bytes = new byte[0xffff];
            int numRead = 0;

            while ((numRead = is.read(bytes, 0, bytes.length)) >= 0) {
                res.write(bytes, 0, numRead);
            }

            return ByteBuffer.wrap(res.toByteArray());
        }
    }

    public static byte[]
    getByteArrayFromByteBuffer(ByteBuffer bb) {
        byte[] res;

        if (bb.position() == 0 && bb.limit() == bb.capacity()) {
            res = bb.array();
            assert res.length == bb.capacity();
            return res;
        }

        int length = bb.limit() - bb.position();
        assert length != bb.capacity();
        res = new byte[length];
        System.arraycopy(bb.array(), bb.position(), res, 0, length);
        return res;
    }

    /* see also: StringUtils.slurp/1
     */
    public static byte[]
    byteBufferToByteArray(ByteBuffer buf) {
        int pos = buf.position();
        int len = buf.limit() - pos;
        byte[] res = new byte[len];
        System.arraycopy(buf.array(), pos /* offset */, res, 0, len);
        return res;
    }

    public static String
    byteBufferToString(ByteBuffer buf, Charset charset) {
        int pos = buf.position();
        int len = buf.limit() - pos;
        return new String(buf.array(), pos /* offset */, len, charset);
    }

    public static void
    byteBufferToOutputStream(ByteBuffer buf, OutputStream out) throws IOException {
        int pos = buf.position();
        int len = buf.limit() - pos;
        out.write(buf.array(), pos /* offset */, len);
    }

    public static void
    byteBufferToFile(ByteBuffer buf, String out) throws IOException {
        OutputStream stream = null;

        try {
            stream = getBufferedFileOutputStream(out);
            byteBufferToOutputStream(buf, stream);
            stream.close();
        } finally {
            if (stream != null) {
                stream.close();
                stream = null;
            }
        }
    }

    public static boolean
    exists(String path) {
        return new File(path).exists();
    }

    public static boolean
    isDirectory(String path) {
        // javadoc says the 'exists' test is unnecessary
        File f = new File(path);
        return f.exists() && f.isDirectory();
    }

    public static long
    lastModified(String path) {
        return new File(path).lastModified();
    }

    public static boolean
    absentOrOlderThan(String left, String right) {
        File f = new File(left);
        return !f.exists() || f.lastModified() < lastModified(right);
    }

    public static boolean
    deleteFile(String path) {
        return new File(path).delete();
    }

    public static boolean
    renameFile(String from, String to) {
        return new File(from).renameTo(new File(to));
    }

    public static long
    fileSize(String path) throws IOException {
        return fileSize(new File(path));
    }

    public static long
    fileSize(File file) throws IOException {
        return fileSize(new FileInputStream(file));
    }

    public static long
    fileSize(FileInputStream fis) throws IOException {
        return fileSize(fis.getChannel());
    }

    public static long
    fileSize(FileChannel fc) throws IOException {
        return fc.size();
    }

    /* buffered file writer */

    public static BufferedWriter
    getBufferedFileWriter(String path, Charset charset, boolean append, int bufferSize)
    throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, append), charset), bufferSize);
    }

    public static BufferedWriter
    getBufferedFileWriter(String path, Charset charset, boolean append)
    throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, append), charset));
    }

    public static BufferedWriter
    getBufferedFileWriter(String path, Charset charset) throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, false /* append */), charset));
    }

    /* buffered file reader */

    public static BufferedReader
    getBufferedFileReader(String path, Charset charset, int bufferSize)
    throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(path), charset), bufferSize);
    }

    public static BufferedReader
    getBufferedFileReader(String path, Charset charset) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(path), charset));
    }

    /* file reader (when you buffer yourself, e.g., StringUtils.slurp/1) */

    public static Reader
    getFileReader(String path, Charset charset) throws FileNotFoundException {
        return new InputStreamReader(new FileInputStream(path), charset);
    }

    /* buffered output stream */

    public static OutputStream
    getBufferedFileOutputStream(String path, boolean append, int bufferSize)
    throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(path, append), bufferSize);
    }

    public static OutputStream
    getBufferedFileOutputStream(String path, boolean append) throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(path, append));
    }

    public static OutputStream
    getBufferedFileOutputStream(String path) throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(path, false /* append */));
    }

    public static OutputStream
    getBufferedFileOutputStream(File file, boolean append, int bufferSize)
    throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(file, append), bufferSize);
    }

    public static OutputStream
    getBufferedFileOutputStream(File file, boolean append) throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(file, append));
    }

    public static OutputStream
    getBufferedFileOutputStream(File file) throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(file, false /* append */));
    }

    public static void
    catFiles(Writer out, List<String> input, Charset charset) throws IOException {
        for (String path : input) {
            char[] buf = new char[8192];
            int got;
            Reader r = null;

            try {
                r = getFileReader(path, charset);

                while ((got = r.read(buf, 0, buf.length)) > 0) {
                    out.write(buf, 0, got);
                }
            } finally {
                if (r != null) {
                    r.close();
                }
            }
        }
    }

    public static void
    catFiles(OutputStream out, List<String> input) throws IOException {
        for (String path : input) {
            byte[] buf = new byte[8192];
            int got;
            InputStream r = null;

            try {
                r = new FileInputStream(path);

                while ((got = r.read(buf, 0, buf.length)) > 0) {
                    out.write(buf, 0, got);
                }
            } finally {
                if (r != null) {
                    r.close();
                }
            }
        }
    }

    public static void
    catStreams(OutputStream out, List<InputStream> input, boolean closeInput) throws IOException {
        for (InputStream r : input) {
            byte[] buf = new byte[8192];
            int got;

            try {
                while ((got = r.read(buf, 0, buf.length)) > 0) {
                    out.write(buf, 0, got);
                }
            } finally {
                if (closeInput && r != null) {
                    r.close();
                }
            }
        }
    }

    public static class PatternFilenameFilter implements FilenameFilter {
        private Pattern filter;
        private List<MatchResult> matchResults = new ArrayList<>();

        public
        PatternFilenameFilter(Pattern filter) {
            this.filter = filter;
        }

        public
        PatternFilenameFilter(String filter) {
            this.filter = Pattern.compile(filter);
        }

        @Override
        public boolean
        accept(File dir, String name) {
            Matcher matcher = filter.matcher(name);
            boolean found = matcher.find();

            if (found) {
                matchResults.add(matcher.toMatchResult());
            }

            return found;
        }

        public List<MatchResult>
        list(File dir) {
            assert dir.list(this).length == matchResults.size();
            return matchResults;
        }
    }
}
