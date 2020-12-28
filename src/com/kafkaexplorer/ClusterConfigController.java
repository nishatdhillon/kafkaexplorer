package com.kafkaexplorer;

import com.kafkaexplorer.model.Cluster;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
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
        bootstrap.setText(cluster.getHostname());
        name.setText(cluster.getName());
        saslMechanism.setText(cluster.getMechanism());
        securityType.setValue(cluster.getProtocol());
        jaasConf.setText(cluster.getJaasConfig());
        kafkaTreeRef = clusterTreeView;
    }

    public void connectToKafka(MouseEvent mouseEvent) throws IOException {
       //connect to kafka cluster and list all topics
       //get main controller and locate the TreeView
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("kafkaExplorer.fxml"));
        Parent treeView = (Parent) mainLoader.load();
        KafkaExplorerController mainController = mainLoader.getController();

        displayTopicList(name.getText());

    }


    private void displayTopicList(String clusterName) {

        //kafkaTree
        for (TreeItem child : kafkaTreeRef.getRoot().getChildren()) {
            System.out.println(child.getValue());
            if ( child.getValue().equals(clusterName)){
                //remove any existing topics
                child.getChildren().removeAll();
                child.getChildren().clear();

                //get topic list from kafka
                //todo
                System.out.println("list topics of " + clusterName);
                TreeItem topic1 = new TreeItem("topic1");
                child.getChildren().add(topic1);

                TreeItem topic2 = new TreeItem("topic2");
                child.getChildren().add(topic2);

                TreeItem topic3 = new TreeItem("topic3");
                child.getChildren().add(topic3);
            }

        }
        //kafkaTree.getRoot().getChildren().removeAll();
    }

}
