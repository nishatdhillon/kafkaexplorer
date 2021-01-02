package com.kafkaexplorer;

import com.kafkaexplorer.kafkaconnector.KafkaLib;
import com.kafkaexplorer.model.Cluster;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.apache.kafka.common.PartitionInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ClusterConfigController implements Initializable {

    @FXML
    public TextField name;

    @FXML
    public TextField securityType;
    public TextField jaasConf;
    public TextField configYamlPath;

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

    }

    public void populateScreen(Cluster cluster, TreeView<String> clusterTreeView) {
        this.cluster = cluster;
        configYamlPath.setText(System.getProperty("user.home") + File.separator + "kafkaexplorer" + File.separator + "config.yaml");
        bootstrap.setText(cluster.getHostname());
        name.setText(cluster.getName());
        saslMechanism.setText(cluster.getMechanism());
        securityType.setText(cluster.getProtocol());
        jaasConf.setText(cluster.getJaasConfig());
        kafkaTreeRef = clusterTreeView;
    }

    public void connectToKafka(MouseEvent mouseEvent) throws IOException {
       //connect to kafka cluster and list all topics
        KafkaLib kafkaConnector = new KafkaLib();
        try {
            kafkaConnector.connect(cluster);


        //get main controller and locate the TreeView
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/kafkaExplorer.fxml"));
        Parent treeView = (Parent) mainLoader.load();
        KafkaExplorerController mainController = mainLoader.getController();

        displayTopicList(name.getText());

        } catch (Exception e) {
            e.printStackTrace();
            //show an alert Dialog
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(e.getMessage());
            a.show();

        }

    }


    private void displayTopicList(String clusterName) {

        //kafkaTree
        for (TreeItem child : kafkaTreeRef.getRoot().getChildren()) {

            if ( child.getValue().equals(clusterName)){
                //remove any existing topics
                child.getChildren().clear();

                //get topic list from kafka
                KafkaLib kafkaConnector = new KafkaLib();

                //Create a SubTreeItem maned "topics"

                child.getChildren().add(new TreeItem("topics"));
                TreeItem topicsChildren = (TreeItem)child.getChildren().get(0);

                Map<String, List<PartitionInfo>> topics = kafkaConnector.listTopics( cluster);
                
                Iterator <Map.Entry<String, List<PartitionInfo>>> iterator = topics.entrySet().iterator();
                
                while (iterator.hasNext()) {
                    Map.Entry<String, List<PartitionInfo>> entry = iterator.next();
                    System.out.println(entry.getKey());

                    TreeItem topic = new TreeItem(entry.getKey());
                    topicsChildren.getChildren().add(topic);
                }

                child.setExpanded(true);
                topicsChildren.setExpanded(true);
            }
            else
            {
                child.setExpanded(false);
            }

        }
        //kafkaTree.getRoot().getChildren().removeAll();
    }

}
