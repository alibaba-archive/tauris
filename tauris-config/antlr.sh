#!/bin/sh

antlr -o src/main/gen/com/aliyun/tauris/config/parser/ast -visitor src/main/antlr4/Tauris.g4

