package com.kafkaexplorer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.kafkaexplorer.utils.ConfigStore;
import com.kafkaexplorer.logger.MyLogger;
import com.kafkaexplorer.model.Cluster;
import com.kafkaexplorer.utils.UI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.UUID;


public class KafkaExplorerController implements Initializable {

    public ProgressIndicator progBar2;
    @FXML
    private TreeView<String> kafkaTree;

    @FXML
    private SplitPane mainContent;

    private Cluster[] clusters;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            new UI().refreshClusterList(kafkaTree);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onMouseClicked(MouseEvent mouseEvent) {

        //Open the topicBrowser screen
        try {
            clusters = new ConfigStore().loadClusters();
            // Get selected Node
            Node node = mouseEvent.getPickResult().getIntersectedNode();

            //Ensure that user clicked on a TreeCell
            if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
                TreeItem selectedItem = (TreeItem) kafkaTree.getSelectionModel().getSelectedItem();
                //selectedItem is a cluster, display cluster config
                if (selectedItem.getParent() != null && selectedItem.getParent().getValue() == "Kafka Clusters") {
                    FXMLLoader clusterConfigLoader = new FXMLLoader(getClass().getResource("/clusterConfig.fxml"));
                    GridPane mainRoot = clusterConfigLoader.load();
                    ClusterConfigController clusterConfigController = clusterConfigLoader.getController();
                    //find selected cluster from Clusters Array
                    Cluster selectedCluster = null;

                    for (int i = 0; i < clusters.length; i++) {
                        if (clusters[i].getName().equals(selectedItem.getValue())) {
                            selectedCluster = new Cluster(clusters[i]);
                        }
                    }

                    if (selectedCluster != null) {
                        clusterConfigController.populateScreen(selectedCluster, kafkaTree);
                       // mainContent.getChildren().setAll(mainRoot);
                        if (mainContent.getItems().size() > 1)
                             mainContent.getItems().remove(1);

                        mainContent.getItems().add(mainRoot);

                    } else {
                        //todo
                      //  mainContent.getChildren().clear();
                    }

                } //If selectedItem is a topic, display topic browser screen
                else if (selectedItem.getParent() != null && selectedItem.getParent().getValue() == "topics") {

                    FXMLLoader topicBrowserLoader = new FXMLLoader(getClass().getResource("/topicBrowser.fxml"));
                    VBox mainRoot = topicBrowserLoader.load();

                    //Display Progress bar
                    progBar2.setVisible(true);

                    TopicBrowserController topicBrowserController = topicBrowserLoader.getController();

                    //Build cluster object from cluster name
                    Cluster cluster = new ConfigStore().getClusterByName(selectedItem.getParent().getParent().getValue().toString());


                    //Switch to Asyn

                    topicBrowserController.populateScreen(cluster, selectedItem.getValue().toString(), kafkaTree);
                    //delete: mainContent.getChildren().setAll(mainRoot);

                    if (mainContent.getItems().size() > 1)
                        mainContent.getItems().remove(1);

                    mainContent.getItems().add(mainRoot);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void addCluster(MouseEvent mouseEvent) {

        try {
            clusters = new ConfigStore().loadClusters();
            Cluster c1 = new Cluster();
            c1.setName("New Cluster");

            c1.setId(UUID.randomUUID().toString().replace("-", ""));

            new ConfigStore().addCluster(c1);
            new UI().refreshClusterList(kafkaTree);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
