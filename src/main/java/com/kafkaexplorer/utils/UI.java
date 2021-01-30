package com.kafkaexplorer.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

import java.util.Optional;
import java.util.Properties;

public class UI {

    public UI() {
    }

    public static boolean confirmationDialog(Alert.AlertType alertType, String statement) {
        Alert alert = new Alert(alertType, statement);
        alert.initStyle(StageStyle.UNDECORATED);
        Optional<ButtonType> choose = alert.showAndWait();
        return choose.get() == ButtonType.OK;
    }

}
