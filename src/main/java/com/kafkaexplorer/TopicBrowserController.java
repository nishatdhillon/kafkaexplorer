package com.kafkaexplorer;

import com.kafkaexplorer.kafkaconnector.KafkaLib;
import com.kafkaexplorer.model.Cluster;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import org.apache.kafka.common.PartitionInfo;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TopicBrowserController implements Initializable {

    @FXML
    public TextField topic;
    private TreeView<String> kafkaTreeRef;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    public void populateScreen(Cluster cluster, String topicName, TreeView<String> clusterTreeView) {
        this.topic.setText(topicName);

        KafkaLib kafkaConnector = new KafkaLib();
        List<PartitionInfo> partitionInfo  = kafkaConnector.getTopicPartitionInfo(cluster,  topicName);
        System.out.println(partitionInfo);

        kafkaTreeRef = clusterTreeView;
    }

}
