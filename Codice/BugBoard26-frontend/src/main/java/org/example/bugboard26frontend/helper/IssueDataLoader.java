package org.example.bugboard26frontend.helper;

import client.IssueClient;
import enums.Ruolo;
import javafx.collections.ObservableList;
import model.Issue;

import java.util.List;

public class IssueDataLoader {
    private static final IssueClient issueClient = new IssueClient();

    private IssueDataLoader() {}
    public static void loadOnTable(Ruolo ruolo, ObservableList<Issue> masterData, boolean isArchivio) {
        if (isArchivio) {
            List<Issue> issues = issueClient.getIssueArchiviate();
            masterData.setAll(issues);
        }
        else {
            if(ruolo == Ruolo.INTERNAL_USER ||  ruolo == Ruolo.ADMIN) {
                List<Issue> issues = issueClient.getIssueAttive();
                masterData.setAll(issues);
            } else if(ruolo == Ruolo.EXTERNAL_USER) {
                List<Issue> issues = issueClient.elencoIssue();
                issues.removeIf(issue -> "ARCHIVIATO".equalsIgnoreCase(issue.getStato()));
                masterData.setAll(issues);
            }
        }
    }
}
