package Controller;

import View.GmailLoginStage;
import View.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
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
//        loginStage.alert.setOnShown(e-> System.out.println("Mailbox opened !"));
    }
}
