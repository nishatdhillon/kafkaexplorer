#!/bin/bash
cd -- "$(dirname -- "$BASH_SOURCE")"
cd releases
java -jar -Xms1024m -Xmx2048m kafkaexplorer-1.1-osx.jar
exit 0

