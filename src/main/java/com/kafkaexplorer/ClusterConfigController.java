package com.kafkaexplorer;

import com.kafkaexplorer.utils.ConfigStore;
import com.kafkaexplorer.utils.KafkaLib;
import com.kafkaexplorer.logger.MyLogger;
import com.kafkaexplorer.model.Cluster;
import com.kafkaexplorer.utils.UI;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;

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
    public ProgressBar progBar1;
    @FXML
    public TextField bootstrap;
    @FXML
    public TextField saslMechanism;
    public TextField consumerGroup;
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

        progBar1.setVisible(false);
    }

    public void populateScreen(Cluster cluster, TreeView<String> clusterTreeView) {
        this.cluster = cluster;
        jks.setText(cluster.getTrustStoreJKS());
        jksPwd.setText(cluster.getTrustStoreJKSPwd());
        bootstrap.setText(cluster.getHostname());
        name.setText(cluster.getName());
        saslMechanism.setText(cluster.getMechanism());
        securityType.setText(cluster.getProtocol());
        jaasConf.setText(cluster.getJaasConfigWithoutPassword());
        consumerGroup.setText(cluster.getConsumerGroup());
        kafkaTreeRef = clusterTreeView;
    }

    public void connectToKafka(MouseEvent mouseEvent) throws IOException {
        //connect to kafka cluster and list all topics
        progBar1.setVisible(true);
        Task task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                KafkaLib kafkaConnector = new KafkaLib();
                progBar1.setDisable(false);

                updateProgress(20, 100);
                kafkaConnector.connect(cluster);

                //kafkaTree
                for (TreeItem child : kafkaTreeRef.getRoot().getChildren()) {

                    if (child.getValue().equals(name.getText())) {

                        updateProgress(40, 100);

                        //remove any existing topics
                        child.getChildren().clear();

                        //Create a SubTreeItem maned "topics"

                        child.getChildren().add(new TreeItem("topics"));
                        TreeItem topicsChildren = (TreeItem) child.getChildren().get(0);

                        //get topic list
                        ArrayList<String> topics = kafkaConnector.listTopics(cluster);
                        Boolean displayAllTopics = false;

                        updateProgress(60, 100);
                        if (cluster.getFilterTopics().size() == 0)
                        {
                            displayAllTopics = true;
                        }

                        for (String topicName : topics) {
                            Boolean displayThisTopic = false;

                            if (displayAllTopics) {
                                displayThisTopic = true;
                            }else {
                                //Search for bookmarked topics
                                for (int i=0; i < cluster.getFilterTopics().size(); i++)
                                {
                                    if (cluster.getFilterTopics().get(i).getName().equals(topicName)){
                                        displayThisTopic = true;
                                    }

                                }
                            }

                            if (displayThisTopic) {
                                TreeItem topicItem = new TreeItem(topicName);
                                topicsChildren.getChildren().add(topicItem);
                            }

                        }
                        updateProgress(80, 100);

                        child.setExpanded(true);
                        topicsChildren.setExpanded(true);
                    } else {
                        child.setExpanded(false);
                    }

                }
                updateProgress(100, 100);
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
            progBar1.setVisible(false);

            //change cluster icon to grey
            setClusterIconToGreen(name.getText(), false);

        });

        task.setOnSucceeded(evt -> {
            //change cluster icon to green
            setClusterIconToGreen(name.getText(), true);

        });

        progBar1.progressProperty().bind(task.progressProperty());

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

        new ConfigStore().saveCluster(cluster);
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
        }
    }

}
