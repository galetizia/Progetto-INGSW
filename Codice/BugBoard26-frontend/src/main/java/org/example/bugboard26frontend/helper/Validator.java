package org.example.bugboard26frontend.helper;

import client.AuthSession;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe Helper che centralizza le logiche di validazione dell'applicazione.
 * Si occupa di verificare la correttezza degli input (email, password), lo stato della sessione
 * e di gestire in sicurezza le comunicazioni con il server (error handling).
 */
public class Validator {
    private static final Logger logger = LoggerFactory.getLogger(Validator.class);

    private Validator() {}

    /**
     * Interfaccia funzionale utilizzata per incapsulare blocchi di codice che effettuano
     * chiamate di rete al backend e che potrebbero generare eccezioni.
     */
    @FunctionalInterface
    public interface BackendAction {
        /**
         * Esegue l'operazione di rete definita.
         *
         * @throws Exception Qualsiasi eccezione generata durante la comunicazione col server.
         */
        void execute() throws Exception;
    }

    /**
     * Verifica che la password fornita rispetti i criteri minimi di sicurezza tramite un'espressione regolare.
     * Se la password è debole, interrompe il flusso visivo mostrando un avviso all'utente.
     *
     * @param password La password in chiaro da validare.
     */
    public static void passwordValidator(String password) {
        if (!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$")) {
            MyAlert.mostraAlert(Alert.AlertType.WARNING, "Attenzione!",
                    "La password deve essere di almeno 8 caratteri, contenere un numero e un carattere speciale (@,#,$,%,^,&,+,=,!)");
        }
    }

    /**
     * Controlla la validità sintattica di un indirizzo email tramite espressione regolare.
     * Mostra un messaggio di avviso se il formato non è conforme (es. manca la @ o il dominio).
     *
     * @param email La stringa contenente l'indirizzo email da verificare.
     */
    public static void emailValidator(String email) {
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            MyAlert.mostraAlert(Alert.AlertType.WARNING, "Attenzione!", "Inserire indirizzo email valido");
        }
    }

    /**
     * Verifica se è presente un utente validamente autenticato nella sessione corrente.
     * In caso negativo, informa l'utente e lo reindirizza forzatamente alla schermata di login.
     *
     * @param stage La finestra corrente, necessaria per effettuare il cambio di scena.
     * @return true se la sessione è valida e attiva, false se l'utente non è loggato.
     */
    public static boolean sessionValidator(Stage stage){
        if(!AuthSession.getInstance().isLoggedIn()){
            MyAlert.mostraAlert(Alert.AlertType.WARNING, "Sessione scaduta!", "La tua sessione non è attiva. Effettua nuovamente il login");
            WindowHelper.tornaAlLogin(stage);
            return false;
        }
        return true;
    }

    /**
     * Esegue in sicurezza una chiamata al backend passata come funzione lambda.
     * Intercetta eventuali eccezioni (es. server irraggiungibile), logga il problema
     * per il debugging e mostra un messaggio di errore all'utente senza far crashare il client JavaFX.
     *
     * @param fun Il blocco di codice da eseguire all'interno del try-catch protetto.
     */
    public static void backEndValidator(BackendAction fun) {


        try{
            if(fun != null) fun.execute();
        } catch (Exception e){
            MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore di connessione.", "Impossibile contattare il server. Verifica che il backend sia in esecuzione.");
            logger.error(e.getMessage(), e);
        }
    }

}
