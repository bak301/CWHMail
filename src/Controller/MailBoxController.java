package Controller;

import Model.GMailFolder;
import Model.MessageTableModel;
import Model.OAuthCredential;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vn130 on 11/16/2015.
 */
public class MailBoxController {
    private ArrayList<OAuthCredential> credentialsList;
    // ----------- FXML CONTROLLER ------------
    @FXML
    private Pane pnLeftColumn;

    @FXML
    private Label lbFullname;

    @FXML
    private TableView<MessageTableModel> tableMain;

    @FXML
    private TableColumn tick;

    @FXML
    private TableColumn<MessageTableModel, String> name;

    @FXML
    private TableColumn attachment;

    @FXML
    private TableColumn star;

    @FXML
    private TableColumn<MessageTableModel, String> content;

    @FXML
    private TableColumn<MessageTableModel, String> date;

    @FXML
    public void initialize(){
        IMAPController imapController = new IMAPController(credentialsList);
        lbFullname.setText(credentialsList.get(0).getUserInfo().getName());
        lbFullname.setVisible(true);

        for (IMAPStore store : imapController.getStoreList()) {
            GMailFolder mainFolder = new GMailFolder(store);
            List<IMAPFolder> folderList = mainFolder.getFolderList();
            for (IMAPFolder f : folderList){
                if (f.getName().equals("INBOX")){
                    createMessages(f);
                }
            }
            createFolderMenu(folderList);
        }


        tableMain.widthProperty().addListener((source, oldWidth, newWidth) -> {
            Pane header = (Pane) tableMain.lookup("TableHeaderRow");
            if (header.isVisible()){
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }

            tableMain.setId("tableMain");
            tableMain.applyCss();
        });
    }

    public void setCredentialsList(ArrayList<OAuthCredential> credentialsList) {
        this.credentialsList = credentialsList;
    }

    private void createFolderMenu(List<IMAPFolder> folderList){
        int layoutY = 95;
        int layoutX = 40;
        for (Folder f : folderList){
            Button btnFolder = new Button(f.getName());
            btnFolder.getStyleClass().add("btnFolder");
            btnFolder.applyCss();

            btnFolder.setLayoutX(layoutX);
            btnFolder.setLayoutY(layoutY);

            btnFolder.setVisible(true);
            layoutY+=25;
            pnLeftColumn.getChildren().add(btnFolder);
        }
    }

    private void createMessages(Folder f){
        try {
            f.open(Folder.READ_WRITE);
            List<Message> last10 = Arrays.asList(f.getMessages());
            Collections.reverse(last10);
            ArrayList<MessageTableModel> modelArrayList = last10.subList(0, 9).stream().map(MessageTableModel::new).collect(Collectors.toCollection(ArrayList::new));
            ObservableList<MessageTableModel> messageObservableList = FXCollections.observableList(modelArrayList);
            name.setCellValueFactory(new PropertyValueFactory<MessageTableModel,String>("from"));
            content.setCellValueFactory(new PropertyValueFactory<MessageTableModel,String>("content"));
            date.setCellValueFactory(new PropertyValueFactory<MessageTableModel,String>("date"));

            tableMain.setItems(messageObservableList);
        } catch (MessagingException e){
            e.printStackTrace();
        }
    }
    // ----- END OF FXML CONTROLLER ----------
}
