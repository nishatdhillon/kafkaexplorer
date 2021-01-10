package com.kafkaexplorer;

import com.kafkaexplorer.kafkaconnector.KafkaLib;
import com.kafkaexplorer.logger.MyLogger;
import com.kafkaexplorer.model.Cluster;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.kafka.common.PartitionInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ClusterConfigController implements Initializable {

    @FXML
    public TextField name;

    @FXML
    public TextField securityType;
    public TextField jaasConf;
    public TextField configYamlPath;
    public StackPane stack;
    public ProgressBar progBar1;

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

    private Cluster cluster;

    @FXML
    public TextField bootstrap;

    @FXML
    public TextField saslMechanism;

    private TreeView<String> kafkaTreeRef;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        progBar1.setVisible(false);
    }

    public void populateScreen(Cluster cluster, TreeView<String> clusterTreeView) {
        this.cluster = cluster;
        configYamlPath.setText(System.getProperty("user.home") + File.separator + "kafkaexplorer" + File.separator + "config.yaml");
        bootstrap.setText(cluster.getHostname());
        name.setText(cluster.getName());
        saslMechanism.setText(cluster.getMechanism());
        securityType.setText(cluster.getProtocol());
        jaasConf.setText(cluster.getJaasConfigWithoutPassword());
        kafkaTreeRef = clusterTreeView;
    }

    public void connectToKafka(MouseEvent mouseEvent) throws IOException {
       //connect to kafka cluster and list all topics
        progBar1.setVisible(true);
        Task task = new Task<Void>() {
            @Override public Void call() throws Exception {
                            KafkaLib kafkaConnector = new KafkaLib();
                            progBar1.setDisable(false);

                            updateProgress(20, 100);
                            kafkaConnector.connect(cluster);

                            //kafkaTree
                            for (TreeItem child : kafkaTreeRef.getRoot().getChildren()) {

                                if ( child.getValue().equals(name.getText())){

                                    updateProgress(40, 100);

                                    //remove any existing topics
                                    child.getChildren().clear();

                                    //Create a SubTreeItem maned "topics"

                                    child.getChildren().add(new TreeItem("topics"));
                                    TreeItem topicsChildren = (TreeItem)child.getChildren().get(0);

                                    //get topic list
                                    Map<String, List<PartitionInfo>> topics = kafkaConnector.listTopics( cluster);

                                    updateProgress(60, 100);

                                    Iterator <Map.Entry<String, List<PartitionInfo>>> iterator = topics.entrySet().iterator();

                                    while (iterator.hasNext()) {


                                        Map.Entry<String, List<PartitionInfo>> entry = iterator.next();
                                        MyLogger.logDebug(entry.getKey());

                                        TreeItem topic = new TreeItem(entry.getKey());
                                        topicsChildren.getChildren().add(topic);
                                    }
                                    updateProgress(80, 100);

                                    child.setExpanded(true);
                                    topicsChildren.setExpanded(true);

                                    //change cluster icon from grey to green
                                    Node rootIcon =  new ImageView(new Image(getClass().getResourceAsStream("/kafka-icon-green.png")));
                                    //TreeItem<String> clusterItem = new TreeItem<String>(clusters[i].getName(),rootIcon);

                                    //child.setGraphic(rootIcon);
                                }
                                else
                                {
                                    child.setExpanded(false);
                                }

                            }
                            updateProgress(100, 100);
                return null;
            }
        };

        task.setOnFailed(evt -> {
            MyLogger.logDebug("The task failed with the following exception: " + task.getException().getMessage());
            //show an alert Dialog
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(task.getException().getMessage());
            a.show();
            progBar1.setVisible(false);

        });


        progBar1.progressProperty().bind(task.progressProperty());

        new Thread(task).start();

    }


}
