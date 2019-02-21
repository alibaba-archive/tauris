#!/bin/sh

antlr4 -o src/main/gen/antlr4/tauris -visitor -lib src/main/antlr4 src/main/antlr4/Tauris.g4
antlr4 -o src/main/gen/antlr4/tauris -visitor -lib src/main/antlr4 src/main/antlr4/TExpression.g4

