#!/bin/sh

export CLASSPATH=./bin/:$CLASSPATH
export CLASSPATH=./bin/ext/log4j.jar:$CLASSPATH
export CLASSPATH=./bin/ext/postgresjdbc7.1-1.2.jar:$CLASSPATH
export CLASSPATH=./test-out:$CLASSPATH

/usr/bin/nohup rmiregistry &
echo $! > rmi.pid

/usr/bin/nohup java de.everlage.ca.core.CentralAgent &
echo $! > ca.pid
