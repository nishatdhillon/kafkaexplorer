package com.kafkaexplorer;

import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXToggleNode;
import com.kafkaexplorer.utils.ConfigStore;
import com.kafkaexplorer.utils.CustomFileChooser;
import com.kafkaexplorer.utils.KafkaLib;
import com.kafkaexplorer.logger.MyLogger;
import com.kafkaexplorer.model.Cluster;
import com.kafkaexplorer.utils.UI;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.PartitionInfo;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ClusterConfigController implements Initializable {

    @FXML
    public TextField name;

    @FXML
    public TextField securityType;
    public TextField jaasConf;
    public TextField jks;
    public TextField  jksPwd;
    public StackPane stack;
    @FXML
    public TextField bootstrap;
    @FXML
    public TextField saslMechanism;
    public TextField consumerGroup;
    public GridPane rootGridPane;
    public TextField srUrl;
    public TextField srUser;
    public TextField srPwd;
    private Cluster cluster;
    private TreeView<String> kafkaTreeRef;

    public TextField getName() {
        return name;
    }

    public void setName(TextField name) {
        this.name = name;
    }

    public TextField getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(TextField bootstrap) {
        this.bootstrap = bootstrap;
    }

    public TextField getSaslMechanism() {
        return saslMechanism;
    }

    public void setSaslMechanism(TextField saslMechanism) {
        this.saslMechanism = saslMechanism;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void populateScreen(Cluster cluster, TreeView<String> clusterTreeView) {
        this.cluster = cluster;

        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {

                jks.setText(cluster.getTrustStoreJKS());
                jksPwd.setText(cluster.getTrustStoreJKSPwd());
                bootstrap.setText(cluster.getHostname());
                name.setText(cluster.getName());
                saslMechanism.setText(cluster.getMechanism());
                securityType.setText(cluster.getProtocol());
                //todo mask pwd on screen
                //jaasConf.setText(cluster.getJaasConfig().substring(0, cluster.getJaasConfig().indexOf("password=")) + "password='***masked***';");
                jaasConf.setText(cluster.getJaasConfig());
                consumerGroup.setText(cluster.getConsumerGroup());
                srUrl.setText(cluster.getSrUrl());
                srUser.setText(cluster.getSrUser());
                srPwd.setText(cluster.getSrPwd());

                kafkaTreeRef = clusterTreeView;

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
                a.setHeaderText("Error!");
                a.setContentText(this.getException().getMessage());
                a.show();
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    public void connectToKafka(MouseEvent mouseEvent) throws IOException {
        //connect to kafka cluster and list all topics


        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {

                ((ProgressIndicator)rootGridPane.getScene().lookup("#progBar2")).setVisible(true);

                KafkaLib kafkaConnector = new KafkaLib();
                kafkaConnector.connect(cluster);



                //Build and expand kafkaTree
                for (TreeItem child : kafkaTreeRef.getRoot().getChildren()) {
                    //Locate cluster to update
                    if (child.getValue().equals(name.getText())) {
                        //remove any existing topics and consumers children
                        child.getChildren().clear();

                        //Create a SubTreeItem maned "topics"

                        HBox cellBox = new HBox(2);

                        TreeItem topicsRoot = new TreeItem("");
                        JFXToggleButton toggleButton1 = new JFXToggleButton();
                        toggleButton1.setText("hide internal)");
                        toggleButton1.setSelected(true);
                        toggleButton1.setOnAction(new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent event) {
                                //TODO
                                //empty topics list for this cluster

                               // if selected (hide internal topics)

                                // if not selected (show all topics )


                            }

                        });

                        Label label = new Label("topics (");
                        cellBox.setAlignment(Pos.CENTER_LEFT);
                        cellBox.getChildren().addAll(label, toggleButton1);

                        topicsRoot.setGraphic(cellBox);

                        child.getChildren().add(topicsRoot);
                        TreeItem topicsChildren = (TreeItem) child.getChildren().get(0);

                        //get topic list
                        ArrayList<String> topics = kafkaConnector.listTopics(cluster);

                        for (String topicName : topics) {

                            //by default, hide internal topics (starting by _)
                            if (!topicName.startsWith("_")) {

                                TreeItem topicItem = new TreeItem(topicName);

                                topicsChildren.getChildren().add(topicItem);
                            }

                        }


                        //Create a SubTreeItem maned "consumer groups"
                        TreeItem consumerNode = new TreeItem("consumer-groups");

                        //get consumer groups list
                        ArrayList<String> consumers = kafkaConnector.listConsumerGroups(cluster);
                        for (String consumerGroupName : consumers) {
                            TreeItem consumerItem = new TreeItem(consumerGroupName);
                            consumerNode.getChildren().add(consumerItem);
                        }
                        consumerNode.setExpanded(true);
                        child.getChildren().add(consumerNode);

                        child.setExpanded(true);
                        topicsChildren.setExpanded(true);


                    } else {
                        child.setExpanded(false);
                    }

                }
                ((ProgressIndicator)rootGridPane.getScene().lookup("#progBar2")).setVisible(false);
                return null;
            }
        };

        task.setOnFailed(evt -> {

            StringWriter errors = new StringWriter();
            task.getException().printStackTrace(new PrintWriter(errors));

            MyLogger.logDebug("The task failed with the following exception: " + errors.toString());
            //show an alert Dialog
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(errors.toString());
            a.show();
            ((ProgressIndicator)rootGridPane.getScene().lookup("#progBar2")).setVisible(false);

            //change cluster icon to grey
            setClusterIconToGreen(name.getText(), false);

        });

        task.setOnSucceeded(evt -> {
            //change cluster icon to green
            setClusterIconToGreen(name.getText(), true);
            ((ProgressIndicator)rootGridPane.getScene().lookup("#progBar2")).setVisible(false);
        });

        new Thread(task).start();

    }


    public void saveCluster(MouseEvent mouseEvent) throws IOException {

        cluster.setTrustStoreJKS(jks.getText());
        cluster.setTrustStoreJKSPwd(jksPwd.getText());
        cluster.setHostname(bootstrap.getText());
        cluster.setName(name.getText());
        cluster.setMechanism(saslMechanism.getText());
        cluster.setProtocol(securityType.getText());
        cluster.setJaasConfig(jaasConf.getText());
        cluster.setConsumerGroup(consumerGroup.getText());

        cluster.setSrUrl(srUrl.getText());
        cluster.setSrUser(srUser.getText());
        cluster.setSrPwd(srPwd.getText());

        new ConfigStore().saveCluster(cluster);

        //refresh cluster list
        new UI().refreshClusterList(kafkaTreeRef);

    }


    private void setClusterIconToGreen(String clusterName, boolean isGreen) {

        Node clusterIconGreen = new ImageView(new Image(getClass().getResourceAsStream("/kafka-icon-green.png")));
        Node clusterIconGrey = new ImageView(new Image(getClass().getResourceAsStream("/kafka-icon-grey.png")));

        for (TreeItem child : kafkaTreeRef.getRoot().getChildren()) {

            if (child.getValue().equals(clusterName)) {
                if (isGreen)
                    child.setGraphic(clusterIconGreen);
                else
                    child.setGraphic(clusterIconGrey);
            }
        }

    }

    public void deleteCluster(MouseEvent mouseEvent) {
        //Ask to confirm deletion
        if (new UI().confirmationDialog(Alert.AlertType.CONFIRMATION, "Are you sure?")){
            new ConfigStore().deleteCluster(cluster);
            //refresh cluster list
            try {
                new UI().refreshClusterList(kafkaTreeRef);
                this.rootGridPane.getChildren().clear();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void browseJKS(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JKS files (*.jks)", "*.jks"));
        Stage stage = (Stage) rootGridPane.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null)
            this.jks.setText(selectedFile.getPath());
    }
}
