package org.example.bugboard26frontend.controller;

import client.AuthClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.bugboard26frontend.helper.MyAlert;
import org.example.bugboard26frontend.helper.Validator;

/**
 * Controller dedicato alla finestra per la creazione di un nuovo account utente.
 * Gestisce l'acquisizione dei dati dal form, email, password
 * e l'invio della richiesta di registrazione al backend.
 */
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

    /**
     * Cattura la selezione della voce "ADMIN" nel menu a tendina,
     * salvando il valore in memoria e aggiornando l'etichetta visibile all'utente.
     */
    @FXML
    protected void onAdminSelezionato() {
        ruoloScelto = "ADMIN";
        ruoloMenuButton.setText("Ruolo: ADMIN");
    }

    /**
     * Cattura la selezione della voce "INTERNAL_USER" nel menu a tendina,
     * salvando il valore in memoria e aggiornando l'etichetta visibile all'utente.
     */
    @FXML
    protected void onInternalUserSelezionato() {
        ruoloScelto = "INTERNAL_USER";
        ruoloMenuButton.setText("Ruolo: INTERNAL_USER");
    }

    /**
     * Cattura la selezione della voce "EXTERNAL_USER" nel menu a tendina,
     * salvando il valore in memoria e aggiornando l'etichetta visibile all'utente.
     */
    @FXML
    protected void onExternalUserSelezionato() {
        ruoloScelto = "EXTERNAL_USER";
        ruoloMenuButton.setText("Ruolo: EXTERNAL_USER");
    }

    /**
     * Gestisce il flusso di convalida e invio del form.
     * Verifica in sequenza che la sessione sia attiva, che nessun campo sia vuoto, che email e password
     * rispettino i formati di sicurezza previsti, e che sia stato scelto un ruolo.
     * Se tutti i controlli passano, invoca la chiamata di rete per creare l'utente.
     */
    @FXML
    protected void onConfermaButtonClick() {
        Stage stage =  (Stage) confermaButton.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        String email = emailField.getText();
        String password = passwordField.getText();

        if(email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            MyAlert.mostraAlert(Alert.AlertType.WARNING, "Attenzione!", "Compilare tutti i campi (Email e Password).");
            return;
        }


        if(ruoloScelto.isEmpty()) {
            MyAlert.mostraAlert(Alert.AlertType.WARNING, "Attenzione!", "Selezionare un ruolo dal menu a tendina.");
            return;
        }

        if(Validator.emailValidator(email) && Validator.passwordValidator(password)){
            Validator.backEndValidator(() -> {
                boolean success = authClient.registerUser(email, password, ruoloScelto);

                if (success) chiudiFinestra();
                else MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore!", "Impossibile creare l'utente. Controlla che l'email non sia già in uso.");
            });
        }
        MyAlert.mostraAlert(Alert.AlertType.INFORMATION, "Successo!", "Utente creato con successo.");
    }

    /**
     * Annulla l'operazione di creazione utente chiudendo immediatamente la finestra di dialogo,
     * senza effettuare alcuna chiamata al server.
     */
    @FXML
    protected void onAnnullaButtonClick() {
        chiudiFinestra();
    }

    /**
     * Chiude la finestra corrente.
     */
    private void chiudiFinestra() {
        Stage stage = (Stage) confermaButton.getScene().getWindow();
        stage.close();
    }
}