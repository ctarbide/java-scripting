
var StringID = Java.type("com.acme.labs.StringID");

var LogManager_getLogger = Java.type("org.apache.logging.log4j.LogManager")["getLogger(String)"];

var __FILE__basename = __FILE__.replace(/^.*\//, "");
var LOG = LogManager_getLogger(__FILE__basename);

var str = "hello world!";
var str_id = new StringID(str);

LOG.info("(new StringID(\"{}\")).toString(): id: [{}], source: [{}]", str, str_id.toString(), str_id.getSource());
