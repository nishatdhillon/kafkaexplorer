package com.kafkaexplorer;

import com.kafkaexplorer.model.Cluster;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

public class TopicBrowserController implements Initializable {

    @FXML
    public TextField topic;
    private TreeView<String> kafkaTreeRef;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    public void populateScreen(String topicName, TreeView<String> clusterTreeView) {
        this.topic.setText(topicName);

        kafkaTreeRef = clusterTreeView;
    }

}
