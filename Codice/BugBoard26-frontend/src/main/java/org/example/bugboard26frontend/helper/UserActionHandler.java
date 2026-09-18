package org.example.bugboard26frontend.helper;

import client.AuthClient;
import javafx.scene.control.Alert;
import model.AuthUser;

public class UserActionHandler {
    private static final AuthClient authClient = new AuthClient();

    private UserActionHandler() {}


    public static void cambiaStatoAccount(AuthUser user, Runnable onSuccess){
        if(user == null) return;

        boolean success = authClient.cambiaStatoUtente(user.getId());

        if (success) {
            MyAlert.mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Stato dell'utente aggiornato");
            if(onSuccess != null) onSuccess.run();
        }
        else MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore", "Impossibile cambiare lo stato dell'utente.");
    }

}
