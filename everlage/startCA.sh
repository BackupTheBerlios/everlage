#!/bin/sh

export CLASSPATH=../bin/:$CLASSPATH
export CLASSPATH=./ext/log4j.jar:$CLASSPATH
export CLASSPATH=./ext/postgresjdbc7.1-1.2.jar:$CLASSPATH

/usr/bin/nohup rmiregistry &
echo $! > rmi.pid

/usr/bin/nohup java de.everlage.ca.core.CentralAgent &
echo $! > ca.pid
