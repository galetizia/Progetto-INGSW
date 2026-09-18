package org.example.bugboard26frontend.helper;

import enums.Ruolo;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AuthUser;

import java.util.List;


public class UserTableHelper {

    private UserTableHelper() {}

    public static void configuraTabella(TableView<AuthUser> tabella){
        tabella.getColumns().clear();

        TableColumn<AuthUser,String> emailColumn = creaColumnSemplice("Email", "email", 250);
        TableColumn<AuthUser,Ruolo> ruoloColumn = creaColumnRuolo();
        TableColumn<AuthUser, Boolean> statoAccountColumn = creaColumnStato();
        TableColumn<AuthUser, Integer> issueAttiveColumn = creaColumnCentrata("Issue Attive", "issueAttive", 95);
        TableColumn<AuthUser, Integer> issueRisolteColumn = creaColumnCentrata("Issue Risolte", "issueRisolte", 95);
        TableColumn<AuthUser, Double> tempoMedioColumn = creaColumnTempoMedio();

        tabella.getColumns().addAll(List.of(emailColumn, ruoloColumn, statoAccountColumn, issueAttiveColumn, issueRisolteColumn, tempoMedioColumn));
    }

    private static <T> TableColumn<AuthUser, T> creaColumnSemplice(String titolo, String property, double width) {
        TableColumn<AuthUser, T> column = new TableColumn<>(titolo);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setMinWidth(width);
        column.setMaxWidth(width);
        return column;
    }


    private static <T> TableColumn<AuthUser, T> creaColumnCentrata(String titolo, String property, double width) {
        TableColumn<AuthUser, T> colonna = creaColumnSemplice(titolo, property, width);
        colonna.setStyle("-fx-alignment: CENTER;");
        return colonna;
    }


    private static TableColumn<AuthUser, Ruolo> creaColumnRuolo() {
        TableColumn<AuthUser, Ruolo> colonna = new TableColumn<>("Ruolo");
        colonna.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRuolo()));
        colonna.setMinWidth(130);
        colonna.setMaxWidth(130);

        colonna.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Ruolo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.name().replace("_USER", ""));
                }
            }
        });
        return colonna;
    }


    private static TableColumn<AuthUser, Boolean> creaColumnStato() {
        TableColumn<AuthUser, Boolean> colonna = new TableColumn<>("Stato");
        colonna.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatoAccount()));
        colonna.setMinWidth(90);
        colonna.setMaxWidth(90);
        colonna.setStyle("-fx-alignment: CENTER;");

        colonna.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    boolean isActive = item;
                    setText(isActive ? "Attivo" : "Disattivato");
                }
            }
        });
        return colonna;
    }


    private static TableColumn<AuthUser, Double> creaColumnTempoMedio() {
        TableColumn<AuthUser, Double> colonna = creaColumnCentrata("Tempo Medio", "tempoMedio", 110);

        colonna.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else if (item == 0.0) {
                    setText("-");
                } else {
                    int minutiTot = (int) Math.round(item * 60);
                    int h = minutiTot / 60;
                    int m = minutiTot % 60;

                    if (h == 0) {
                        setText(m + "m");
                    } else if (m == 0) {
                        setText(h + "h");
                    } else {
                        setText(h + "h " + m + "m");
                    }
                }
            }
        });
        return colonna;
    }

}
