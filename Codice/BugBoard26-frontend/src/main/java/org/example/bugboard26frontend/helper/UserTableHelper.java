package org.example.bugboard26frontend.helper;

import enums.Ruolo;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AuthUser;


public class UserTableHelper {
    public static void configuraTabella(TableView<AuthUser> tabella){
        tabella.getColumns().clear();

        TableColumn<AuthUser,String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setMinWidth(250);
        emailColumn.setMaxWidth(250);

        TableColumn<AuthUser,Ruolo> ruoloColumn = new TableColumn<>("Ruolo");
        ruoloColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getRuolo()));

        ruoloColumn.setMinWidth(130);
        ruoloColumn.setMaxWidth(130);

        ruoloColumn.setCellFactory(column -> new TableCell<AuthUser, Ruolo>() {
            @Override
            protected void updateItem(Ruolo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {setText(null);}
                else {String ruolo = item.name().replace("_USER","");
                    setText(ruolo);}
            }
        });

        TableColumn<AuthUser, Boolean> statoAccountColumn = new TableColumn<>("Stato");
        statoAccountColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStatoAccount()));

        statoAccountColumn.setMinWidth(90);
        statoAccountColumn.setMaxWidth(90);
        statoAccountColumn.setStyle("-fx-alignment: CENTER;");

        statoAccountColumn.setCellFactory(column -> new TableCell<AuthUser, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }
                else {
                    setText(item ? "Attivo" : "Disattivato");
                }

            }
        });


        TableColumn<AuthUser, Integer> issueAttiveColumn = new TableColumn<>("Issue Attive");
        issueAttiveColumn.setCellValueFactory(new PropertyValueFactory<>("issueAttive"));
        issueAttiveColumn.setMinWidth(90);
        issueAttiveColumn.setMaxWidth(100);
        issueAttiveColumn.setStyle("-fx-alignment: CENTER;");



        TableColumn<AuthUser, Integer> issueRisolteColumn = new TableColumn<>("Issue Risolte");
        issueRisolteColumn.setCellValueFactory(new PropertyValueFactory<>("issueRisolte"));



        issueRisolteColumn.setMinWidth(90);
        issueRisolteColumn.setMaxWidth(100);
        issueRisolteColumn.setStyle("-fx-alignment: CENTER;");






        TableColumn<AuthUser, Double> tempoMedioColumn =  new TableColumn<>("Tempo Medio");
        tempoMedioColumn.setCellValueFactory(new PropertyValueFactory<>("tempoMedio"));

        tempoMedioColumn.setMinWidth(110);
        tempoMedioColumn.setMaxWidth(110);
        tempoMedioColumn.setStyle("-fx-alignment: CENTER;");


        tempoMedioColumn.setCellFactory(column -> new TableCell<AuthUser, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    setText(null);
                else if (item == 0.0)
                    setText("-");
                else {
                    int minutiTot = (int) Math.round(item * 60);
                    int h = minutiTot / 60;
                    int m = minutiTot % 60;

                    if (h == 0)
                        setText(m + "m");
                    else if (m == 0)
                        setText(h + "h");
                    else
                        setText(h + "h " + m + "m");
                }
            }
        });


        tabella.getColumns().addAll(emailColumn, ruoloColumn, statoAccountColumn, issueAttiveColumn, issueRisolteColumn, tempoMedioColumn);
    }
}
