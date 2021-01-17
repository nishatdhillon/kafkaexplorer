#!/bin/bash
cd -- "$(dirname -- "$BASH_SOURCE")"
rm -rf kafkaexplorer-1.1.app
./appify.sh run.command kafkaexplorer-1.1
mkdir kafkaexplorer-1.1.app/Contents/MacOS/releases
cp ../releases/kafkaexplorer-1.1-osx.jar kafkaexplorer-1.1.app/Contents/MacOS/releases
rm -rf ../releases/kafkaexplorer-1.1.app
mv kafkaexplorer-1.1.app ../releases


