#!/bin/bash
JAVA_HOME=/usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/
CLASSPATH=.:$JAVA_HOME/jre/lib/rt.jar
CLASSPATH=$CLASSPATH:/root/autometry/lib/*

$JAVA_HOME/bin/java -Dautometry_obd -Dfile.encoding=UTF-8 -cp "$CLASSPATH" ru.autometry.obd.USSDAutometry /root/autometry/config.properties 1>autometry.log 2>error.log



