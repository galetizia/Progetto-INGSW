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
import java.util.List;

public class IssueTableHelper {

    private IssueTableHelper() {}

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void configuraTabella(TableView<Issue> tabella, Ruolo ruolo, boolean isArchiviati){
        tabella.getColumns().clear();
        tabella.setPrefHeight(321);
        tabella.setPrefWidth(670);

        TableColumn<Issue, Integer> idColumn = createColumnSemplice("ID", "id", 50, 50);
        TableColumn<Issue, String> titoloColumn = createColumnSemplice("Titolo", "titolo", 150, -1);
        TableColumn<Issue, String> tipoColumn = createColumnSemplice("Tipo", "tipo", 140, -1);

        TableColumn<Issue, String> prioritaColumn = createColumnPriorita();
        TableColumn<Issue, String> statoColumn = creaColonnaStato(ruolo, isArchiviati);
        TableColumn<Issue, LocalDateTime> dataColumn = creaColonnaData(isArchiviati);

        tabella.getColumns().addAll(List.of(
                idColumn, titoloColumn, prioritaColumn, statoColumn, tipoColumn, dataColumn
        ));
    }


    private static <T> TableColumn<Issue, T> createColumnSemplice(String titolo, String property, double minWidth, double maxWidth) {
        TableColumn<Issue, T> column = new TableColumn<>(titolo);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setMinWidth(minWidth);
        if (maxWidth > 0) column.setMaxWidth(maxWidth);
        return column;
    }


    private static TableColumn<Issue, String> createColumnPriorita(){
        TableColumn<Issue, String> prioritaColumn = createColumnSemplice("Priorita", "priorita", 80, -1);
        prioritaColumn.setStyle("-fx-alignment: CENTER;");

        prioritaColumn.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.equalsIgnoreCase("no") ? "-" : item);
                }
            }
        });
        return prioritaColumn;
    }


    private static TableColumn<Issue, LocalDateTime> creaColonnaData(boolean isArchiviati) {
        String titolo = isArchiviati ? "Data Risoluzione" : "Data";
        String property = isArchiviati ? "dataRisoluzione" : "data";

        TableColumn<Issue, LocalDateTime> colonna = createColumnSemplice(titolo, property, 140, -1);
        colonna.setStyle("-fx-alignment: CENTER;");

        colonna.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    return;
                }

                Issue issue = getTableRow().getItem();
                setText(isArchivioData(issue, date, isArchiviati));
            }
        });
        return colonna;
    }

    private static String isArchivioData(Issue issue, LocalDateTime date, boolean isArchiviati) {
        if (!isArchiviati) {
            return date != null ? formatter.format(date) : "";
        }
        boolean isRisolto = "RISOLTO".equalsIgnoreCase(issue.getStato());
        return (isRisolto && date!=null) ? formatter.format(date) : "-";
    }


    private static TableColumn<Issue, String> creaColonnaStato(Ruolo ruolo, boolean isArchiviati) {
        TableColumn<Issue, String> colonna = createColumnSemplice("Stato", "stato", 100, -1);

        colonna.setCellFactory(_ -> new TableCell<>() {
            @Override
            protected void updateItem(String stato, boolean empty) {
                super.updateItem(stato, empty);
                if (empty || stato == null || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Issue issue = getTableRow().getItem();
                    setText(ottieniTestoStato(stato, issue, ruolo, isArchiviati));
                }
            }
        });
        return colonna;
    }


    private static String ottieniTestoStato(String stato, Issue issue, Ruolo ruolo, boolean isArchiviati) {
        String statoUpper = stato.toUpperCase();

        if (isArchiviati) {
            return switch (statoUpper) {
                case "RISOLTO" -> "✅ RISOLTO";
                case "ARCHIVIATO" -> "📦 ARCHIVIATO";
                default -> stato;
            };
        }

        return switch (statoUpper) {
            case "TO_DO" -> "🟢 TO-DO";
            case "ASSEGNATO" -> {
                if (ruolo == Ruolo.INTERNAL_USER && issue.getAssignee() != null
                        && issue.getAssignee().getId() == AuthSession.getInstance().getUtenteCorrente().getId()) {
                    yield "👤 IN LAVORAZIONE";
                }
                yield "🔒 ASSEGNATO";
            }
            case "RISOLTO" -> (ruolo == Ruolo.EXTERNAL_USER) ? "✅ RISOLTO" : stato;
            default -> stato;
        };
    }
}