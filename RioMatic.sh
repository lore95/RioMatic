rm -rf logs/RioMatic.*
cat /dev/null > logs/RioMatic.log
java -classpath ".:./bin/*:./lib/*:./lib/pi4j/*" RioMatic conf/RioMatic.ini conf/log.properties