package View;

import Controller.ConnectDB;
import Controller.IMAPController;
import Controller.MailBoxController;
import Model.OAuthCredential;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Welcome !");
        ConnectDB db = new ConnectDB();
        ArrayList<OAuthCredential> credentialsList = db.getCredentials();
        db.close();

        Parent root;
        if (credentialsList != null){// Prepare mail for mailBox
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../View/GUI/fxml/mailBox.fxml"));
            MailBoxController mailBoxController = new MailBoxController();
            mailBoxController.setCredentialsList(credentialsList);
            fxmlLoader.setController(mailBoxController);
            root = fxmlLoader.load();
        } else {
            root = FXMLLoader.load(getClass().getResource("../View/GUI/fxml/login.fxml"));
        }
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
