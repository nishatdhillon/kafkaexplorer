package com.kafkaexplorer;

import com.kafkaexplorer.kafkaconnector.KafkaLib;
import com.kafkaexplorer.model.Cluster;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.MapValueFactory;
import org.apache.kafka.common.PartitionInfo;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TopicBrowserController implements Initializable {

    @FXML
    public TextField topic;
    public TableView partitionTable;
    private TreeView<String> kafkaTreeRef;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    public void populateScreen(Cluster cluster, String topicName, TreeView<String> clusterTreeView) {
        this.topic.setText(topicName);
        this.kafkaTreeRef = clusterTreeView;

        KafkaLib kafkaConnector = new KafkaLib();
        List<PartitionInfo> partitionInfo  = kafkaConnector.getTopicPartitionInfo(cluster,  topicName);
        displayPartitionInfo(partitionInfo);

    }

    private void displayPartitionInfo(List<PartitionInfo> partitionInfo) {

        TableColumn<Map, Object> partitionColumn = new TableColumn<>("Partition");
        partitionColumn.setCellValueFactory(new MapValueFactory<>("Partition"));

        TableColumn<Map, Object> leaderColumn = new TableColumn<>("Leader");
        leaderColumn.setCellValueFactory(new MapValueFactory<>("Leader"));

        TableColumn<Map, Object> replicasColumn = new TableColumn<>("Replicas");
        replicasColumn.setCellValueFactory(new MapValueFactory<>("Replicas"));

        TableColumn<Map, Object> inSynReplicasColumn = new TableColumn<>("InSynReplicas");
        inSynReplicasColumn.setCellValueFactory(new MapValueFactory<>("InSynReplicas"));

        partitionTable.getColumns().add(partitionColumn);
        partitionTable.getColumns().add(leaderColumn);
        partitionTable.getColumns().add(replicasColumn);
        partitionTable.getColumns().add(inSynReplicasColumn);

        ObservableList<Map<String, Object>> items = FXCollections.<Map<String, Object>>observableArrayList();

        for (int i = 0; i < partitionInfo.size(); i++ ) {
            Map<String, Object> item1 = new HashMap<>();
            item1.put("Partition", partitionInfo.get(i).partition());
            item1.put("Leader" , partitionInfo.get(i).leader());
            //Replicas List

            String replicaList = "[";
            for (int j = 0; j < partitionInfo.get(i).replicas().length; j++ ) {

                replicaList += partitionInfo.get(i).replicas()[j].id() + ",";
            }
            replicaList += "]";
            item1.put("Replicas" , replicaList);

            String inSyncReplicaList = "[";
            for (int j = 0; j < partitionInfo.get(i).replicas().length; j++ ) {

                inSyncReplicaList += partitionInfo.get(i).inSyncReplicas()[j].id() + ",";
            }
            inSyncReplicaList += "]";
            item1.put("InSynReplicas" , inSyncReplicaList);

            items.add(item1);
        }

        partitionTable.getItems().addAll(items);

        System.out.println(partitionInfo);
    }

}
