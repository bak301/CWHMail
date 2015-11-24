package Controller;

import Controller.Utility.MessageUtility;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParseException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
            List<String> list = Arrays.asList(message.getFrom()).stream().map(address -> {
                String[] tmp = address.toString().split(" <");
                String from = tmp[0].replace("\"","");
                String name = from;
                try {
                    name = from.contains("=?")?MimeUtility.decodeWord(from):from;
                } catch (IOException | ParseException e){
                    e.printStackTrace();
                }
                return  name + " <" + tmp[1];
            }).collect(Collectors.toList());

            String buildup = String.join(", ", list);
            fromLabel.setText(buildup);
            subjectLabel.setText(message.getSubject());
            engine.loadContent(MessageUtility.getHTMLContent(message), "text/html");
        } catch (MessagingException | IOException e){
            e.printStackTrace();
        }
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
