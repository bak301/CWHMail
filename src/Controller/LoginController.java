package Controller;

import View.GmailLoginStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

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
        loginStage.alert.setOnShown(e-> System.out.println("Mailbox opened !"));
    }
}
