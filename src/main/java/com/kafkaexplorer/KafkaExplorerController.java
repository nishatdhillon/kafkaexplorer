package com.kafkaexplorer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.kafkaexplorer.logger.MyLogger;
import com.kafkaexplorer.model.Cluster;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;


public class KafkaExplorerController implements Initializable {

    @FXML
    private TreeView<String> kafkaTree;

    @FXML
    private SplitPane mainContent;

    private Cluster[] clusters;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Validate config.yaml file
        HashMap<String, String> errorList = new Utils().validateYamlConfig();

        if (errorList.size() > 0) {
            //show an alert Dialog
            Alert a = new Alert(Alert.AlertType.ERROR);
            String errorMessage = "";

            //Todo manage multiple error messages
            errorList.entrySet().forEach(entry -> {
                a.setHeaderText(entry.getKey());
                a.setContentText(entry.getValue());
            });

            a.showAndWait();

            //Exit the application
            Platform.exit();
            System.exit(0);
        }
        //Load config.yaml file from the user.home/kafkaexplorer/config.yaml
        String path = System.getProperty("user.home") + File.separator + "kafkaexplorer" + File.separator + "config.yaml";
        File file = new File(path);

        // Instantiating a new ObjectMapper as a YAMLFactory
        ObjectMapper om = new ObjectMapper(new YAMLFactory());

        // Mapping the cluster Array from the YAML file to the Cluster class
        try {
            clusters = om.readValue(file, com.kafkaexplorer.model.Cluster[].class);

            TreeItem<String> root = new TreeItem<>("Kafka Clusters");

            for (int i = 0; i < clusters.length; i++) {
                //build kafka cluster tree
                Node rootIcon = new ImageView(new Image(getClass().getResourceAsStream("/kafka-icon-grey.png")));
                TreeItem<String> clusterItem = new TreeItem<String>(clusters[i].getName(), rootIcon);

                root.getChildren().add(clusterItem);
            }


            kafkaTree.setRoot(root);
            root.setExpanded(true);

            MyLogger.logDebug("KafkaExplorerController initialized! ");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onMouseClicked(MouseEvent mouseEvent) {

        //Open the topicBrowser screen
        try {
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
                        if (clusters[i].getName() == selectedItem.getValue()) {
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

                    TopicBrowserController topicBrowserController = topicBrowserLoader.getController();

                    //Build cluster object from cluster name
                    Cluster cluster = new Utils().getClusterByName(selectedItem.getParent().getParent().getValue().toString());

                    topicBrowserController.populateScreen(cluster, selectedItem.getValue().toString(), kafkaTree);
                    //mainContent.getChildren().setAll(mainRoot);

                    if (mainContent.getItems().size() > 1)
                        mainContent.getItems().remove(1);

                    mainContent.getItems().add(mainRoot);


                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
