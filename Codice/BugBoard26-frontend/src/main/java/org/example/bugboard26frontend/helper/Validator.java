package org.example.bugboard26frontend.helper;

import client.AuthSession;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Validator {
    private static final Logger logger = LoggerFactory.getLogger(Validator.class);

    private Validator() {}

    @FunctionalInterface
    public interface BackendAction {
        void execute() throws Exception;
    }

    public static boolean passwordValidator(String password) {
        if (!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$")) {
            MyAlert.mostraAlert(Alert.AlertType.WARNING, "Attenzione!",
                    "La password deve essere di almeno 8 caratteri, contenere un numero e un carattere speciale (@,#,$,%,^,&,+,=,!)");
            return false;
        }
        return true;
    }


    public static boolean emailValidator(String email) {
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            MyAlert.mostraAlert(Alert.AlertType.WARNING, "Attenzione!", "Inserire indirizzo email valido");
            return false;
        }
        return true;
    }


    public static boolean sessionInvalid(Stage stage){
        if(!AuthSession.getInstance().isLoggedIn()){
            MyAlert.mostraAlert(Alert.AlertType.WARNING, "Sessione scaduta!", "La tua sessione non è attiva. Effettua nuovamente il login");
            WindowHelper.tornaAlLogin(stage);
            return true;
        }
        return false;
    }

    public static void backEndValidator(BackendAction fun) {

        try{
            if(fun != null) fun.execute();
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
            logger.error(e.getMessage(), e);
        } catch (Exception e){
            MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore di connessione.", "Impossibile contattare il server. Verifica che il backend sia in esecuzione.");
            logger.error(e.getMessage(), e);
        }
    }

}
