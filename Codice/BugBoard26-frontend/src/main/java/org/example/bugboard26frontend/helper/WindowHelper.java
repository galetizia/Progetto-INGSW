package org.example.bugboard26frontend.helper;

import enums.Ruolo;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Issue;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class WindowHelper {
    private static final MyAlert alert =  new MyAlert();

    public static void apriSegnalazione(Runnable azioneDopoChiusura){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(WindowHelper.class.getResource("/org/example/bugboard26frontend/segnalazione-issue-view.fxml"));
            Parent root = fxmlLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Segnala Nuova Issue");
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            // per bloccare le finestre sottostanti
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            dialogStage.showAndWait();
            if(azioneDopoChiusura!=null){
                azioneDopoChiusura.run();
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore nell'apertura finestra segnalazione");
        }
    }

    public static void apriAllegato(Issue issue, Window mainWindow){

        if(issue != null && issue.getAllegato()!=null) {
            try {
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
                imgStage.setTitle("Allegato: "+issue.getAllegato().getNome());
                imgStage.setScene(new Scene(layout));

                if (mainWindow != null)
                    imgStage.initOwner(mainWindow);

                imgStage.showAndWait();

            } catch (Exception e){
                System.out.println("Errore nell'apertura allegato" + e.getMessage());
            }
        }
    }

    public static void tornaAlLogin(Stage stageAttuale){
        try{
            FXMLLoader loader = new FXMLLoader(WindowHelper.class.getResource("/org/example/bugboard26frontend/login-view.fxml"));
            Parent root = loader.load();

            stageAttuale.setScene(new Scene(root));
            stageAttuale.setTitle("BugBoard - Login");
            stageAttuale.setResizable(false);

            stageAttuale.sizeToScene();
            stageAttuale.centerOnScreen();
        } catch (IOException e){
            e.printStackTrace();
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

            stage.setTitle("BugBoard - " + ruolo.name());
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();

        } catch (IOException e){
            alert.mostraAlert(Alert.AlertType.ERROR, "Errore", "Errore nel caricare la schermata");
            e.printStackTrace();
        }
    }


}
