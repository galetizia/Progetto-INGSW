package org.example.bugboard26frontend.helper;

import client.AuthSession;
import enums.Ruolo;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Issue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IssueTableHelper {
    public static void configuraTabella(TableView<Issue> tabella, Ruolo ruolo, boolean isArchiviati){
        tabella.getColumns().clear();
        tabella.setPrefHeight(321);
        tabella.setPrefWidth(670);

        TableColumn<Issue, Integer> idColumn = new TableColumn<>("ID"); //si
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setMinWidth(50);
        idColumn.setMaxWidth(50);

        TableColumn<Issue, String> titoloColumn = new TableColumn<>("Titolo"); //si
        titoloColumn.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        titoloColumn.setMinWidth(150);

        TableColumn<Issue, String> prioritaColumn = new TableColumn<>("Priorita"); //si
        prioritaColumn.setCellValueFactory(new PropertyValueFactory<>("priorita"));
        prioritaColumn.setMinWidth(80);
        prioritaColumn.setCellFactory(column -> new TableCell<Issue, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    if(item.equalsIgnoreCase("no"))
                        setText("-");
                    else
                        setText(item);
                }
                setStyle("-fx-alignment: CENTER;");
            }
        });

        TableColumn<Issue, String> statoColumn = new TableColumn<>("Stato"); //si
        statoColumn.setCellValueFactory(new PropertyValueFactory<>("stato"));
        statoColumn.setMinWidth(100);




        TableColumn<Issue, String> tipoColumn = new TableColumn<>("Tipo"); //si
        tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        tipoColumn.setMinWidth(140);





        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        TableColumn<Issue, LocalDateTime> dataColumn;

        if(isArchiviati){
            dataColumn = new TableColumn<>("Data Risoluzione");
            dataColumn.setCellValueFactory(new PropertyValueFactory<>("dataRisoluzione"));
        } else {
            dataColumn = new TableColumn<>("Data");
            dataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));

        }


        dataColumn.setCellFactory(column -> new TableCell<Issue, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || date == null) {
                    setText(null); // Se la riga è vuota, non scrive nulla
                } else {
                    Issue issue = getTableRow().getItem();
                    if(issue != null){
                        if(isArchiviati){
                            if(issue.getStato() != null && issue.getStato().toString().equalsIgnoreCase("RISOLTO"))
                                setText(formatter.format(date));
                            else
                                setText("-");
                        } else {
                            setText(formatter.format(date));
                        }
                    } else {
                        setText(null);
                    }
                }
                dataColumn.setStyle("-fx-alignment: CENTER;");
                dataColumn.setMinWidth(140);
            }
        });



        if(ruolo == Ruolo.INTERNAL_USER && !isArchiviati){
            statoColumn.setCellFactory(column -> new TableCell<Issue, String>() {
                @Override
                protected void updateItem(String stato, boolean empty) {
                    super.updateItem(stato, empty);

                    if (empty || stato == null || getTableRow() == null || getTableRow().getItem() == null) {
                        setText(null);
                        return;
                    }

                    Issue issueCorrente = getTableRow().getItem();

                    if ("TO_DO".equalsIgnoreCase(stato)) {
                        setText("🟢 TO_DO");
                    } else if ("ASSEGNATO".equalsIgnoreCase(stato)) {

                        if (issueCorrente.getAssignee() != null
                                && issueCorrente.getAssignee().getId() == AuthSession.getInstance().getUtenteCorrente().getId()) {
                            setText("👤 IN LAVORAZIONE");
                        } else {
                            setText("🔒 ASSEGNATO");
                        }

                    } else {
                        setText(stato);
                    }
                }
            });

        } else if (ruolo == Ruolo.ADMIN && !isArchiviati){
            statoColumn.setCellFactory(column -> new TableCell<Issue, String>() {
                @Override
                protected void updateItem(String stato, boolean empty) {
                    super.updateItem(stato, empty);

                    if (empty || stato == null) {
                        setText(null);
                    } else {
                        if ("TO_DO".equalsIgnoreCase(stato)) {
                            setText("🟢 TO-DO");
                        } else if ("ASSEGNATO".equalsIgnoreCase(stato)) {
                            setText("🔒 ASSEGNATO");
                        } else {
                            setText(stato);
                        }
                    }
                }
            });
        } else if (ruolo == Ruolo.EXTERNAL_USER && !isArchiviati){
            statoColumn.setCellFactory(column -> new TableCell<Issue, String>() {
                @Override
                protected void updateItem(String stato, boolean empty) {
                    super.updateItem(stato, empty);

                    if (empty || stato == null || getTableRow() == null || getTableRow().getItem() == null) {
                        setText(null);
                        return;
                    }

                    if ("TO_DO".equalsIgnoreCase(stato)) {
                        setText("🟢 TO-DO");
                    } else if ("ASSEGNATO".equalsIgnoreCase(stato)) {
                        setText("🔒 ASSEGNATO");
                    } else if ("RISOLTO".equalsIgnoreCase(stato)) {
                        setText("✅ RISOLTO");
                    } else {
                        setText(stato);
                    }
                }
            });
        } else if(isArchiviati){
            statoColumn.setCellFactory(column -> new TableCell<Issue, String>() {
                @Override
                protected void updateItem(String stato, boolean empty) {
                    super.updateItem(stato, empty);
                    if (empty || stato == null) {
                        setText(null);
                    } else {
                        if ("RISOLTO".equalsIgnoreCase(stato)) {
                            setText("✅ RISOLTO");
                        } else if ("ARCHIVIATO".equalsIgnoreCase(stato)) {
                            setText("📦 ARCHIVIATO");
                        } else {
                            setText(stato);
                        }
                    }
                }
            });

        }
        tabella.getColumns().addAll(idColumn, titoloColumn, prioritaColumn ,statoColumn, tipoColumn, dataColumn);

    }
}
