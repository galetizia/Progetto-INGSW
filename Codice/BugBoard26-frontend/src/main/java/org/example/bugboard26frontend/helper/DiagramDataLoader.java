package org.example.bugboard26frontend.helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;

import java.util.Map;

public class DiagramDataLoader {

    private DiagramDataLoader(){}

    public static ObservableList<PieChart.Data> configuraDiagrammaTipoIssueAttive(Map<String, Integer> issuesType) {

        int countBug = issuesType.getOrDefault("BUG", 0);
        int countFeature = issuesType.getOrDefault("FEATURE", 0);
        int countQuestion = issuesType.getOrDefault("QUESTION", 0);
        int countDocumentation = issuesType.getOrDefault("DOCUMENTATION", 0);

        ObservableList<PieChart.Data> issueTypes = FXCollections.observableArrayList();

        if(countBug > 0) issueTypes.add(new PieChart.Data("Bug (" + countBug + ")", countBug));
        if(countFeature > 0) issueTypes.add(new PieChart.Data("Feature (" + countFeature + ")", countFeature));
        if(countDocumentation > 0) issueTypes.add(new PieChart.Data("Documentation (" + countDocumentation + ")", countDocumentation));
        if(countQuestion > 0) issueTypes.add(new PieChart.Data("Question (" + countQuestion + ")", countQuestion));

        return issueTypes;
    }

    public static ObservableList<PieChart.Data> configuraDiagrammaStatoIssueAttive(Map<String, Integer> dataStates) {
        int countToDo = dataStates.getOrDefault("TO_DO", 0);
        int countAssegnati = dataStates.getOrDefault("ASSEGNATO", 0);

        ObservableList<PieChart.Data> issueStates = FXCollections.observableArrayList();

        if(countToDo > 0) issueStates.add(new PieChart.Data("To-Do (" + countToDo + ")", countToDo));
        if(countAssegnati > 0) issueStates.add(new PieChart.Data("Assegnati (" + countAssegnati + ")", countAssegnati));

        return issueStates;
    }


    public static XYChart.Series<String, Number> preparaDatiIssueAssegnate(Map<String, Integer> issuesPerUser) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        issuesPerUser.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> {
                    String email = entry.getKey();
                    int count = entry.getValue();
                    String rawName = email.split("@")[0];

                    String username = (rawName.length()>10) ? rawName.substring(0,10) + "..." : rawName;

                    XYChart.Data<String, Number> data = new XYChart.Data<>(username, count);

                    data.nodeProperty().addListener((_, _, newNode) -> {
                        if (newNode != null) {
                            javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(
                                    "Utente: " + email + "\nBug assegnati: " + count
                            );
                            tooltip.setShowDelay(javafx.util.Duration.millis(100));
                            javafx.scene.control.Tooltip.install(newNode, tooltip);
                        }
                    });
                    series.getData().add(data);
                });
        return series;
    }

    public static void configuraDiagrammaIssueAssegnate(XYChart<String, Number> chart) {
        chart.setTitle("Utenti con più Issue assegnate");
        chart.setLegendVisible(false);

        CategoryAxis xAxis = (CategoryAxis) chart.getXAxis();
        xAxis.setTickLabelRotation(315);

        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        yAxis.setMinorTickVisible(false);

        yAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number object) {

                if (object.doubleValue() % 1 == 0) {
                    return String.valueOf(object.intValue());
                } else {
                    return "";
                }
            }
            @Override
            public Number fromString(String string) {
                return null;
            }
        });

    }


    public static XYChart.Series<String, Number> preparaDatiTempoMedio(Map<String, Double> dataTimes) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        dataTimes.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.comparingByValue())
                .limit(10)
                .forEach(entry -> {
                    Double tempo = entry.getValue();
                    String email = entry.getKey();
                    double tempoArrotondato = Math.round(tempo * 10.0) / 10.0;

                    int minTot = (int) Math.round(tempoArrotondato * 60);
                    String tempoStringa = (minTot / 60 > 0 ? (minTot / 60) + "h " : "") + (minTot % 60) + "m";

                    String rawName = email.split("@")[0];
                    String username = (rawName.length() > 10) ? rawName.substring(0, 10) + "..." : rawName;

                    XYChart.Data<String, Number> data = new XYChart.Data<>(username, tempoArrotondato);
                    data.nodeProperty().addListener((_, _, newNode) -> {
                        if (newNode != null) {
                            javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(
                                    "Utente: " + email + "\nTempo medio: " + tempoStringa
                            );
                            tooltip.setShowDelay(javafx.util.Duration.millis(100));
                            javafx.scene.control.Tooltip.install(newNode, tooltip);
                        }
                    });
                    series.getData().add(data);
                });
        return series;
    }


    public static String calcoloTempoMedio(Map<String, Double> dataTimes) {
        double oreTot = 0.0;
        int utentiValidi = 0;

        for (Double tempo : dataTimes.values()) {
            if (tempo != null && tempo > 0) {
                oreTot += tempo;
                utentiValidi++;
            }
        }
        if(utentiValidi == 0) return "0m";

        double media = oreTot / utentiValidi;
        int minutiTot = (int) Math.round(media * 60);
        int hMedia = minutiTot /60;
        int mMedia = minutiTot % 60;

        if(hMedia == 0) return mMedia + "m";
        else if(mMedia == 0) return hMedia + "h";

        return hMedia + "h" + mMedia + "m";
    }

    public static void configuraDiagrammaTempoMedio(XYChart<String, Number> chart, String textMedia) {
        chart.setTitle("Media Globale: " + textMedia +" | Top 10 utenti più veloci");
        chart.setLegendVisible(false);

        CategoryAxis xAxis = (CategoryAxis) chart.getXAxis();
        xAxis.setTickLabelRotation(315);

        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        yAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number object) {
                int minutiTotali = (int) Math.round(object.doubleValue() * 60);
                int h = minutiTotali / 60;
                int m = minutiTotali % 60;

                if (h == 0) {
                    return m + "m";
                } else if (m == 0) {
                    return h + "h";
                } else {
                    return h + "h " + m + "m";
                }
            }
            @Override
            public Number fromString(String string){
                return null;
            }
        });
    }
}
