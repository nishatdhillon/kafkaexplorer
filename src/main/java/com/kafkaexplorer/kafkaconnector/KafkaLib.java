package com.kafkaexplorer.kafkaconnector;

import com.kafkaexplorer.logger.MyLogger;
import com.kafkaexplorer.model.Cluster;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.slf4j.Logger;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class KafkaLib {

    public boolean continueBrowsing;
    private Properties props;

    public KafkaLib() {

         this.props = new Properties();

    }

    private Properties getProps() {
        return this.props;
    }

    private void setProps(Cluster cluster) {

        //consumer config
        this.props.put("bootstrap.servers", cluster.getHostname());
        this.props.put("security.protocol", cluster.getProtocol());
        this.props.put("sasl.jaas.config", cluster.getJaasConfig());
        this.props.put("sasl.mechanism", cluster.getMechanism());

        this.props.put("default.api.timeout.ms", 5000);
        this.props.put("request.timeout.ms", 5000);
        //this.props.put("session.timeout.ms", 5000);
        this.props.put("auto.commit.interval.ms", "1000");

        this.props.put("group.id", cluster.getConsumerGroup());
        this.props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        this.props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        //producer config
        this.props.put(ProducerConfig.ACKS_CONFIG, "all");
        this.props.put(ProducerConfig.ACKS_CONFIG, "all");
        this.props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        this.props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        //set SSL Truststore if any provided in the config.yaml
        if (cluster.getTrustStoreJKS() != "") {
            this.props.put("ssl.truststore.location", cluster.getTrustStoreJKS());
            this.props.put("ssl.truststore.password", cluster.getTrustStoreJKSPwd());
        }

    }

    public String connect(Cluster cluster) throws Exception{

        this.setProps(cluster);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>( this.getProps());
        consumer.close();

        return "OK";
    }

    public ArrayList<String> listTopics(Cluster cluster){

        Map<String, List<PartitionInfo>> topics;
        ArrayList<String> onlyTopicsName = new ArrayList<String>();

        this.setProps(cluster);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(this.getProps());
        topics = consumer.listTopics();
        consumer.close();

        Iterator<Map.Entry<String, List<PartitionInfo>>> iterator = topics.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, List<PartitionInfo>> entry = iterator.next();
            onlyTopicsName.add(entry.getKey());
        }
        Collections.sort(onlyTopicsName);

        return onlyTopicsName;
    }


    public List<PartitionInfo> getTopicPartitionInfo(Cluster cluster, String topicName){

        List<PartitionInfo> topicPartitions;

        this.setProps(cluster);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(this.getProps());
        topicPartitions = consumer.partitionsFor(topicName);
        consumer.close();

        return topicPartitions;
    }


    public void browseTopic(Cluster cluster, String topicName, TableView messagesTable) {

        this.setProps(cluster);

        //props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); //or latest

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(this.getProps());
        consumer.subscribe(Arrays.asList(topicName));

        consumer.poll(0);  // without this, the assignment will be empty.
        //consumer.assignment().forEach(t -> {
        //    MyLogger.logDebug("Set " + t.toString() + " to offset 0");
            consumer.seekToBeginning( consumer.assignment());
        //});


        try {
            while (continueBrowsing) {
                ConsumerRecords<String, String> records = consumer.poll(5000);
                for (ConsumerRecord<String, String> record : records) {
                    Map<String, Object> item1 = new HashMap<>();
                    item1.put("Offset", record.offset());
                    item1.put("Partition", record.partition());

                    //for (Header header : record.headers()) {
                     //    String attributeName = header.key();
                     //    String attributeValue = header.value().toString();
                    //}
                    Date date = new Date(record.timestamp());
                    Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    item1.put("Created", format.format(date).toString());

                    item1.put("Message", record.value() );

                    messagesTable.getItems().add(item1);
                    messagesTable.sort();
                }

            }
        }
        finally {
            consumer.close();
        }


    }

    public void produceMessage(Cluster cluster, String topicName, String record) {

        this.setProps(cluster);

        Producer<String, String> producer = new KafkaProducer<String, String>( this.getProps());

        //Handle an exception from the callback
     try {
         producer.send(new ProducerRecord<String, String>(topicName, "", record));

     } catch (CompletionException e) {
         //todo validate behaviour

         MyLogger.logDebug("Can't produce" + e.getMessage());

            //show an alert Dialog
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Can't produce! You need to set the TOPIC WRITE ACLs.");
            a.setContentText(e.getMessage());
           a.show();
        }

        producer.flush();
        producer.close();

    }

    public KafkaFuture<Config> getTopicInfo(Cluster cluster, String topicName) {
    //Need to build an AdminClient (requires more privileges that a Kafka Consumer)
        List<PartitionInfo> topicPartitions;

        this.setProps(cluster);

        AdminClient adminClient = KafkaAdminClient.create(this.getProps());

            ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topicName.toString());

            //KafkaFuture<TopicDescription> descriptionFuture = adminClient.describeTopics(Collections.singleton(topicName.toString())).values().get(topicName.toString());
            KafkaFuture<Config> configFuture = adminClient.describeConfigs(Collections.singleton(resource)).values().get(resource);

        return configFuture;
    }
}
