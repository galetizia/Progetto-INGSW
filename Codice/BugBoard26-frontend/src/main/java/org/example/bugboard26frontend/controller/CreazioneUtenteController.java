package org.example.bugboard26frontend.controller;

import client.AuthClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.bugboard26frontend.helper.MyAlert;
import org.example.bugboard26frontend.helper.Validator;

public class CreazioneUtenteController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private MenuButton ruoloMenuButton;
    @FXML private Button confermaButton;

    private String ruoloScelto = "";

    AuthClient authClient;
    public void setAuthClient(AuthClient authClient){
        this.authClient = authClient;
    }

    @FXML
    protected void onAdminSelezionato() {
        ruoloScelto = "ADMIN";
        ruoloMenuButton.setText("Ruolo: ADMIN");
    }

    @FXML
    protected void onInternalUserSelezionato() {
        ruoloScelto = "INTERNAL_USER";
        ruoloMenuButton.setText("Ruolo: INTERNAL_USER");
    }

    @FXML
    protected void onExternalUserSelezionato() {
        ruoloScelto = "EXTERNAL_USER";
        ruoloMenuButton.setText("Ruolo: EXTERNAL_USER");
    }

    @FXML
    protected void onConfermaButtonClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if(email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            MyAlert.mostraAlert(Alert.AlertType.WARNING, "Attenzione!", "Compilare tutti i campi (Email e Password).");
            return;
        }

        Validator.emailValidator(email);
        Validator.passwordValidator(password);

        if(ruoloScelto.isEmpty()) {
            MyAlert.mostraAlert(Alert.AlertType.WARNING, "Attenzione!", "Selezionare un ruolo dal menu a tendina.");
            return;
        }

        boolean success = authClient.registerUser(email, password, ruoloScelto);

        if (success) chiudiFinestra();
        else MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore!", "Impossibile creare l'utente. Controlla che l'email non sia già in uso.");
    }

    @FXML
    protected void onAnnullaButtonClick() {
        chiudiFinestra();
    }

    private void chiudiFinestra() {
        Stage stage = (Stage) confermaButton.getScene().getWindow();
        stage.close();
    }
}