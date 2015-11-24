package Controller;

import Controller.Utility.MessageUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by vn130 on 11/23/2015.
 */
public class ReaderController {

    // -------- FXML CONTROLLER ------------
    @FXML
    private WebView messageView;

    @FXML
    private Label fromLabel;

    @FXML
    private Label subjectLabel;

    @FXML
    public void initialize(){
        engine = messageView.getEngine();
        loadMail();
    }

    // --------- END OF FXML CONTROLLER

    private Message message;
    private WebEngine engine;

    public void loadMail(){
        try {
            String buildup = String.join(", ", Arrays.toString(message.getFrom()));
            fromLabel.setText(buildup);
            subjectLabel.setText(message.getSubject());
            engine.loadContent(MessageUtility.getStringContent(message), "text/plain");
        } catch (MessagingException | IOException e){
            e.printStackTrace();
        }
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
