package org.example.bugboard26frontend.helper;

import client.AuthSession;
import enums.Ruolo;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import model.AuthUser;
import model.Issue;

import java.util.Comparator;
import java.util.List;

public class FiltroEOrdinaHelper {

    private FiltroEOrdinaHelper() {}
    public static void configuraFiltroEOrdine(TableView<Issue> issueTable,
                                              ObservableList<Issue> masterData,
                                              ChoiceBox<String> filtroChoiceBox,
                                              ChoiceBox<String> ordinaChoiceBox,
                                              Ruolo ruolo) {

        FilteredList<Issue> filteredData = new FilteredList<>(masterData, _ -> true);

        SortedList<Issue> sortedData = new SortedList<>(filteredData);

        issueTable.setItems(sortedData);

        filtroChoiceBox.getItems().addAll("Tutte", "To-do", "Bug", "Feature", "Documentation", "Question");
        filtroChoiceBox.setValue("Tutte");

        if(ruolo == Ruolo.INTERNAL_USER)
            filtroChoiceBox.getItems().add("Le mie issue");
        else if (ruolo == Ruolo.ADMIN || ruolo == Ruolo.EXTERNAL_USER)
            filtroChoiceBox.getItems().add("Assegnate");

        if (ruolo == Ruolo.EXTERNAL_USER)
            filtroChoiceBox.getItems().add("Risolte");

        ordinaChoiceBox.getItems().addAll("Nessun ordine", "Priorità Alta", "Priorità Bassa", "Più recenti");
        ordinaChoiceBox.setValue("Nessun ordine");

        filtroChoiceBox.getSelectionModel().selectedItemProperty().addListener((_, _, _) ->
                applicaFiltroEOrdine(filteredData, sortedData, filtroChoiceBox, ordinaChoiceBox));


        ordinaChoiceBox.getSelectionModel().selectedItemProperty().addListener((_, _, _) ->
                applicaFiltroEOrdine(filteredData, sortedData, filtroChoiceBox, ordinaChoiceBox));

    }

    private static void applicaFiltroEOrdine(FilteredList<Issue> filteredData,
                                             SortedList<Issue> sortedData,
                                             ChoiceBox<String> filtroChoiceBox,
                                             ChoiceBox<String> ordinaChoiceBox){

        if(filteredData == null || sortedData == null) return;

        String filtro = filtroChoiceBox.getValue();
        String ordina = ordinaChoiceBox.getValue();

        filter(filtro, filteredData);
        sort(ordina, sortedData);

    }

    private static void sort(String ordina, SortedList<Issue> sortedData) {
        switch (ordina) {
            case "Priorità Alta" -> {
                List<String> ordine = List.of("ALTA", "MEDIA", "BASSA", "NO");
                sortedData.setComparator(Comparator.comparingInt(issue -> {
                    String priorita = String.valueOf(issue.getPriorita()).toUpperCase();
                    int posizione = ordine.indexOf(priorita);
                    return posizione == -1 ? Integer.MAX_VALUE : posizione;
                }));
            }
            case "Priorità Bassa" -> {
                List<String> ordine = List.of("BASSA", "MEDIA", "ALTA", "NO");
                sortedData.setComparator(Comparator.comparingInt(issue -> {
                    String priorita = String.valueOf(issue.getPriorita()).toUpperCase();
                    int posizione = ordine.indexOf(priorita);
                    return posizione == -1 ? Integer.MAX_VALUE : posizione;
                }));

            }
            case "Più recenti" -> sortedData.setComparator(
                    Comparator.comparing(Issue::getData, Comparator.nullsLast(Comparator.naturalOrder()))
                            .reversed());
            case null, default -> sortedData.setComparator(null);
        }

    }

    private static void filter(String filtro, FilteredList<Issue> filteredData){
        filteredData.setPredicate(issue -> {
            if (filtro == null) return true;

            return switch (filtro) {
                case "To-do" -> "TO_DO".equalsIgnoreCase(issue.getStato());
                case "Bug" -> "BUG".equalsIgnoreCase(issue.getTipo().name());
                case "Feature" -> "FEATURE".equalsIgnoreCase(issue.getTipo().name());
                case "Documentation" -> "DOCUMENTATION".equalsIgnoreCase(issue.getTipo().name());
                case "Question" -> "QUESTION".equalsIgnoreCase(issue.getTipo().name());
                case "Le mie issue" -> "ASSEGNATO".equalsIgnoreCase(issue.getStato())
                        && issue.getAssignee() != null
                        && issue.getAssignee().getId() == AuthSession.getInstance().getUtenteCorrente().getId();
                case "Assegnate" -> "ASSEGNATO".equalsIgnoreCase(issue.getStato());
                case "Risolte" -> "RISOLTO".equalsIgnoreCase(issue.getStato());
                default -> true;
            };
        });
    }

    public static void configuraFiltroUtenti(TableView<AuthUser> userTable,
                                             ObservableList<AuthUser> masterData,
                                             ChoiceBox<String> filtroChoiceBox){


        FilteredList<AuthUser> filteredData = new FilteredList<>(masterData, _ -> true);
        userTable.setItems(filteredData);

        filtroChoiceBox.getItems().clear();
        filtroChoiceBox.getItems().addAll("Tutti", "Attivi", "Non Attivi");
        filtroChoiceBox.setValue("Tutti");

        filtroChoiceBox.getSelectionModel().selectedItemProperty().addListener((_, _, _) ->
            applicaFiltroUtenti(filteredData, filtroChoiceBox));
    }

    private static void applicaFiltroUtenti(FilteredList<AuthUser> filteredData, ChoiceBox<String> filtroChoiceBox){
        if(filteredData == null) return;

        String filtro = filtroChoiceBox.getValue();

        filteredData.setPredicate(user -> {
            if ("Attivi".equals(filtro)) return user.getStatoAccount();
            if("Non Attivi".equals(filtro)) return !user.getStatoAccount();
            return true;
        });

    }
}
