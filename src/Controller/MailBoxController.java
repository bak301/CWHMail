package Controller;

import Model.TableModel.MessageTableModel;
import Model.OAuthCredential;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

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
    private ConnectDB db;
    private ArrayList<OAuthCredential> credentialsList;

    // ----------- FXML CONTROLLER ------------
    @FXML
    private MenuButton mbPanelTop;

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
        IMAPController imapController = new IMAPController(credentialsList, db);
        lbFullname.setText(credentialsList.get(0).getUserInfo().getName());
        lbFullname.setVisible(true);

        for (IMAPStore store : imapController.getStoreList()){
            ArrayList<IMAPFolder> folderList = imapController.getFolderList(store);
            for (IMAPFolder folder : folderList){
                createMessageDataCell(folder.getName().equals("INBOX")?folder:null);
            }
            createFolderMenu(folderList);
        }


        tableMain.widthProperty().addListener((source, oldWidth, newWidth) -> {
            Pane header = (Pane) tableMain.lookup("TableHeaderRow");
            if (header.isVisible()){
//                header.setMaxHeight(0);
//                header.setMinHeight(0);
//                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });

        for (MenuItem mi : mbPanelTop.getItems()){
            mi.setOnAction(e->{
                mbPanelTop.setText(mi.getText());
            });
        }
    }
    // ----- END OF FXML CONTROLLER ----------

    private void createFolderMenu(List<IMAPFolder> folderList){
        int layoutY = 95;

        ArrayList<Button> listBtn = new ArrayList<>();
        for (Folder f : folderList){
            Button btnFolder = createFolderButton(f.getName(), layoutY, listBtn);
            layoutY+=25;
            pnLeftColumn.getChildren().add(btnFolder);
        }
    }

    private Button createFolderButton(String name, int layoutY, ArrayList<Button> listBtn){
        Button btnFolder = new Button(name);
        btnFolder.getStyleClass().add("btnFolder");
        btnFolder.applyCss();

        btnFolder.setLayoutX(55);
        btnFolder.setLayoutY(layoutY);
        btnFolder.setVisible(true);

        btnFolder.setOnAction(e->{
            for (Button b : listBtn){
                b.getStyleClass().removeIf(c->c.equals("btnFolderSelected"));
            }
            btnFolder.getStyleClass().add("btnFolderSelected");
        });
        listBtn.add(btnFolder);
        return btnFolder;
    }

    private HBox createMailPreview(String content){
        String[] splittedContent = content.split("SPLITTER");
//        WebView view = new WebView();
//        view.setMaxHeight(35);
//        WebEngine engine = view.getEngine();
//        engine.loadContent(content);
//        return view;
        HBox box = new HBox();
        Label subject = new Label(splittedContent[0]);
        subject.setStyle("-fx-font-weight : bold;");
        Label preview = new Label(splittedContent[1]);
        box.getChildren().add(subject);
        box.getChildren().add(preview);
        return box;
    }

    private void createMessageDataCell(Folder f){
        if (f == null){
            return;
        }

        try {
            f.open(Folder.READ_WRITE);
            List<Message> last10 = Arrays.asList(f.getMessages());
            Collections.reverse(last10);
            ArrayList<MessageTableModel> modelArrayList = last10.subList(0, 20).stream().map(MessageTableModel::new).collect(Collectors.toCollection(ArrayList::new));
            ObservableList<MessageTableModel> messageObservableList = FXCollections.observableList(modelArrayList);

            name.setCellValueFactory(new PropertyValueFactory<MessageTableModel,String>("from"));
            content.setCellValueFactory(new PropertyValueFactory<MessageTableModel,String>("content"));
            content.setCellFactory(c -> new TableCell<MessageTableModel, String>(){
                    @Override
                    protected void updateItem(String content, boolean empty){
                        super.updateItem(content, empty);
                        if (!empty){
                            setGraphic(createMailPreview(content));
//                            setMaxHeight(30);
                        }
                    }
                }
            );
            date.setCellValueFactory(new PropertyValueFactory<MessageTableModel,String>("date"));

            tableMain.setItems(messageObservableList);
        } catch (MessagingException e){
            e.printStackTrace();
        }
    }

    public void setCredentialsList(ArrayList<OAuthCredential> credentialsList) {
        this.credentialsList = credentialsList;
    }

    public void setDb(ConnectDB db){
        this.db = db;
    }
}
