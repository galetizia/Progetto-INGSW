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

/**
 * Classe Helper responsabile della configurazione e formattazione visiva della tabella delle issue.
 * Gestisce la creazione delle colonne, il binding dei dati e la personalizzazione grafica delle celle (es. formattazione date, inserimento di emoji negli stati).
 */
public class IssueTableHelper {

    private IssueTableHelper() {}

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Pulisce e configura la struttura della tabella JavaFX, instanziando e dimensionando dinamicamente
     * le colonne in base alla schermata corrente ed ai permessi dell'utente.
     *
     * @param tabella      L'oggetto TableView grafico da configurare.
     * @param ruolo        Il ruolo dell'utente loggato, necessario per personalizzare la visibilità di alcuni stati.
     * @param isArchiviati Booleano che indica se la tabella sta mostrando l'archivio (true) o le issue attive (false).
     */
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

    /**
     * Metodo di supporto per snellire la creazione di colonne standard.
     * Collega automaticamente la colonna alla proprietà corrispondente nel modello (Issue).
     *
     * @param titolo   L'intestazione testuale della colonna.
     * @param property Il nome esatto dell'attributo nella classe Issue (es. "titolo").
     * @param minWidth La larghezza minima garantita della colonna.
     * @param maxWidth La larghezza massima consentita (se > 0).
     * @param <T>      Il tipo di dato contenuto nella colonna (es. String, Integer).
     * @return La colonna configurata e pronta da aggiungere alla tabella.
     */
    private static <T> TableColumn<Issue, T> createColumnSemplice(String titolo, String property, double minWidth, double maxWidth) {
        TableColumn<Issue, T> column = new TableColumn<>(titolo);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setMinWidth(minWidth);
        if (maxWidth > 0) column.setMaxWidth(maxWidth);
        return column;
    }

    /**
     * Crea e formatta la colonna "Priorità".
     * Nasconde il valore di sistema "no" dietro a un trattino visivo ("-") per una UI più pulita.
     *
     * @return La colonna formattata per la priorità.
     */
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

    /**
     * Crea la colonna per le date, adattandosi dinamicamente al contesto.
     * Se siamo nell'archivio mostra la Data Risoluzione, altrimenti la Data di creazione.
     *
     * @param isArchiviati Indica se la tabella è in modalità archivio.
     * @return La colonna per le date configurata.
     */
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

    /**
     * Logica di estrazione e formattazione della data.
     * Converte l'oggetto LocalDateTime in una stringa leggibile (dd/MM/yyyy HH:mm).
     * Nel caso dell'archivio, gestisce eventuali anomalie in cui una issue non ha una data di risoluzione valida.
     *
     * @param issue        La issue in analisi.
     * @param date         Il valore della data da parsare.
     * @param isArchiviati Indica il contesto di visualizzazione.
     * @return La stringa formattata da mostrare nella cella.
     */

    private static String isArchivioData(Issue issue, LocalDateTime date, boolean isArchiviati) {
        if (!isArchiviati) {
            return date != null ? formatter.format(date) : "";
        }
        boolean isRisolto = "RISOLTO".equalsIgnoreCase(issue.getStato());
        return (isRisolto && date!=null) ? formatter.format(date) : "-";
    }

    /**
     * Crea la colonna che mostra lo stato di avanzamento della issue, applicando
     * trasformazioni grafiche tramite una CellFactory personalizzata.
     *
     * @param ruolo        Il ruolo dell'utente loggato.
     * @param isArchiviati Indica se la tabella archivio è attiva o meno.
     * @return La colonna configurata per mostrare lo stato.
     */
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

    /**
     * Valuta lo stato grezzo della issue e lo converte in un'etichetta user-friendly.
     * Aggiunge icone visive per un'immediata comprensione. Se una issue è assegnata all'utente che sta
     * guardando lo schermo, le cambia l'etichetta in "👤 IN LAVORAZIONE".
     *
     * @param stato        Lo stato salvato a database.
     * @param issue        L'oggetto issue completo per verifiche incrociate (es. l'assegnatario).
     * @param ruolo        Il ruolo dell'utente.
     * @param isArchiviati Il flag che indica la vista corrente.
     * @return La stringa finale (arricchita di emoji) da mostrare nella UI.
     */
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