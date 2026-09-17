package org.example.bugboard26frontend.helper;

import client.AuthClient;
import client.IssueClient;
import enums.Ruolo;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Issue;
import org.example.bugboard26frontend.controller.AdminHomeController;
import org.example.bugboard26frontend.controller.CreazioneUtenteController;
import org.example.bugboard26frontend.controller.GestioneUtentiController;
import org.example.bugboard26frontend.controller.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class WindowHelper {

    private static final Logger logger = LoggerFactory.getLogger(WindowHelper.class);

    private WindowHelper() {}


    public static void apriSegnalazione(Runnable azioneDopoChiusura){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(WindowHelper.class.getResource("/org/example/bugboard26frontend/segnalazione-issue-view.fxml"));
            Parent root = fxmlLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Segnala Nuova Issue");
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            dialogStage.initModality(Modality.APPLICATION_MODAL);

            dialogStage.showAndWait();
            if(azioneDopoChiusura!=null){
                azioneDopoChiusura.run();
            }
        } catch (Exception e) {
            MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore", "Errore nel caricare la schermata");
            logger.error(e.getMessage());
        }
    }


    public static void apriAllegato(Issue issue, Window mainWindow){

        if(issue != null && issue.getAllegato()!=null) {
            try {
                Stage imgStage = creaStageAllegato(issue, mainWindow);
                imgStage.showAndWait();
            } catch (Exception e){
                MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore", "Errore nel caricare la schermata");
                logger.error(e.getMessage());
            }
        }
    }


    private static Stage creaStageAllegato(Issue issue, Window mainWindow){
            byte[] data = issue.getAllegato().getContenuto();
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            Image image = new Image(bais);
            ImageView imageView = new ImageView(image);

            imageView.setPreserveRatio(true);
            imageView.setFitHeight(1000);
            imageView.setFitWidth(800);

            StackPane layout = new StackPane(imageView);
            layout.setStyle("-fx-background-color: #0b0914; -fx-padding: 20;");

            Stage imgStage = new Stage();
            imgStage.setTitle("Allegato: "+ issue.getAllegato().getNome());
            imgStage.setScene(new Scene(layout));

            if (mainWindow != null)
                imgStage.initOwner(mainWindow);

            return imgStage;
    }


    public static void tornaAlLogin(Stage stageAttuale){
        try{
            FXMLLoader loader = new FXMLLoader(WindowHelper.class.getResource("/org/example/bugboard26frontend/login-view.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setAuthClient(new AuthClient());

            stageAttuale.setScene(new Scene(root));
            stageAttuale.setTitle("BugBoard - Login");
            stageAttuale.setResizable(false);

            stageAttuale.sizeToScene();
            stageAttuale.centerOnScreen();
        } catch (Exception e){
            MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore", "Errore nel caricare la schermata");
            logger.error(e.getMessage());
        }
    }


    public static void apriHome(Stage stage, Ruolo ruolo){
        try{
            String viewToLoad = switch (ruolo) {
                case ADMIN -> "/org/example/bugboard26frontend/admin-home-view.fxml";
                case EXTERNAL_USER -> "/org/example/bugboard26frontend/externalUser-home-view.fxml";
                case INTERNAL_USER -> "/org/example/bugboard26frontend/user-home-view.fxml";
            };

            FXMLLoader loader = new FXMLLoader(WindowHelper.class.getResource(viewToLoad));
            Scene scene = new Scene(loader.load());

            if (ruolo == Ruolo.ADMIN) {
                AdminHomeController controller = loader.getController();
                controller.setIssueClient(new IssueClient());
            }

            stage.setTitle("BugBoard - " + ruolo.name());
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();

        } catch (IOException e){
            MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore", "Errore nel caricare la schermata");
            logger.error(e.getMessage());
        }
    }


    public static void apriCreazioneUtente(Stage stage, Runnable onClose){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(WindowHelper.class.getResource("/org/example/bugboard26frontend/creazione-utente-view.fxml"));
            Parent root = fxmlLoader.load();

            CreazioneUtenteController controller = fxmlLoader.getController();
            controller.setAuthClient(new AuthClient());

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Creazione Utente");
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.setResizable(false);

            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            dialogStage.initOwner(stage);

            dialogStage.showAndWait();

            if(onClose != null) onClose.run();

        } catch (Exception e) {
            MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore", "Errore nel caricare la schermata");
            logger.error(e.getMessage());
        }
    }

    public static void apriGestioneUtenti(Stage stage){
        try{
            FXMLLoader loader = new FXMLLoader(WindowHelper.class.getResource("/org/example/bugboard26frontend/gestione-utenti-view.fxml"));
            Scene scene = new Scene(loader.load());
            GestioneUtentiController controller = loader.getController();

            controller.setAuthClient(new AuthClient());
            controller.setIssueClient(new IssueClient());

            stage.setTitle("Gestione utenti");
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();
        } catch (Exception e) {
            MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore", "Errore nel caricare la schermata");
            logger.error(e.getMessage());
        }
    }


}
