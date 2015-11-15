#!/bin/bash
echo "Running hazelcast instance"
export CLASSPATH=./hazelcast-all-3.5.2.jar:../../../build/libs/lucas-0.1-all.jar
java com.hazelcast.console.ConsoleApp