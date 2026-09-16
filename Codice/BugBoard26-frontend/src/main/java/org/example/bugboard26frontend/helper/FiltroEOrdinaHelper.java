package org.example.bugboard26frontend.helper;

import client.AuthSession;
import enums.Ruolo;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import model.Issue;

import java.util.Comparator;
import java.util.List;

public class FiltroEOrdinaHelper {
    public static void configuraFiltroEOrdine(TableView<Issue> issueTable,
                                              ObservableList<Issue> masterData,
                                              ChoiceBox<String> filtroChoiceBox,
                                              ChoiceBox<String> ordinaChoiceBox,
                                              Ruolo ruolo) {

        FilteredList<Issue> filteredData = new FilteredList<>(masterData, p -> true);

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

        filteredData.setPredicate(issue -> {
            if ("To-do".equals(filtro)) return "TO_DO".equalsIgnoreCase(issue.getStato());
            if ("Bug".equals(filtro)) return "BUG".equalsIgnoreCase(issue.getTipo().name());
            if ("Feature".equals(filtro)) return "FEATURE".equalsIgnoreCase(issue.getTipo().name());
            if ("Documentation".equals(filtro)) return "DOCUMENTATION".equalsIgnoreCase(issue.getTipo().name());
            if ("Question".equals(filtro)) return "QUESTION".equalsIgnoreCase(issue.getTipo().name());
            if ("Le mie issue".equals(filtro)) {
                return "ASSEGNATO".equalsIgnoreCase(issue.getStato())
                        && issue.getAssignee() != null
                        && issue.getAssignee().getId() == AuthSession.getInstance().getUtenteCorrente().getId();
            }
            if ("Assegnate".equals(filtro)) return "ASSEGNATO".equalsIgnoreCase(issue.getStato());
            if ("Risolte".equals(filtro)) return "RISOLTO".equalsIgnoreCase(issue.getStato());
            return true;
        });

        if("Priorità Alta".equals(ordina)){
            List<String> ordine = List.of("ALTA", "MEDIA", "BASSA", "NO");
            sortedData.setComparator(Comparator.comparingInt(issue -> {
                String priorita = String.valueOf(issue.getPriorita()).toUpperCase();
                int posizione = ordine.indexOf(priorita);
                return posizione == -1 ? Integer.MAX_VALUE : posizione;
            }));
        } else if ("Priorità Bassa".equals(ordina)) {
            List<String> ordine = List.of("BASSA", "MEDIA", "ALTA", "NO");
            sortedData.setComparator(Comparator.comparingInt(issue -> {
                String priorita = String.valueOf(issue.getPriorita()).toUpperCase();
                int posizione = ordine.indexOf(priorita);
                return posizione == -1 ? Integer.MAX_VALUE : posizione;
            }));

        } else if("Più recenti".equals(ordina)){
            sortedData.setComparator(
                    Comparator.comparing(Issue::getData, Comparator.nullsLast(Comparator.naturalOrder()))
                            .reversed());
        } else{
            sortedData.setComparator(null);
        }
    }
}
