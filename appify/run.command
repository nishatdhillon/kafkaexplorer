#!/bin/bash
cd -- "$(dirname -- "$BASH_SOURCE")"
cd releases
java -jar kafkaexplorer-1.1-osx.jar 
exit 0

