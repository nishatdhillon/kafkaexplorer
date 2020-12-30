package com.kafkaexplorer.kafkaconnector;

import com.kafkaexplorer.model.Cluster;
import javafx.scene.control.TableView;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.PartitionInfo;

import java.util.*;

public class KafkaLib {

    public boolean continueBrowsing;

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


    public void browseTopic(Cluster cluster, String topicName, TableView messagesTable) {

        Properties props = new Properties();
        props.put("bootstrap.servers", cluster.getHostname());
        props.put("security.protocol", cluster.getProtocol());
        props.put("sasl.jaas.config", cluster.getJaasConfig());
        props.put("sasl.mechanism", cluster.getMechanism());

        props.put("default.api.timeout.ms", 5000);
        props.put("request.timeout.ms", 5000);
        //props.put("session.timeout.ms", 5000);

        props.put("group.id", "kafkaexplorer-consumer-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        //props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //or latest


        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList(topicName));

        consumer.poll(0);  // without this, the assignment will be empty.
        consumer.assignment().forEach(t -> {
            System.out.printf("Set %s to offset 0%n", t.toString());
            consumer.seek(t, 0);
        });


        try {
            while (continueBrowsing) {
                System.out.println("Start Browsing topic");
                ConsumerRecords<String, String> records = consumer.poll(5000);
                for (ConsumerRecord<String, String> record : records){
                    Map<String, Object> item1 = new HashMap<>();
                    item1.put("Offset", record.offset());
                    item1.put("Message", record.value());
                    messagesTable.getItems().add(item1);
                }

            }
        } finally {
            consumer.close();
        }

    }

    public void produceMessage(Cluster cluster, String topicName, String record) {

        Properties props = new Properties();
        props.put("bootstrap.servers", cluster.getHostname());
        props.put("security.protocol", cluster.getProtocol());
        props.put("sasl.jaas.config", cluster.getJaasConfig());
        props.put("sasl.mechanism", cluster.getMechanism());

        props.put("default.api.timeout.ms", 5000);
        props.put("request.timeout.ms", 5000);
        //props.put("session.timeout.ms", 5000);

        props.put("group.id", "test-consumer-group");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");


        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        producer.send(new ProducerRecord<String, String>(topicName, "", record), new Callback() {
            @Override
            public void onCompletion(RecordMetadata m, Exception e) {
                if (e != null) {
                    e.printStackTrace();
                } else {
                    System.out.printf("Produced record to topic %s partition [%d] @ offset %d%n", m.topic(), m.partition(), m.offset());
                }
            }
        });

        producer.flush();
        producer.close();
    }
}
