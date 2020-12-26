package com.kafkaexplorer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ClusterConfigController implements Initializable {

    public TextField bootstrap;
    public TextField name;

    private String clusterName;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.name.setText(clusterName);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }


    public void populateScreen() {



    }

}
