package org.example.bugboard26frontend;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.bugboard26frontend.helper.WindowHelper;

/**
 * Classe principale ed entry point dell'applicazione client JavaFX.
 * Inizializza il toolkit grafico e definisce il flusso di avvio dell'interfaccia utente.
 */
public class FrontEnd_BugBoard extends Application {

    /**
     * Metodo invocato automaticamente dal runtime di JavaFX una volta che il sistema grafico è pronto.
     * Prepara la finestra principale e delega al WindowHelper il caricamento della schermata iniziale (Login).
     *
     * @param stage La finestra principale fornita dal framework JavaFX.
     */
    @Override
    public void start(Stage stage){
        WindowHelper.tornaAlLogin(stage);
        stage.show();
    }

    /**
     * Punto di avvio standard della Java Virtual Machine (JVM).
     * Invoca il metodo interno launch() per far partire l'intero ciclo di vita dell'applicazione JavaFX.
     *
     * @param args Argomenti opzionali passati da riga di comando.
     */
    public static void main(String[] args) {
        launch();
    }
}