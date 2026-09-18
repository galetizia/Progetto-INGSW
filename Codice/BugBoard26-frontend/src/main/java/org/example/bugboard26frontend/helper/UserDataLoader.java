package org.example.bugboard26frontend.helper;

import client.AuthClient;
import javafx.collections.ObservableList;
import model.AuthUser;

import java.util.List;
import java.util.Map;

/**
 * Classe Helper incaricata di recuperare l'anagrafica degli utenti dal backend
 * e di arricchirla incrociandola con le statistiche operative (issue attive, issue risolte e tempo medio).
 */
public class UserDataLoader {
    private static final AuthClient authClient = new AuthClient();

    private UserDataLoader(){}

    /**
     * Esegue chiamate di rete per ottenere la lista base degli utenti e le varie mappe statistiche.
     * Successivamente, itera su ogni utente per iniettare al suo interno le statistiche calcolate dal backend,
     * aggiornando infine la lista osservabile per il rendering nella tabella JavaFX.
     *
     * @param masterData La lista osservabile (collegata alla UI) da popolare con gli oggetti utente completi di statistiche.
     */
    public static void loadUserData(ObservableList<AuthUser> masterData) {
        List<AuthUser> users = authClient.getUsers();
        Map<String , Integer> issueRisoltePerUser = authClient.getRisoltePerUser();
        Map<String, Integer> issuesPerUser = authClient.getIssuesPerUser();
        Map<String, Double> timeMap = authClient.getTimePerUser();

        for(AuthUser user : users){
            String email = user.getEmail();
            int count = issueRisoltePerUser.getOrDefault(email, 0);
            user.setIssueRisolte(count);

            int bugAssegnati = issuesPerUser.getOrDefault(email, 0);
            user.setIssueAttive(bugAssegnati);

            double tempoMedio = timeMap.getOrDefault(email, 0.0);
            double tempoArrotondato = Math.round(tempoMedio*10.0)/10.0;
            user.setTempoMedio(tempoArrotondato);
        }
        masterData.setAll(users);
    }
}
