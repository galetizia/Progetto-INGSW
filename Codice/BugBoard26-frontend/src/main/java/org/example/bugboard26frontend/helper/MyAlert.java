package org.example.bugboard26frontend.helper;

import javafx.scene.control.Alert;

/**
 * Classe Helper per la generazione centralizzata e semplificata delle finestre di dialogo di JavaFX.
 * Permette di mantenere uno stile visivo coerente in tutta l'applicazione e riduce la duplicazione del codice.
 */
public class MyAlert {

    private MyAlert() {}

    /**
     * Crea e mostra immediatamente a schermo una finestra di dialogo modale, bloccando l'interazione
     * con l'applicazione finché l'utente non la chiude. Creata per i messaggi informativi o di errore.
     *
     * @param tipo      La tipologia di alert che ne determina l'icona e lo stile.
     * @param titolo    Il titolo testuale della finestra.
     * @param contenuto Il messaggio di dettaglio da mostrare all'utente.
     */
    public static void mostraAlert(Alert.AlertType tipo, String titolo, String contenuto) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }

    /**
     * Prepara e configura una finestra di dialogo per la conferma di un'operazione,
     * restituendo l'oggetto al chiamante senza mostrarlo subito. Questo permette a chi invoca il metodo
     * di gestire in autonomia le risposte dei bottoni.
     *
     * @param titolo      Il titolo testuale della finestra.
     * @param headerText  L'intestazione in grassetto.
     * @param contentText Il messaggio descrittivo o di avviso sottostante.
     * @return L'oggetto Alert di tipo CONFIRMATION pronto per essere mostrato e gestito.
     */
    public static Alert mostraAlertConfirmation(String titolo, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titolo);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert;
    }
}
