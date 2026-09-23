package org.example.bugboard26frontend.controller;

import client.AuthClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.bugboard26frontend.helper.MyAlert;
import org.example.bugboard26frontend.helper.WindowHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller dedicato alla schermata del Login.
 * Gestisce l'acquisizione delle credenziali, la validazione formale lato client,
 * l'invio della richiesta al server e il successivo reindirizzamento
 * verso la schermata corretta in base al ruolo dell'utente riconosciuto.
 */
public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    AuthClient authClient;
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    public void setAuthClient(AuthClient authClient){
        this.authClient = authClient;
    }


    /**
     * Gestisce il flusso del click sul pulsante di Login.
     * Verifica che i campi non siano vuoti e tenta l'autenticazione tramite il server.
     * In caso di successo, interroga la sessione appena creata per scoprire il ruolo
     * dell'utente e delega al WindowHelper il compito di caricare la schermata appropriata.
     * Intercetta e gestisce visivamente eccezioni specifiche.
     */
    @FXML
    protected void onLoginButtonClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if(email.isEmpty() || password.isEmpty()) {
            MyAlert.mostraAlert(Alert.AlertType.WARNING, "Attenzione!", "Compilare tutti i campi!");
            return;
        }

        try{
            boolean success = authClient.login(email, password);

            if(success) {
                Stage stage = (Stage) emailField.getScene().getWindow();
                var ruolo = client.AuthSession.getInstance().getUtenteCorrente().getRuolo();

                WindowHelper.apriHome(stage, ruolo);
            } else {
                MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore!", "Errore imprevisto");
            }
        } catch (IllegalAccessException e) {
            MyAlert.mostraAlert(Alert.AlertType.ERROR, "Accesso negato!", e.getMessage());
            logger.error(e.getMessage(), e);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(e.getMessage(), e);

        } catch (Exception e) {
            MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore di Sistema", "Impossibile contattare il server. Riprova più tardi.");
            logger.error(e.getMessage(), e);
        }
    }
}