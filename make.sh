#!/bin/sh

export PATH=$JAVA_HOME/bin:$PATH
base=`dirname $0`
cd $base
base=`pwd`
mvn package -Dmaven.test.skip=true -Ddist=${base}
