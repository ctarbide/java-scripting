
var JavaLangSystem = Java.type("java.lang.System");
var JavaLangLong = Java.type("java.lang.Long");
var JavaLangMath = Java.type("java.lang.Math");

var LogManager_getLogger = Java.type("org.apache.logging.log4j.LogManager")["getLogger(String)"];

var __FILE__basename = __FILE__.replace(/^.*\//, "");
var LOG = LogManager_getLogger(__FILE__basename);

function parseLong(s) { return (JavaLangLong["valueOf(String)"])("" + s); }

function now() { return new JavaLangLong(JavaLangSystem.currentTimeMillis()); }

function timestamp_diff(a, b) {
    // subtractExact was added in java-1.8
    return new JavaLangLong(JavaLangMath["subtractExact(long,long)"](a, b));
}

var t0 = now();

(function(t1){
    "use strict";
    LOG.info("t0: {}", t0);
    LOG.info("t1: {}", t1);
    LOG.info("diff: {}", timestamp_diff(t1, t0));
}(now()));
