package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import javax.swing.plaf.basic.BasicTreeUI;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

@FXML
private TreeView<String> kafkaTree;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        TreeItem<String> root = new TreeItem<>("Kafka Clusters");
        //build kafka cluster tree
        TreeItem cluster = new TreeItem("Cluster01");
        root.getChildren().add(cluster);
        kafkaTree.setRoot(root);
        root.setExpanded(true);

    }
}
