package Controller;

import Model.OAuthCredential;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Troang on 11/16/2015.
 */
public class ComposeViewController {
    private ArrayList<OAuthCredential> listUser;
    private String fromUser;
    private String fromEmail;
    @FXML
    private ChoiceBox chooseUser;
    @FXML
    private TextField toUser;
    @FXML
    private TextField ccUser;
    @FXML
    private TextField subjectField;
    @FXML
    private Button btnSend;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button resetBtn;
    @FXML
    private HTMLEditor contentArea;
    @FXML
    private Button attachFile;
    @FXML
    private ListView<File> attachments;

    final ObservableList<File> items = FXCollections.observableArrayList();
    private List<File> chooseFile;
    @FXML
    public void initialize() {
        FileChooser chooser = new FileChooser();
        System.out.println("user"+listUser.get(0).getUserInfo().getName());
        //adjust components
        attachments.setVisible(false);
        attachments.setOrientation(Orientation.HORIZONTAL);
        attachments.setItems(items);
        //who to send
        fromEmail = listUser.get(0).getUsername();
        fromUser = listUser.get(0).getUserInfo().getName();
        chooseUser.setItems(FXCollections.observableArrayList(
            fromUser
        ));
        chooseUser.getSelectionModel().selectFirst();

        //attach file
        attachFile.setOnAction(event -> {
            chooseFile = chooser.showOpenMultipleDialog(null);
            if (chooseFile != null) {
                attachments.setVisible(true);
                for (int i = 0; i < chooseFile.size(); i++) {

                    items.add(chooseFile.get(i).getAbsoluteFile());

                    java.nio.file.Path p = Paths.get(chooseFile.get(i).getAbsolutePath());
                    String fileName = p.getFileName().toString();
                    System.out.println("File name: "+fileName);
                    System.out.println("file location: "+chooseFile.get(i).getAbsolutePath());

                }

            } else {
                attachments.setVisible(false);
                System.out.println("User cancel select file");
            }

        });

        //double click on file name to remove an attachment
        attachments.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (event.getClickCount() == 2) {
                        int index = attachments.getSelectionModel().getSelectedIndex();
                        items.remove(index);
                        chooseFile.remove(attachments.getSelectionModel().getSelectedItem());
                        System.out.println("CLicked on: "+attachments.getSelectionModel().getSelectedIndex());
                    }
                }
            }
        });
        //reset all
        resetBtn.setOnAction(e -> {
            subjectField.clear();
            toUser.clear();
            contentArea.setHtmlText("");
        });
        //send mail
        btnSend.setOnAction(event -> {
            if (attachments.isVisible()) {
                sendMultiPartMail();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Gửi thành công!");
                alert.setHeaderText(null);
                alert.show();
            } else {
                sendNormalMail();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Gửi thành công!");
                alert.setHeaderText(null);
                alert.show();
            }
        });
    }
    private IMAPController imapController;

    public ComposeViewController(IMAPController imapController) {
        this.imapController = imapController;
    }

    public void setCredential(ArrayList<OAuthCredential> credential) {
        this.listUser = credential;
    }

    //send attachments
    private boolean sendMultiPartMail(){
        SendViaSMTP sendViaSMTP = new SendViaSMTP(listUser.get(0));
        try {
            Message message = new MimeMessage(sendViaSMTP.smtpConnection());
            message.setFrom(new InternetAddress(fromEmail));
            message.setSubject(subjectField.getText());
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toUser.getText()));

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(contentArea.getHtmlText(), "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart("mixed");
            multipart.addBodyPart(messageBodyPart);

            //adding attachments
            for (File f : chooseFile) {
                String filePath = f.getAbsolutePath();
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filePath);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(source.getName());
                multipart.addBodyPart(messageBodyPart);
            }
            message.setContent(multipart);
            Transport.send(message);

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return false;
    }
    private boolean sendNormalMail() {
        SendViaSMTP sendViaSMTP = new SendViaSMTP(listUser.get(0));

        try {
            Message message = new MimeMessage(sendViaSMTP.smtpConnection());
            message.setFrom(new InternetAddress(fromEmail));
            message.setSubject(subjectField.getText());
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toUser.getText()));
            message.setContent(contentArea.getHtmlText(), "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("sent!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
