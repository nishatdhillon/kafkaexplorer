package com.kafkaexplorer;

import com.kafkaexplorer.kafkaconnector.KafkaLib;
import com.kafkaexplorer.model.Cluster;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseEvent;
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
    public ChoiceBox browsingType;
    public TableView messagesTable;
    public TextField produceMsg;
    public Button startButton;
    public Button stopButton;
    private TreeView<String> kafkaTreeRef;
    private Cluster cluster;

    final KafkaLib kafkaConnector = new KafkaLib();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        browsingType.getItems().addAll("from-beginning"); //add "latest" later
        browsingType.setValue("from-beginning");

        //init message browser table
        TableColumn<Map, Object> offsetColumn = new TableColumn<>("Offset");
        offsetColumn.setCellValueFactory(new MapValueFactory<>("Offset"));

        TableColumn<Map, Object> messageColumn = new TableColumn<>("Message");
        messageColumn.setCellValueFactory(new MapValueFactory<>("Message"));
        messageColumn.setMinWidth(800);

        messagesTable.getColumns().add(offsetColumn);
        messagesTable.getColumns().add(messageColumn);

    }


    public void populateScreen(Cluster cluster, String topicName, TreeView<String> clusterTreeView) {
        this.topic.setText(topicName);
        this.kafkaTreeRef = clusterTreeView;
        this.cluster = cluster;

        stopButton.setDisable(true);

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

    public void startBrowsing(MouseEvent mouseEvent) {
        messagesTable.getItems().clear();
        startButton.setDisable(true);
        stopButton.setDisable(false);

        kafkaConnector.continueBrowsing = true;
        //Create a thread for browsing topic, to not block the UI
        Task<Integer> task = new Task<Integer>() {
            @Override protected Integer call() throws Exception {
                kafkaConnector.browseTopic(cluster,  topic.getText(), messagesTable);
                return 0;
            }

            @Override protected void succeeded() {
                super.succeeded();
                System.out.println("Done!");
            }

            @Override protected void cancelled() {
                super.cancelled();
                System.out.println("Cancelled!");
            }

            @Override protected void failed() {
                super.failed();
                System.out.println("Failed!");
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

        public void stopBrowsing(MouseEvent mouseEvent) {
        //todo Cancel the browsing task/thread instead of using boolean
            kafkaConnector.continueBrowsing = false;
            startButton.setDisable(false);
            stopButton.setDisable(true);
        }

    public void produceMessage(MouseEvent mouseEvent) {

        kafkaConnector.produceMessage(cluster,  topic.getText(), produceMsg.getText());


    }

    public void clearMsgTable(MouseEvent mouseEvent) {

        messagesTable.getItems().clear();

    }
}
