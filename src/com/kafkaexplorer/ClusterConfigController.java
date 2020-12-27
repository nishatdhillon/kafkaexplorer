package com.kafkaexplorer;

import com.kafkaexplorer.model.Cluster;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    //init security.protocol dropdown list with PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL
        securityType.getItems().add("PLAINTEXT");
        securityType.getItems().add("SSL");
        securityType.getItems().add("SASL_PLAINTEXT");
        securityType.getItems().add("SASL_SSL");
        securityType.setValue("PLAINTEXT");
    }

    public void populateScreen(Cluster cluster) {
        bootstrap.setText(cluster.getHostname());
        name.setText(cluster.getName());
        saslMechanism.setText(cluster.getMechanism());
        securityType.setValue(cluster.getProtocol());
        jaasConf.setText(cluster.getJaasConfig());
    }

}
