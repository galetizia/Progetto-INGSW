package org.example.bugboard26frontend.helper;

import client.AuthClient;
import javafx.scene.control.Alert;
import model.AuthUser;

/**
 * Classe Helper che funge da intermediario tra l'interfaccia grafica e le chiamate di rete
 * verso il backend per la gestione degli account utente.
 */
public class UserActionHandler {
    private static final AuthClient authClient = new AuthClient();

    private UserActionHandler() {}

    /**
     * Invia una richiesta al server per invertire lo stato di attivazione di un account utente.
     * Al termine dell'operazione, mostra un pop-up visivo
     * con l'esito e innesca l'aggiornamento dell'interfaccia.
     *
     * @param user      L'utente selezionato di cui si desidera modificare lo stato di accesso.
     * @param onSuccess L'azione di callback da eseguire in caso di successo.
     */
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
