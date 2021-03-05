package com.kafkaexplorer;

import com.jfoenix.controls.JFXComboBox;
import com.kafkaexplorer.utils.KafkaLib;
import com.kafkaexplorer.model.Cluster;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.config.TopicConfig;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TopicBrowserController implements Initializable {

    @FXML
    public TextField topic;
    public TableView partitionTable;
    public JFXComboBox browsingType;
    public TableView messagesTable;
    public TextField produceMsg;
    public Button startButton;
    public Button stopButton;
    public TableView topicConfigTable;
    public VBox rootNode;
    private TreeView<String> kafkaTreeRef;
    private Cluster cluster;

    final KafkaLib kafkaConnector = new KafkaLib();
    private static DecimalFormat df2 = new DecimalFormat("#.##");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        browsingType.getItems().addAll("from-beginning");
        browsingType.getItems().addAll("latest");
        browsingType.setValue("from-beginning");

        //init message browser table
        TableColumn<Map, Object> partitionColumn = new TableColumn<>("Part.");
        partitionColumn.setCellValueFactory(new MapValueFactory<>("Partition"));
        partitionColumn.setPrefWidth(40);
        partitionColumn.setMinWidth(40);

        TableColumn<Map, Object> offsetColumn = new TableColumn<>("Offset");
        offsetColumn.setCellValueFactory(new MapValueFactory<>("Offset"));
        offsetColumn.setPrefWidth(40);
        offsetColumn.setMinWidth(40);

        TableColumn<Map, Object> createdColumn = new TableColumn<>("Created");
        createdColumn.setCellValueFactory(new MapValueFactory<>("Created"));
        createdColumn.setSortType(TableColumn.SortType.DESCENDING);
        createdColumn.setMinWidth(125);

        TableColumn<Map, Object> srTypeColumn = new TableColumn<>("Schema Type");
        srTypeColumn.setCellValueFactory(new MapValueFactory<>("Schema Type"));
        srTypeColumn.setMinWidth(50);

        TableColumn<Map, Object> srIdColumn = new TableColumn<>("Schema Id");
        srIdColumn.setCellValueFactory(new MapValueFactory<>("Schema Id"));
        srIdColumn.setMinWidth(50);

        TableColumn<Map, Object> srSubjectColumn = new TableColumn<>("Schema Subject");
        srSubjectColumn.setCellValueFactory(new MapValueFactory<>("Schema Subject"));
        srSubjectColumn.setMinWidth(100);

        TableColumn<Map, Object> messageColumn = new TableColumn<>("Message");
        messageColumn.setCellValueFactory(new MapValueFactory<>("Message"));
        messageColumn.setMinWidth(800);

        messagesTable.getColumns().add(partitionColumn);
        messagesTable.getColumns().add(offsetColumn);
        messagesTable.getColumns().add(createdColumn);
        messagesTable.getColumns().add(srTypeColumn);
        messagesTable.getColumns().add(srIdColumn);
        messagesTable.getColumns().add(srSubjectColumn);

        messagesTable.getColumns().add(messageColumn);

        messagesTable.getSortOrder().add(createdColumn);

        //init topic config table
        TableColumn<Map, Object> topicConfigKey = new TableColumn<>("Config");
        topicConfigKey.setCellValueFactory(new MapValueFactory<>("Config"));

        TableColumn<Map, Object> topicConfigValue = new TableColumn<>("Value");
        topicConfigValue.setCellValueFactory(new MapValueFactory<>("Value"));

        topicConfigTable.getColumns().add(topicConfigKey);
        topicConfigTable.getColumns().add(topicConfigValue);

    }

    public void populateScreen(Cluster cluster, String topicName, TreeView<String> clusterTreeView) {
        this.topic.setText(topicName);
        this.kafkaTreeRef = clusterTreeView;
        this.cluster = cluster;

        stopButton.setDisable(true);


        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {

                KafkaLib kafkaConnector = new KafkaLib();
                List<PartitionInfo> partitionInfo = kafkaConnector.getTopicPartitionInfo(cluster, topicName);
                displayPartitionInfo(partitionInfo);

                KafkaFuture<Config> configFuture = kafkaConnector.getTopicInfo(cluster, topicName);
                displayTopicInfo(configFuture);

                return 0;
            }

            @Override
            protected void succeeded() {
                ((ProgressIndicator)rootNode.getScene().lookup("#progBar2")).setVisible(false);
                super.succeeded();
            }

            @Override
            protected void cancelled() {
                ((ProgressIndicator)rootNode.getScene().lookup("#progBar2")).setVisible(false);
                super.cancelled();
            }

            @Override
            protected void failed() {
                ((ProgressIndicator)rootNode.getScene().lookup("#progBar2")).setVisible(false);
                super.failed();
                //show an alert Dialog
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("Error!");
                a.setContentText(this.getException().getMessage());
                a.show();
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();

    }

    private void displayTopicInfo(KafkaFuture<Config> configFuture) {

        ObservableList<Map<String, Object>> items = FXCollections.<Map<String, Object>>observableArrayList();

        try {

            Config config = configFuture.get();


            ConfigEntry entry1 = config.get(TopicConfig.RETENTION_MS_CONFIG);
            Map<String, Object> item1 = new HashMap<>();


            item1.put("Config", TopicConfig.RETENTION_MS_CONFIG);
            item1.put("Value", entry1.value() + "ms (" + df2.format(Double.parseDouble(entry1.value()) / (1000 * 60 * 60 * 24)) + "d)");

            ConfigEntry entry2 = config.get(TopicConfig.RETENTION_BYTES_CONFIG);
            Map<String, Object> item2 = new HashMap<>();
            item2.put("Config", TopicConfig.RETENTION_BYTES_CONFIG);
            item2.put("Value", ((entry2.value().equals("-1")) ? "-1 (not set)" : entry2.value()));


            ConfigEntry entry3 = config.get(TopicConfig.MAX_MESSAGE_BYTES_CONFIG);
            Map<String, Object> item3 = new HashMap<>();
            item3.put("Config", TopicConfig.MAX_MESSAGE_BYTES_CONFIG);
            item3.put("Value", ((entry3.value().equals("-1")) ? "-1 (not set)" : entry3.value() + "b (" + df2.format(Double.parseDouble(entry3.value()) / (1024 * 1024)) + "Mb)"));

            items.add(item1);
            items.add(item2);
            items.add(item3);

        } catch (Exception e) {

            Map<String, Object> item4 = new HashMap<>();
            item4.put("Config", "Not Authorized!");

            Map<String, Object> item5 = new HashMap<>();
            item5.put("Config", "DESCRIBE_CONFIGS ACL required on this TOPIC");

            items.add(item4);
            items.add(item5);
        }

        topicConfigTable.getItems().addAll(items);

    }

    private void displayPartitionInfo(List<PartitionInfo> partitionInfo) {

        TableColumn<Map, Object> partitionColumn = new TableColumn<>("Id");
        partitionColumn.setCellValueFactory(new MapValueFactory<>("Id"));
        partitionColumn.setMaxWidth(25);

        TableColumn<Map, Object> leaderColumn = new TableColumn<>("Leader");
        leaderColumn.setCellValueFactory(new MapValueFactory<>("Leader"));

        TableColumn<Map, Object> replicasColumn = new TableColumn<>("Replicas");
        replicasColumn.setCellValueFactory(new MapValueFactory<>("Replicas"));

        TableColumn<Map, Object> inSynReplicasColumn = new TableColumn<>("ISR");
        inSynReplicasColumn.setCellValueFactory(new MapValueFactory<>("ISR"));

        partitionTable.getColumns().add(partitionColumn);
        partitionTable.getColumns().add(leaderColumn);
        partitionTable.getColumns().add(replicasColumn);
        partitionTable.getColumns().add(inSynReplicasColumn);

        ObservableList<Map<String, Object>> items = FXCollections.<Map<String, Object>>observableArrayList();

        for (int i = 0; i < partitionInfo.size(); i++) {
            Map<String, Object> item1 = new HashMap<>();
            item1.put("Id", partitionInfo.get(i).partition());
            item1.put("Leader", partitionInfo.get(i).leader());
            //Replicas List

            String replicaList = "[";
            for (int j = 0; j < partitionInfo.get(i).replicas().length; j++) {

                replicaList += partitionInfo.get(i).replicas()[j].id() + ",";
            }
            replicaList += "]";
            item1.put("Replicas", replicaList);

            String inSyncReplicaList = "[";
            for (int j = 0; j < partitionInfo.get(i).replicas().length; j++) {

                inSyncReplicaList += partitionInfo.get(i).inSyncReplicas()[j].id() + ",";
            }
            inSyncReplicaList += "]";
            item1.put("ISR", inSyncReplicaList);

            items.add(item1);
        }

        partitionTable.getItems().addAll(items);
    }

    public void startBrowsing(MouseEvent mouseEvent) {
        messagesTable.getItems().clear();
        startButton.setDisable(true);
        stopButton.setDisable(false);

        kafkaConnector.continueBrowsing = true;
        //Create a thread for browsing topic, to not block the UI
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                kafkaConnector.browseTopic(cluster, topic.getText(), messagesTable,startButton, stopButton );
                return 0;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
            }

            @Override
            protected void failed() {
                super.failed();

                //show an alert Dialog
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("Can't browse! You need to set the CONSUMER GROUP and TOPIC READ ACLs.");
                a.setContentText(this.getException().getMessage());
                a.show();
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    public void stopBrowsing(MouseEvent mouseEvent) {
        //todo Cancel the browsing task/thread instead of using boolean
        kafkaConnector.continueBrowsing = false;
        stopButton.setDisable(true);
    }

    public void produceMessage(MouseEvent mouseEvent) {

        kafkaConnector.produceMessage(cluster, topic.getText(), produceMsg.getText());


    }

    public void clearMsgTable(MouseEvent mouseEvent) {

        messagesTable.getItems().clear();

    }
}
