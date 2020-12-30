[![ko-fi](https://www.ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/B0B132J1L)

| Branch  | Build Status  |
| --- | --- |
| develop  | ![example workflow name](https://github.com/stephaneuh/kafkaexplorer/workflows/Maven%20Build/badge.svg?branch=develop)

# kafkaexplorer
An easy, straight to the point, graphical tool to explore Kafka topics and produce messages.
Working on Windows, MacOs and Linux.

![Alt text](img/kt_01.png "Title")

# Requirements
1. JavaFX

This application is a based on JavaFX 11, so it requires the JavaFX SDK to be installed. Download the version 11 for Windows/MacOs/Linux here:
https://gluonhq.com/products/javafx/

Be sure that the JavaFx binaries are in your PATH.

2. config.yaml

A config file with all the connection information to your kafka clusters must exist here:

| OS  | location  |
| --- | --- |
|Windows|%HOMEDRIVE%%HOMEPATH%/kafkaexplorer/config.yaml| 
|MacOs|~/kafkaexplorer/config.yaml| 
|Linux|~/kafkaexplorer/config.yaml| 

A sample config.yaml is provided [here](/config/config.yaml)


# How to build and run

Simply execute:
```
mvn clean javafx:run
```
