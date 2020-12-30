package com.kafkaexplorer.kafkaconnector;

import com.kafkaexplorer.model.Cluster;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KafkaLib {

    public String connect(Cluster cluster) throws Exception{

        Properties props = new Properties();
        props.put("bootstrap.servers", cluster.getHostname());
        props.put("security.protocol", cluster.getProtocol());
        props.put("sasl.jaas.config", cluster.getJaasConfig());
        props.put("sasl.mechanism", cluster.getMechanism());

        props.put("default.api.timeout.ms", 5000);
        props.put("request.timeout.ms", 5000);
        props.put("session.timeout.ms", 5000);

        props.put("group.id", "test-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.close();

        return "OK";
    }

    public Map<String, List<PartitionInfo>> listTopics(Cluster cluster){

        Map<String, List<PartitionInfo>> topics;

        Properties props = new Properties();
        props.put("bootstrap.servers", cluster.getHostname());
        props.put("security.protocol", cluster.getProtocol());
        props.put("sasl.jaas.config", cluster.getJaasConfig());
        props.put("sasl.mechanism", cluster.getMechanism());

        props.put("default.api.timeout.ms", 5000);
        props.put("request.timeout.ms", 5000);
        props.put("session.timeout.ms", 5000);

        props.put("group.id", "test-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        topics = consumer.listTopics();
        consumer.close();

        return topics;
    }


    public List<PartitionInfo> getTopicPartitionInfo(Cluster cluster, String topicName){

        List<PartitionInfo> topicPartitions;

        Properties props = new Properties();
        props.put("bootstrap.servers", cluster.getHostname());
        props.put("security.protocol", cluster.getProtocol());
        props.put("sasl.jaas.config", cluster.getJaasConfig());
        props.put("sasl.mechanism", cluster.getMechanism());

        props.put("default.api.timeout.ms", 5000);
        props.put("request.timeout.ms", 5000);
        props.put("session.timeout.ms", 5000);

        props.put("group.id", "test-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        topicPartitions = consumer.partitionsFor(topicName);
        consumer.close();

        return topicPartitions;
    }



}
