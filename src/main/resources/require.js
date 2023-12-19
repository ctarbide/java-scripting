/*
 * reference: 2020-09-26_18h43m08_nashorn-jjs-jmeter-integration/jmeter-run.js
 */

if (this.require) {
    throw "require already defined";
}

var array_type = (function(global){
    "use strict";

    var array_types = {};

    function import_(m, n) {
        var res = Java.type(m);
        var sym = n || m.split(/\./).pop();
        if (/\[\]$/.test(m)) {
            array_types[(new res(0)).getClass().getName()] = res;
        }
        if (global[sym]) {
            throw 'error: symbol already defined: "' + sym + '"';
        }
        return global[sym] = res;
    }

    import_('java.io.BufferedReader');
    import_('java.io.BufferedWriter');
    import_('java.io.File');
    import_('java.io.FileInputStream');
    import_('java.io.FileOutputStream');
    import_('java.io.FileReader');
    import_('java.io.IOException');
    import_('java.io.InputStreamReader');
    import_('java.io.OutputStreamWriter');
    import_('java.io.PrintWriter');
    import_('java.lang.AssertionError');
    import_('java.lang.Byte');
    import_('java.lang.Double');
    import_('java.lang.Float');
    import_('java.lang.IllegalArgumentException');
    import_('java.lang.Integer');
    import_('java.lang.Long');
    import_('java.lang.Object', 'JavaLangObject');
    import_('java.lang.RuntimeException');
    import_('java.lang.Short');
    import_('java.lang.StackTraceElement');
    import_('java.lang.String', 'JavaLangString');
    import_('java.lang.System');
    import_('java.lang.Thread');
    import_('java.lang.Throwable');
    import_('java.nio.ByteBuffer');
    import_('java.nio.charset.StandardCharsets');
    import_('java.nio.file.Files');
    import_('java.nio.file.Path');
    import_('java.nio.file.Paths');
    import_('java.security.MessageDigest');
    import_('java.security.Security');
    import_('java.util.Arrays');
    import_('java.util.Date', 'JavaUtilDate');
    import_('javax.script.ScriptContext');
    import_('javax.script.ScriptEngine');
    import_('javax.script.ScriptEngineManager');
    import_('org.apache.commons.codec.binary.Base32');
    import_('org.apache.logging.log4j.LogManager');
    import_('org.bouncycastle.crypto.Digest');
    import_('org.bouncycastle.crypto.digests.SHA1Digest');
    import_('org.bouncycastle.crypto.digests.SHA256Digest');
    import_('org.bouncycastle.crypto.digests.SHA512Digest');
    import_('org.bouncycastle.crypto.macs.HMac');
    import_('org.bouncycastle.crypto.params.KeyParameter');
    import_('org.bouncycastle.jce.provider.BouncyCastleProvider');
    import_('org.bouncycastle.util.encoders.Hex');

    import_('byte[]', 'ByteArray');
    import_('char[]', 'CharArray');
    import_('double[]', 'DoubleArray');
    import_('float[]', 'FloatArray');
    import_('int[]', 'IntArray');
    import_('java.lang.Object[]', 'ObjectArray');
    import_('long[]', 'LongArray');
    import_('short[]', 'ShortArray');

    import_('com.acme.labs.Example');

    return function array_type(b) {
        return array_types[b.getClass().getName()];
    };
}(this));

var require = (function (global, args) {
    var loaded = {};

    function load_isolated_once(filepath) {
        var realpath = Paths.get(filepath).toRealPath().toString();
        var realpath_basename = realpath.replace(/^.*\//, '');
        var log = LogManager['getLogger(java.lang.String)'](realpath_basename);

        if (loaded[realpath]) {
            log.warn("returning previously loaded module for \"{}\"", realpath);
            return loaded[realpath];
        }

        var scriptEngine = (new ScriptEngineManager()).getEngineByName("nashorn");

        // set context attributes
        var context = scriptEngine.getContext();
        context.setAttribute(ScriptEngine.FILENAME, realpath, ScriptContext.ENGINE_SCOPE);

        // set bindings
        var bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
        Object.keys(global).forEach(function (key) {
            bindings[key] = global[key];
        });
        bindings['log'] = log;

        var module = {
            exports: {},
        };
        loaded[realpath] = module;

        // keep it simple, expose only module.exports
        bindings['exports'] = module.exports;

        scriptEngine.eval(new FileReader(realpath), context);

        return module.exports;
    }

    return load_isolated_once;
}(this));

var log = (function(global){
    if (global.log) {
        return global.log;
    }
    var loaderScript = __FILE__ || "unknown";
    var stackTrace = Java.from(Thread.currentThread().getStackTrace()).slice(3 /* skip getStackTrace, this anonymous function and the upper load */);
    for (const elt of stackTrace) {
        let filename = String(elt.getFileName());
        if (/\.js$/.test(filename)) {
            loaderScript = filename;
            break;
        }
    }
    var basename = loaderScript.replace(/^.*\//, '');
    return LogManager['getLogger(java.lang.String)'](basename);
}(this));

var console = {
    log: function () {
        var args = Array.prototype.slice.call(arguments);
        args.unshift(null);
        log.info(Function.prototype.call.apply(JavaLangString['format(String,Object[])'], args));
    }
};
