# Use -Dlog4j.debug for Log4J startup debugging info
# Use -Xms512M -Xmx512M to start with 512MB of heap memory. Set size according to your needs.
# Use -XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenSweepingEnabled for PermGen GC

CPATH="./:avg-graph.jar:lib/*"
java -cp ${CPATH} -Dfile.encoding=UTF-8 com.creant.graph.AvengersGraphApplication $1 $2