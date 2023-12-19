
log.info("utils loading");

(function (exports) {
    "use strict";

    /* return [0,max) */
    function randDouble(max) {
        return Math.random() * max;
    }
    exports.randDouble = randDouble;

    function trunc(n) {
        return n - n % 1;
    }
    exports.trunc = trunc;

    function numpad2(x) { return ('00' + x).slice(-2); }
    function numpad3(x) { return ('000' + x).slice(-3); }
    function numpad5(x) { return ('00000' + x).slice(-5); }
    exports.numpad2 = numpad2;
    exports.numpad3 = numpad3;
    exports.numpad5 = numpad5;

    function numpad(x, len) {
        if (len < 0) {
            throw 'len < 0';
        }
        if (len > 30) {
            throw 'len > 30';
        }
        return ('000000000000000000000000000000' + x).slice(-len);
    }
    exports.numpad = numpad;

    /* return [0,max) */
    function randInteger(max) {
        return trunc(randDouble(max));
    }
    exports.randInteger = randInteger;

    function choose(ar) {
        if (typeof (ar.length) !== 'number') {
            throw 'invalid argument';
        }
        if (ar.length === 0) {
            throw 'empty array';
        }
        return ar[randInteger(ar.length)];
    }
    exports.choose = choose;

    function curry(fn) {
        var args = Array.prototype.slice.call(arguments, 1);   // arg0=fn, arg1..argn=args
        return function () {
            return fn.apply(this, args.concat(Array.prototype.slice.call(arguments)));
        };
    }
    exports.curry = curry;

    function coerceToString(x) {
        if ((typeof x) !== 'string') {
            return String(x);
        }
        return x;
    }
    exports.coerceToString = coerceToString;

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
    exports.escapeXml = escapeXml;

    function shuffle(ar) {
        var len = ar.length;
        if (len < 2) {
            return this;
        }

        // Fisher-Yates shuffle
        while (len) {
            var k = Math.floor(Math.random() * len);
            len -= 1;
            var t = ar[k];
            ar[k] = ar[len];
            ar[len] = t;
        }

        return ar;
    }
    exports.shuffle = shuffle;

    function coerceToDate(dt) {
        if (dt instanceof Date) {
            return dt;
        }
        if (typeof (dt) === 'number') {
            return new Date(dt);
        }
        throw 'invalid argument';
    }
    exports.coerceToDate = coerceToDate;

    function driftedDate(dt) {
        dt = coerceToDate(dt);
        // timezone offset specified in minutes
        return new Date(dt.getTime() - dt.getTimezoneOffset() * 60000);
    }
    exports.driftedDate = driftedDate;

    function friendlyDate(dt) {
        return dt.toISOString().replace(/^([\d\-]{10})T([\d:]{8})(?:.*)$/, '$1 $2');
    }
    exports.friendlyDate = friendlyDate;

    function toISOParts(dt) {
        return dt.toISOString().match(/^(\d\d\d\d)-(\d\d)-(\d\d)T(\d\d):(\d\d):(\d\d)\.(\d\d\d)(.*)$/).slice(1);
    }
    exports.toISOParts = toISOParts;

    function filenameDate(dt) {
        if (dt.length === 8 && dt.slice) {
            // assume iso parts
            return dt.slice(0, 3).join('-') + '_' + dt[3] + 'h' + dt[4] + 'm' + dt[5];
        }
        return filenameDate(toISOParts(coerceToDate(dt)));
    }
    exports.filenameDate = filenameDate;

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
    exports.decodeQueryString = decodeQueryString;

    return exports;
}(exports));

log.info("utils loaded");
