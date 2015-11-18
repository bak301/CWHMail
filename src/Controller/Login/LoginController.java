package Controller.Login;

import Controller.ConnectDB;
import Controller.MailBoxController;
import Model.OAuthCredential;
import View.GmailLoginStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class LoginController {
    private ConnectDB db;

    @FXML
    private Button btnGmail;
    @FXML
    private Button btnYahoomail;
    @FXML
    private Button btnOutlookmail;
    @FXML
    private void toGmailLogin(ActionEvent event){
        GmailLoginStage loginStage = new GmailLoginStage();
        loginStage.start();
        loginStage.alert.setOnShown(e-> toMailBox());
    }

    private void toMailBox(){
        ArrayList<OAuthCredential> credentialsList = db.getCredentials();

        // Call mailbox
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../../View/GUI/fxml/mailBox.fxml"));

        MailBoxController controller = new MailBoxController();
        controller.setDb(db);
        controller.setCredentialsList(credentialsList);

        fxmlLoader.setController(controller);
        try {
            Parent root = fxmlLoader.load();
            Stage main = (Stage) btnGmail.getScene().getWindow();
            main.setScene(new Scene(root));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setDb(ConnectDB db){
        this.db = db;
    }
}
