rm -rf logs/RioMatic.*
cat /dev/null > logs/RioMatic.log
java -classpath ".:./bin/*:./lib/*:./lib/pi4j/*" -DDBHOST=192.168.200.99:3306 RioMatic conf/RioMatic.ini conf/log.properties