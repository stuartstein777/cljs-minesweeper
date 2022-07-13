#!/bin/bash

# requires karma
# npm install -g karma-cli
# npm install karma-chrome-launcher
# npm isntall karma-cljs-tests

npx shadow-cljs compile test
./node_modules/karma/bin/karma start --single-run