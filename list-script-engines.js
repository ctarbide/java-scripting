
load("classpath:require.js");

var scriptManager = new ScriptEngineManager();
var scriptEngine = scriptManager.getEngineByName("nashorn");

var facs = scriptManager.getEngineFactories();

for (const fac of facs) {
    log.info("");
    log.info("**** factory: {}", fac);
    log.info("              name: {}", fac.getEngineName());
    log.info("           version: {}", fac.getEngineVersion());
    log.info("     language-name: {}", fac.getLanguageName());
    log.info("  language-version: {}", fac.getLanguageVersion());
    log.info("        extensions: {}", fac.getExtensions());
    log.info("        mime-types: {}", fac.getMimeTypes());
    log.info("             names: {}", fac.getNames());
}
log.info("");

