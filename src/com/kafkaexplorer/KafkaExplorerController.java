package com.kafkaexplorer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.kafkaexplorer.model.Cluster;
import com.kafkaexplorer.model.Clusters;
import com.sun.source.tree.Tree;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class KafkaExplorerController implements Initializable {

@FXML
private TreeView<String> kafkaTree;

@FXML
private AnchorPane mainContent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Load config.yaml file from the /resources folder
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File file = new File(classLoader.getResource("config.yaml").getFile());

        // Instantiating a new ObjectMapper as a YAMLFactory
        ObjectMapper om = new ObjectMapper(new YAMLFactory());

        // Mapping the cluster Array from the YAML file to the Cluster class
        try {
            Clusters clusters = om.readValue(file, com.kafkaexplorer.model.Clusters.class);

            TreeItem<String> root = new TreeItem<>("Kafka Clusters");

            clusters.getClusters().forEach((cluster) -> {
                //build kafka cluster tree
                TreeItem clusterItem = new TreeItem(cluster.getName());
                root.getChildren().add(clusterItem);
            });

        kafkaTree.setRoot(root);
        root.setExpanded(true);

        } catch (IOException e) {
            e.printStackTrace();
        }





        try {
            //AnchorPane mainRoot = new FXMLLoader(getClass().getResource("clusterConfig.fxml")).load();
            //mainContent.getChildren().setAll(mainRoot);

            AnchorPane mainRoot = new FXMLLoader(getClass().getResource("topicBrowser.fxml")).load();
            mainContent.getChildren().setAll(mainRoot);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
