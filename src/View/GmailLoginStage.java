package View;

import Controller.Login.GmailLoginController;
import Controller.Utility.OAuthUtility;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * Created by vn130 on 11/12/2015.
 */
public class GmailLoginStage extends Stage{
    private WebView view = new WebView();
    public WebEngine engine = view.getEngine();
    private ScrollPane panel = new ScrollPane();
    GmailLoginController controller = new GmailLoginController(this);
    public Alert alert;

    public GmailLoginStage() {
        panel.setContent(view);
        this.setResizable(true);
        this.setScene(new Scene(panel));
        alert = new Alert(Alert.AlertType.CONFIRMATION);
    }

    public void start(){
        this.show();
        controller.addListener();
        // Load the login page
        engine.load(OAuthUtility.GETAuthCode());
    }

    public void congratulation(){
        alert.setTitle("Congratulation !");
        alert.setHeaderText(null);
        alert.setContentText("You can access your Gmail account now !");
        alert.show();
    }
}
