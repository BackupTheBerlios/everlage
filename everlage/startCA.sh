#!/bin/sh

export CLASSPATH=./bin/
export CLASSPATH=./bin/ext/log4j.jar:$CLASSPATH
export CLASSPATH=./bin/ext/postgresjdbc7.1-1.2.jar:$CLASSPATH
export CLASSPATH=./test-out:$CLASSPATH
export CLASSPATH=./bin/ext/Tidy.jar:$CLASSPATH
export CLASSPATH=./bin/ext/httpunit.jar:$CLASSPATH
export CLASSPATH=./bin/ext/js.jar:$CLASSPATH
export CLASSPATH=./bin/ext/nekohtml.jar:$CLASSPATH
export CLASSPATH=./bin/ext/xercesImpl.jar:$CLASSPATH
export CLASSPATH=./bin/ext/xmlParserAPIs.jar:$CLASSPATH
export CLASSPATH=./bin/ext/exml.jar:$CLASSPATH
export CLASSPATH=./bin/ext/dtdparser.jar:$CLASSPATH


/usr/bin/nohup rmiregistry &
echo $! > rmi.pid

/usr/bin/nohup java de.everlage.ca.core.CentralAgent &
echo $! > ca.pid
