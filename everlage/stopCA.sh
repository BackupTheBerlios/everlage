#!/bin/sh

kill `cat ca.pid`
rm -f ca.pid

kill `cat rmi.pid`
rm -f rmi.pid

rm -f nohup.out