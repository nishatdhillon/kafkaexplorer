
<p align="center">
<img src="img/ke-logo-font-15.png">
<a href='https://ko-fi.com/B0B132J1L' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://cdn.ko-fi.com/cdn/kofi3.png?v=2' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>
</p>

# Kafkaexplorer ![example workflow name](https://github.com/stephaneuh/kafkaexplorer/workflows/Maven%20Build/badge.svg?branch=develop) <img src="img/jfxlogopad1.png" width="60" height="60" align="right">

An easy, straight to the point, **graphical tool** to explore Kafka topics and produce messages.
Working on **Windows and MacOs**.

![Alt text](img/kt_01.png "Main") ![Alt text](img/browser.PNG "Browser")

Features:
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

1. OpenJDK 15+

2. A config file with all the connection information to your kafka clusters must exist here:

| OS  | location  |
| --- | --- |
|Windows|%HOMEDRIVE%%HOMEPATH%\kafkaexplorer\config.yaml <br>(like C:\Users\MyUser\kafkaexplorer\config.yaml)| 
|MacOs|~/kafkaexplorer/config.yaml|

A sample config.yaml is provided [here](/config/config.yaml)

## How to run from binaries

Download the latest release of **kafkaexplorer.jar** for Windows or MacOs:
https://github.com/stephaneuh/kafkaexplorer/tree/develop/releases

And Simply execute:

```
java -jar kafkaexplorer-[RELEASE_VERSION]-windows.jar
//or
java -jar kafkaexplorer-[RELEASE_VERSION]-macos.jar
```

## How to build and run from sources

Simply execute:
```
mvn clean javafx:run
```
