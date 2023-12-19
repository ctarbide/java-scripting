
package com.acme.labs;

public class HtmlUtils {
    public static char
    translateEntity(String entity) {
        char res =
            entity.equals("amp") ? '&' :
            entity.equals("lt") ? '<' :
            entity.equals("gt") ? '>' :
            entity.equals("quot") ? '"' :
            entity.equals("apos") ? '\'' :
            0;

        if (res != 0) {
            return res;
        }

        int len = entity.length();

        if (len == 0) {
            return 0;
        }

        if (entity.charAt(0) == '#') {
            if (entity.charAt(1) == 'x' && len <= 4 /* #xHH */) {
                res = (char)Integer.parseInt(entity.substring(2), 16);
            } else if (len <= 4 /* #DDD */) {
                res = (char)Integer.parseInt(entity.substring(1));
            } else {
                System.err.println("WARN: unknown numeric entity \"" + entity + "\"");
            }
        }

        if (res < 32 || res >= 127) {
            return 0;
        }

        return res;
    }

    public static char
    translateEntity(CharSequence entity) {
        if (entity instanceof String) {
            return translateEntity((String)entity);
        }

        throw new IllegalArgumentException("unknown instance of char sequence");
    }

    public static CharSequence
    unescapeHtmlEntities(CharSequence input) {
        StringBuilder buf = null;
        int mark = 0;
        int len = input.length();

        for (int k = 0; k < len; k++) {
            char cp = input.charAt(k);

            if (cp == '&') {
                int semicolon = StringUtils.indexOf(input, ";", k + 1);

                if (semicolon == -1) {
                    k = len - 1; // last k
                    continue;
                }

                char out = translateEntity(input.subSequence(k + 1, semicolon));

                if (out == 0) {
                    k = semicolon; // skip the whole shebang
                    continue;
                }

                if (buf == null) {
                    buf = new StringBuilder(len);
                }

                if (mark < k) {
                    buf.append(input, mark, k);    // else nothing to add
                }

                buf.append(out);
                k = semicolon;
                mark = k + 1;
            }
        }

        if (buf == null) {
            return input;
        }

        if (mark < len) {
            buf.append(input, mark, len);
        }

        return buf;
    }

    public static CharSequence
    escapeHtmlEntities1(CharSequence input) {
        StringBuilder buf = null;
        int mark = 0;
        int len = input.length();

        for (int k = 0; k < len; k++) {
            String out;

            switch (input.charAt(k)) {
            case '&':
                out = "&amp;";
                break;

            case '<':
                out = "&lt;";
                break;

            default:
                continue;
            }

            if (buf == null) {
                buf = new StringBuilder(len);
            }

            if (mark < k) {
                buf.append(input, mark, k);    // else nothing to add
            }

            buf.append(out);
            mark = k + 1;
        }

        if (buf == null) {
            return input;
        }

        if (mark < len) {
            buf.append(input, mark, len);
        }

        return buf;
    }

    /*
    public static void
    main(String[] args) {
        assert StringUtils.toString(escapeHtmlEntities1("ampersand is & and less-than is <")).equals("ampersand is &amp; and less-than is &lt;");
        assert StringUtils.toString(escapeHtmlEntities1("&<")).equals("&amp;&lt;");
        assert StringUtils.toString(escapeHtmlEntities1("<&")).equals("&lt;&amp;");
        assert StringUtils.toString(escapeHtmlEntities1("<<&&")).equals("&lt;&lt;&amp;&amp;");
        assert StringUtils.toString(escapeHtmlEntities1("&&<<")).equals("&amp;&amp;&lt;&lt;");
        assert StringUtils.toString(escapeHtmlEntities1(" & & < < ")).equals(" &amp; &amp; &lt; &lt; ");
    }
    */
}
