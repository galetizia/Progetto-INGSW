package org.example.bugboard26frontend.helper;

import client.AuthClient;
import client.AuthSession;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class CambioPasswordDialog {
    private static final AuthClient authClient = new AuthClient();
    private static final MyAlert alert = new MyAlert();

    public void mostra() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cambio Password");
        dialog.setHeaderText("Inserire i dati per il cambio password");

        ButtonType confermaButton = new ButtonType("Conferma",  ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confermaButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField emailField = new TextField();
        emailField.setText(AuthSession.getInstance().getUtenteCorrente().getEmail());
        emailField.setEditable(false);
        emailField.setStyle("-fx-background-color: #e0e0e0;");

        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setPromptText("Password attuale");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nuova Password");

        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(new Label("Password Attuale:"), 0, 1);
        grid.add(oldPasswordField, 1, 1);
        grid.add(new Label("Nuova Password:"), 0, 2);
        grid.add(newPasswordField, 1, 2);

        javafx.scene.Node confirmBtnNode = dialog.getDialogPane().lookupButton(confermaButton);
        confirmBtnNode.setDisable(true);

        javafx.beans.value.ChangeListener<String> inputListener = (obs, oldV, newV) -> {
            confirmBtnNode.setDisable(
                    oldPasswordField.getText().trim().isEmpty() ||
                            newPasswordField.getText().trim().isEmpty()
            );
        };

        oldPasswordField.textProperty().addListener(inputListener);
        newPasswordField.textProperty().addListener(inputListener);


        dialog.getDialogPane().setContent(grid);


        dialog.showAndWait().ifPresent(response -> {
            if (response == confermaButton) {
                gestioneConferma(emailField.getText(), oldPasswordField.getText(), newPasswordField.getText());
            }
        });
    }

    private void gestioneConferma(String email, String oldPassword, String newPassword) {

        boolean success = authClient.changePassword(email, oldPassword, newPassword);

        // da implementare il controllo della password
        if (success){
            alert.mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Password aggiornata con successo");
        } else {
            alert.mostraAlert(Alert.AlertType.ERROR, "Errore", "Errore nel cambio password, ricontrollare i dati");
        }
    }
}
