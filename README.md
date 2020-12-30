[![ko-fi](https://www.ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/B0B132J1L)

| Branch  | Build Status  |
| --- | --- |
| develop  | ![example workflow name](https://github.com/stephaneuh/kafkaexplorer/workflows/Maven%20Build/badge.svg?branch=develop)
# Kafkaexplorer
An easy, straight to the point, graphical tool to explore Kafka topics and produce messages.
Working on **Windows, MacOs and Linux**.

![Alt text](img/kt_01.png "Title")

## Requirements (config.yaml)

A config file with all the connection information to your kafka clusters must exist here:

| OS  | location  |
| --- | --- |
|Windows|%HOMEDRIVE%%HOMEPATH%/kafkaexplorer/config.yaml| 
|MacOs|~/kafkaexplorer/config.yaml| 
|Linux|~/kafkaexplorer/config.yaml| 

A sample config.yaml is provided [here](/config/config.yaml)

## How to run from binaries

Download the file **kafkaexplorer.jar** from the last release:
https://github.com/stephaneuh/kafkaexplorer/releases/latest

Simply execute:

```
java -jar kafkaexplorer-[RELEASE_NAME].jar
```

## How to build and run from sources

Simply execute:
```
mvn clean javafx:run
```
