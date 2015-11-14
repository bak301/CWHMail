package MainApplication;

import Controller.ConnectDB;
import Controller.MailBoxController;
import Model.OAuthCredential;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Welcome !");
        ConnectDB db = new ConnectDB();
        ArrayList<OAuthCredential> credentialsList = db.getCredentials();

        Parent root;
        if (credentialsList != null){
            root = FXMLLoader.load(getClass().getResource("../GUI/fxml/mailBox.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            // Prepare mail for mailBox
            mailBoxInit(credentialsList,db);
        } else {
            root = FXMLLoader.load(getClass().getResource("../GUI/fxml/login.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }
    }

    private void mailBoxInit(ArrayList<OAuthCredential> credentialsList, ConnectDB db){
        MailBoxController mailBoxController = new MailBoxController(credentialsList,db);
        mailBoxController.test_showAllMail();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
