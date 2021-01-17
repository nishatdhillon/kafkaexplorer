#!/bin/bash
cd -- "$(dirname -- "$BASH_SOURCE")"
rm -rf kafkaexplorer-1.1.app
./appify.sh run.command kafkaexplorer-1.1
mkdir kafkaexplorer-1.1.app/Contents/MacOS/releases
cp ../releases/kafkaexplorer-1.1-osx.jar kafkaexplorer-1.1.app/Contents/MacOS/releases
rm -rf kafkaexplorerDMG
mkdir kafkaexplorerDMG
mv kafkaexplorer-1.1.app kafkaexplorerDMG
hdiutil create /tmp/tmp.dmg -ov -volname "kafkaexplorer-1.1" -fs HFS+ -srcfolder "kafkaexplorerDMG"
hdiutil convert /tmp/tmp.dmg -format UDZO -o kafkaexplorer-1.1.dmg
rm -rf ../releases/kafkaexplorer-1.1.dmg
mv kafkaexplorer-1.1.dmg ../releases


