
var System = Java.type("java.lang.System");
var Example = Java.type("com.acme.labs.Example");

System.out.println(Example.getValue());

var LogManager = Java.type('org.apache.logging.log4j.LogManager');
var LogManager_getLogger = Java.type('org.apache.logging.log4j.LogManager')['getLogger(String)'];

var LOG = LogManager_getLogger("nashorn-example.js");

LOG.trace("testing: {}", 'trace message');
LOG.debug("testing: {}", 'debug message');
LOG.info("testing: {}", 'info message');
LOG.warn("testing: {}", 'warn message');
