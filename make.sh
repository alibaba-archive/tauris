#!/bin/sh

export PATH=$JAVA_HOME/bin:$PATH
base=`dirname $0`
cd $base
base=`pwd`
version=`cat VERSION`
rm -rf lib
mvn package -Dmaven.test.skip=true -Ddist=${base}

mkdir -p dist/tauris
cp -R bin dist/tauris
cp -R lib dist/tauris
cp -R config dist/tauris
tar cvf dist/tauris-${version}.tgz -C dist tauris
rm -rf dist/tauris
