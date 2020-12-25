package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

import javax.swing.plaf.basic.BasicTreeUI;
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

        TreeItem<String> root = new TreeItem<>("Kafka Clusters");
        //build kafka cluster tree
        TreeItem cluster = new TreeItem("Cluster01");
        root.getChildren().add(cluster);
        kafkaTree.setRoot(root);
        root.setExpanded(true);

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
