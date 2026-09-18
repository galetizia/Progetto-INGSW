package org.example.bugboard26frontend.helper;

import enums.Ruolo;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.AuthUser;

import java.util.List;


/**
 * Classe Helper responsabile della configurazione e formattazione visiva della tabella degli utenti.
 * Gestisce la creazione delle colonne, il binding dei dati anagrafici e statistici, e la personalizzazione dell'aspetto delle celle.
 */
public class UserTableHelper {

    private UserTableHelper() {}

    /**
     * Inizializza e compone le colonne della tabella utenti, ripulendo eventuali configurazioni precedenti.
     * Definisce le larghezze, l'ordine di visualizzazione dei dati ed inserisce le formattazioni personalizzate.
     *
     * @param tabella L'oggetto TableView grafico da configurare.
     */
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

    /**
     * Metodo di supporto per la creazione rapida di una colonna standard testuale, ancorata all'attributo del modello.
     *
     * @param titolo   L'intestazione testuale della colonna.
     * @param property Il nome esatto dell'attributo nella classe AuthUser.
     * @param width    La larghezza fissa della colonna.
     * @param <T>      Il tipo di dato contenuto nella colonna.
     * @return La colonna configurata.
     */
    private static <T> TableColumn<AuthUser, T> creaColumnSemplice(String titolo, String property, double width) {
        TableColumn<AuthUser, T> column = new TableColumn<>(titolo);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setMinWidth(width);
        column.setMaxWidth(width);
        return column;
    }

    /**
     * Crea una colonna standard e ne centra il contenuto testuale.
     *
     * @param titolo   L'intestazione testuale della colonna.
     * @param property Il nome esatto dell'attributo nella classe AuthUser.
     * @param width    La larghezza fissa della colonna.
     * @param <T>      Il tipo di dato contenuto nella colonna.
     * @return La colonna configurata con allineamento centrale.
     */
    private static <T> TableColumn<AuthUser, T> creaColumnCentrata(String titolo, String property, double width) {
        TableColumn<AuthUser, T> colonna = creaColumnSemplice(titolo, property, width);
        colonna.setStyle("-fx-alignment: CENTER;");
        return colonna;
    }

    /**
     * Crea e formatta la colonna dedicata al Ruolo dell'utente.
     * Rimuove il suffisso "_USER" (es. INTERNAL_USER diventa INTERNAL) per una maggiore pulizia dell'interfaccia.
     *
     * @return La colonna formattata per la visualizzazione dei ruoli.
     */
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

    /**
     * Crea e formatta la colonna per lo stato dell'account.
     * Traduce il valore booleano di base in un testo leggibile per l'utente ("Attivo" se true, "Disattivato" se false).
     *
     * @return La colonna formattata per lo stato dell'account.
     */
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

    /**
     * Crea e formatta la colonna del tempo medio di risoluzione.
     * Converte il valore numerico decimale (ore) nel formato testuale standard "Xh Ym".
     * Mostra un trattino ("-") se il valore è zero (nessuna statistica disponibile).
     *
     * @return La colonna formattata per i tempi medi.
     */
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
