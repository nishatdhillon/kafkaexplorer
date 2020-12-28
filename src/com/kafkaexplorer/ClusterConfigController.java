package com.kafkaexplorer;

import com.kafkaexplorer.kafkaconnector.KafkaLib;
import com.kafkaexplorer.model.Cluster;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.kafka.common.PartitionInfo;

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
    public ChoiceBox securityType;
    public TextField jaasConf;

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
    //init security.protocol dropdown list with PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL
        securityType.getItems().add("PLAINTEXT");
        securityType.getItems().add("SSL");
        securityType.getItems().add("SASL_PLAINTEXT");
        securityType.getItems().add("SASL_SSL");
        securityType.setValue("PLAINTEXT");
    }

    public void populateScreen(Cluster cluster, TreeView<String> clusterTreeView) {
        this.cluster = cluster;
        bootstrap.setText(cluster.getHostname());
        name.setText(cluster.getName());
        saslMechanism.setText(cluster.getMechanism());
        securityType.setValue(cluster.getProtocol());
        jaasConf.setText(cluster.getJaasConfig());
        kafkaTreeRef = clusterTreeView;
    }

    public void connectToKafka(MouseEvent mouseEvent) throws IOException {
       //connect to kafka cluster and list all topics
        KafkaLib kafkaConnector = new KafkaLib();
        kafkaConnector.connect(cluster);

       //get main controller and locate the TreeView
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("kafkaExplorer.fxml"));
        Parent treeView = (Parent) mainLoader.load();
        KafkaExplorerController mainController = mainLoader.getController();

        displayTopicList(name.getText());

    }


    private void displayTopicList(String clusterName) {

        //kafkaTree
        for (TreeItem child : kafkaTreeRef.getRoot().getChildren()) {

            if ( child.getValue().equals(clusterName)){
                //remove any existing topics
                child.getChildren().clear();

                //get topic list from kafka
                KafkaLib kafkaConnector = new KafkaLib();

                Map<String, List<PartitionInfo>> topics = kafkaConnector.listTopics( cluster);


                Iterator <Map.Entry<String, List<PartitionInfo>>> iterator = topics.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String, List<PartitionInfo>> entry = iterator.next();
                    System.out.println(entry.getKey());

                    TreeItem topic = new TreeItem(entry.getKey());
                    child.getChildren().add(topic);
                }

                child.setExpanded(true);
            }
            else
            {
                child.setExpanded(false);
            }

        }
        //kafkaTree.getRoot().getChildren().removeAll();
    }

}
