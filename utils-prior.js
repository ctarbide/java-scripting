
/* -*- mode: javascript; -*- */

/*jslint white, for, this */

var exports;
var require;
var java;

(function(exports){
    "use strict";

    var charsetUTF8 = java.nio.charset.Charset.forName('UTF-8');
    var bufferSize = 0x100000; /* 0x100000 = 1MB */

    function writeString(outputFilepath, str, makeDirs) {
        var file = new java.io.File(outputFilepath);
        if (makeDirs) {
            file.getParentFile().mkdirs();
        }
        var pw = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(file), charsetUTF8), bufferSize));
        try {
            pw.write(str);
        } finally {
            pw.close();
        }
    }

    function appendString(outputFilepath, str) {
        var pw = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(outputFilepath, true /* append? */), charsetUTF8), bufferSize));
        try {
            pw.write(str);
        } finally {
            pw.close();
        }
    }

    function writeJSON(outputFilepath, obj) {
        var pw = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(outputFilepath), charsetUTF8), bufferSize));
        try {
            pw.write(JSON.stringify(obj));
        } finally {
            pw.close();
        }
    }

    function getFileReader(path, charset) {
        return new java.io.InputStreamReader(new java.io.FileInputStream(path), charset || charsetUTF8);
    }

    function getBufferedFileReader(path, charset, bufferSize) {
        return new java.io.BufferedReader(
            new java.io.InputStreamReader(new java.io.FileInputStream(path), charset || charsetUTF8),
            bufferSize || 8192
        );
    }

    function readFileLines(path, charset) {
        var reader = getBufferedFileReader(path, charset);
        var line;
        var res = [];
        while (true) {
            line = reader.readLine();
            if (!line) {
                break;
            }
            res.push(line);
        }
        return res;
    }

    function getByteBufferFromFile(path) {
        var fc;
        var b;  // ByteBuffer
        fc = new java.io.FileInputStream(new java.io.File(path)).getChannel();
        if (fc.size() > java.lang.Integer.MAX_VALUE) {
            throw new java.lang.IOException("file too large");
        }
        b = java.nio.ByteBuffer.allocate(fc.size());
        fc.read(b); // read from channel and write to buffer
        fc.close();
        b.flip();
        return b;
    }

    function byteBufferToJavaString(buf, charset) {
        var pos;
        var len;
        pos = buf.position();
        len = buf.limit() - pos;
        return new java.lang.String(buf.array(), pos /* offset */, len, charset || charsetUTF8);
    }

    function fileToJavaString(path, charset) {
        return byteBufferToJavaString(getByteBufferFromFile(path), charset || charsetUTF8);
    }

    function readJSON(inputFilepath) {
        return JSON.parse(byteBufferToJavaString(getByteBufferFromFile(inputFilepath)));
    }

    /* return [0,max) */
    function randDouble(max) {
        return Math.random() * max;
    }

    function trunc(n) {
        return n - n % 1;
    }

    function numpad2(x) { return ('00' + x).slice(-2); }
    function numpad3(x) { return ('000' + x).slice(-3); }
    function numpad5(x) { return ('00000' + x).slice(-5); }
    function numpad(x, len) {
        if (len < 0) {
            throw 'len < 0';
        }
        if (len > 30) {
            throw 'len > 30';
        }
        return ('000000000000000000000000000000' + x).slice(-len);
    }

    /* return [0,max) */
    function randInteger(max) {
        return trunc(randDouble(max));
    }

    function choose(ar) {
        if (typeof(ar.length) !== 'number') {
            throw 'invalid argument';
        }
        if (ar.length === 0) {
            throw 'empty array';
        }
        return ar[randInteger(ar.length)];
    }

    function listFiles(dir) {
        var tmp = (new java.io.File(dir)).listFiles();
        var res = [];
        Object.keys(tmp).forEach(function (idx) {res.push(String(tmp[idx].getName()));});
        return res;
    }

    var slice = Array.prototype.slice;

    function curry(fn) {
        var args = slice.call(arguments, 1);   // arg0=fn, arg1..argn=args
        return function() {
            return fn.apply(this, args.concat(slice.call(arguments)));
        };
    }

    function coerceToString(x) {
        if ((typeof x) !== 'string') {
            return String(x);
        }
        return x;
    }

    function fileToString(path, charset) {
        return coerceToString(fileToJavaString(path, charset));
    }

    function escapeXml(x) {
        return x.replace(/[<>&'"\r]/g, function (c) {
            switch (c) {
            case '<': return '&lt;';
            case '>': return '&gt;';
            case '&': return '&amp;';
            case '\'': return '&apos;';
            case '"': return '&quot;';
                //case '\r': return "&#13;";
            case '\r': return "&#xd;";
            }
        });
    }

    var shuffle = (function (floor, random) {
        return function (ar) {
            var k;
            var t;
            var len;

            len = ar.length;

            if (len < 2) {
                return this;
            }

            // Fisher-Yates shuffle
            while (len) {
                k = floor(random() * len);
                len -= 1;
                t = ar[k];
                ar[k] = ar[len];
                ar[len] = t;
            }

            return ar;
        };
    }(Math.floor, Math.random));

    function coerceToDate(dt) {
        if (dt instanceof Date) {
            return dt;
        }
        if (typeof(dt) === 'number') {
            return new Date(dt);
        }
        if (dt instanceof java.util.Date) {
            return new Date(dt.getTime());
        }
        throw 'invalid argument';
    }

    function driftedDate(dt) {
        dt = coerceToDate(dt);
        // timezone offset specified in minutes
        return new Date(dt.getTime() - dt.getTimezoneOffset() * 60000);
    }

    function friendlyDate(dt) {
        return dt.toISOString().replace(/^([\d\-]{10})T([\d:]{8})(?:.*)$/,'$1 $2');
    }

    function toISOParts(dt) {
        return dt.toISOString().match(/^(\d\d\d\d)-(\d\d)-(\d\d)T(\d\d):(\d\d):(\d\d)\.(\d\d\d)(.*)$/).slice(1);
    }

    function filenameDate(dt) {
        if (dt.length === 8 && dt.slice) {
            // assume iso parts
            return dt.slice(0,3).join('-') + '_' + dt[3] + 'h' + dt[4] + 'm' + dt[5];
        }
        return filenameDate(toISOParts(coerceToDate(dt)));
    }

    function decodeQueryString(input) {
        if (input === undefined) {
            throw 'undefined input';
        }
        var regex = /([^=]+)=(.*?)(?:&|$)/g;
        var kvp;
        var key;
        var tmp;
        var res = [];
        var lastIndex;
        while (kvp = regex.exec(input)) {
            key = kvp[1];
            if (/^.+&/.test(key)) {
                tmp = key.match(/^(.+)&(.*)/);
                res.push([tmp[1]]);
                key = tmp[2];
            }
            res.push([key, kvp[2]]);
            lastIndex = regex.lastIndex;
        }
        if (lastIndex && lastIndex !== input.length) {
            res.push([input.slice(lastIndex)]);
        }
        return res;
    }

    function decodeQueryStringAmp(input) {
        if (input === undefined) {
            throw 'undefined input';
        }
        var regex = /([^=]+)=(.*?)(?:&amp;|$)/g;
        var kvp;
        var key;
        var tmp;
        var res = [];
        var lastIndex;
        while (kvp = regex.exec(input)) {
            key = kvp[1];
            if (/^.+&/.test(key)) {
                tmp = key.match(/^(.+)&amp;(.*)/);
                res.push([tmp[1]]);
                key = tmp[2];
            }
            res.push([key, kvp[2]]);
            lastIndex = regex.lastIndex;
        }
        if (lastIndex && lastIndex !== input.length) {
            res.push([input.slice(lastIndex)]);
        }
        return res;
    }

    exports.charsetUTF8 = charsetUTF8;
    exports.writeString = writeString;
    exports.appendString = appendString;
    exports.writeJSON = writeJSON;
    exports.coerceToString = coerceToString;
    exports.readJSON = readJSON;
    exports.randDouble = randDouble;
    exports.trunc = trunc;
    exports.randInteger = randInteger;
    exports.choose = choose;
    exports.listFiles = listFiles;
    exports.readFileLines = readFileLines;
    exports.fileToJavaString = fileToJavaString;
    exports.fileToString = fileToString;
    exports.shuffle = shuffle;
    exports.getFileReader = getFileReader;
    exports.escapeXml = escapeXml;
    exports.numpad2 = numpad2;
    exports.numpad3 = numpad3;
    exports.numpad5 = numpad5;
    exports.numpad = numpad;
    exports.curry = curry;
    exports.coerceToDate = coerceToDate;
    exports.driftedDate = driftedDate;
    exports.friendlyDate = friendlyDate;
    exports.toISOParts = toISOParts;
    exports.filenameDate = filenameDate;
    exports.decodeQueryString = decodeQueryString;
    exports.decodeQueryStringAmp = decodeQueryStringAmp;
}(exports !== undefined ? exports : this /* or window */ ));
