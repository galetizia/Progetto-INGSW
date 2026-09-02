package org.example.bugboard26frontend;

import client.AuthClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import client.AuthSession;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class SegnalazioneIssueController {

    @FXML
    private TextField titoloField;

    @FXML
    private Button confermaButton;

    @FXML
    private TextField descrizioneField;

    @FXML
    private TextField prioritaField;

    @FXML
    protected void onConfermaButtonClick(){

    }
}
