#!/bin/bash
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home
CLASSPATH=.:$JAVA_HOME/jre/lib/rt.jar
CLASSPATH=$CLASSPATH:/Users/jeck/Work/my/odb2/grid/bin/lib/*

$JAVA_HOME/bin/java -Dsmart_obd -Dfile.encoding=UTF-8 -cp "$CLASSPATH" ru.autometry.obd.Test conf/config2.xml


