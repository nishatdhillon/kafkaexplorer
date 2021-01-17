
<p align="center">
<img src="img/ke-logo-font-15.png" >
<br />
<a href='https://ko-fi.com/B0B132J1L' target='_blank'><img height="28px" style='border:0px;height:28px;' src='https://cdn.ko-fi.com/cdn/kofi3.png?v=2' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>
<img style='border:0px;height:28px;' src='https://github.com/stephaneuh/kafkaexplorer/workflows/Maven%20Build/badge.svg?branch=develop' border='0' alt='Buy Me a Coffee at ko-fi.com' />
</p>


# Kafkaexplorer (community-edition) 

An free, easy, straight to the point, **graphical tool** to explore Kafka topics and produce messages.
Working on **Windows** and **MacOs**. Based on [JavaFx](https://en.wikipedia.org/wiki/JavaFX).

## Screenshots 

![Alt text](img/kt_01.png "Main") ![Alt text](img/browser.PNG "Browser")

## Features

- List Kafka topics
- Browse topics from the beginning (see offset, key, message content, partition)
- See partitions information for a topic (leader, replicas, inSynReplica)
- Produce String messages into topics
- Support protocol: SASL_SSL and mechanism: PLAIN

Coming soon Features:
- Support more security protocols like SSL (keystore authentication)
- Support Json and Avro message formats for consuming/producing messages
- Display consumer groups information (with last offset)
- Export topic messages to files
- Import messages from files to topics
- Access some Cluster/Topic metrics

Have questions? [Q&A section](https://github.com/stephaneuh/kafkaexplorer/discussions/categories/q-a).

## Requirements (config.yaml)

1. Java OpenJDK 11 or later with the JAVA_HOME environment variable set.

2. A config file with all the connection information to your kafka clusters must exist here:

| OS  | location  |
| --- | --- |
|Windows|%HOMEDRIVE%%HOMEPATH%\kafkaexplorer\config.yaml <br>(like C:\Users\MyUser\kafkaexplorer\config.yaml)| 
|MacOs|~/kafkaexplorer/config.yaml|

A sample config.yaml is provided [here](/config/config.yaml)

## How to run from binaries

Download and execute the latest version:

- Windows: [kafkaexplorer-1.1.exe](/releases/kafkaexplorer-1.1.exe)| 
- MacOs: [kafkaexplorer-1.1.dmg](/releases/kafkaexplorer-1.1.dmg)|

## How to build and run from sources

Simply execute:
- Windows:
```
mvn package javafx:run -P windows
```
- MacOs:
```
mvn package javafx:run -P mac
```