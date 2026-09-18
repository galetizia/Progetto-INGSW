package org.example.bugboard26frontend.helper;

import javafx.scene.control.Alert;

public class MyAlert {

    private MyAlert() {}

    public static void mostraAlert(Alert.AlertType tipo, String titolo, String contenuto) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }


    public static Alert mostraAlertConfirmation(String titolo, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert;
    }
}
