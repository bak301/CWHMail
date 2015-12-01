package Controller;

import Controller.Utility.MessageUtility;
import Model.CustomLogger;
import Model.TableModel.MessageTableModel;
import Model.OAuthCredential;
import com.sun.mail.imap.IMAPFolder;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by vn130 on 11/16/2015.
 */
public class MailBoxController {
    // ----------- FXML CONTROLLER ------------
    @FXML
    private MenuButton mbPanelTop;

    @FXML
    private Button btnAddMail;

    @FXML
    private void toComposeMail(){
        ComposeViewController composeViewController = new ComposeViewController(imapController);
        composeViewController.setCredential(credentialsList);
        Parent root;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/GUI/fxml/ComposeView.fxml"));
        loader.setController(composeViewController);
        Stage stage = new Stage();
        try {
            root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Pane pnLeftColumn;

    @FXML
    private Label lbFullname;

    @FXML
    private TableView<MessageTableModel> mainTable;

    @FXML
    private TableView<MessageTableModel> socialTable;

    @FXML
    private TableView<MessageTableModel> otherTable;

    @FXML
    private TableColumn tick;

    @FXML
    private TableColumn<MessageTableModel, String> mainName;

    @FXML
    private TableColumn attachment;

    @FXML
    private TableColumn star;

    @FXML
    private TableColumn<MessageTableModel, String> mainContent;

    @FXML
    private TableColumn<MessageTableModel, String> mainDate;

    private ConnectDB db;
    private ArrayList<OAuthCredential> credentialsList;
    private Logger logger;
    private ArrayList<TableView<MessageTableModel>> tableList;
    private IMAPController imapController;
    private ArrayList<Button> listBtn;

    @FXML
    public void initialize(){
        ObservableList<IMAPFolder> folderObservableList = FXCollections.observableArrayList();
        logger = new CustomLogger("Mail Box").getLogger();
        listBtn = new ArrayList<>();

        initFolderList(folderObservableList);
        initTable();
        addListenerToFolderList(folderObservableList);

        mbPanelTop.getItems().forEach(i->i.setOnAction(e->mbPanelTop.setText(i.getText())));
    }
    // ----- END OF FXML CONTROLLER ----------

    private void initFolderList(ObservableList<IMAPFolder> folderObservableList){
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                imapController = new IMAPController(credentialsList, db);
                return null;
            }
        };

        task.stateProperty().addListener((ov,old,newState)->{
            if (newState == Worker.State.SUCCEEDED){
                if (imapController.getStoreList().size() > 0){
                    ArrayList<IMAPFolder> folders = imapController.getFolderList(imapController.getStoreList().get(0));
                    folderObservableList.addAll(folders.stream().collect(Collectors.toList()));
                }
                lbFullname.setText(credentialsList.get(0).getUserInfo().getName());
            }
        });

        new Thread(task).start();
    }

    // -------------------------- TABLE ------------------------------

    private void initTable(){
        tableList = new ArrayList<>();
        tableList.add(mainTable);
//        tableList.add(socialTable);
//        tableList.add(otherTable);

        for (TableView<MessageTableModel> t : tableList){
            t.widthProperty().addListener((source, oldWidth, newWidth) -> {
                Node header = t.lookup("TableHeaderRow");
                header.setVisible(!header.isVisible());
            });

            t.setRowFactory(param -> {
                final TableRow<MessageTableModel> row = new TableRow<MessageTableModel>();
                row.setOnMouseClicked(event -> {
                    Message m = param.getItems().get(param.getSelectionModel().getSelectedIndex()).getMessage();
                    ReaderController controller = new ReaderController();
                    controller.setMessage(m);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/GUI/fxml/reader.fxml"));
                    loader.setController(controller);
                    Stage stage = new Stage();
                    try {
                        Parent root = loader.load();
                        stage.setScene(new Scene(root));
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    stage.show();
                });
                return row;
            });
        }
    }

    private void addListenerToFolderList(ObservableList<IMAPFolder> folderObservableList){
        folderObservableList.addListener((ListChangeListener<IMAPFolder>) c -> {
            while (c.next()){
                if (c.wasAdded()){
                    c.getAddedSubList().forEach(MailBoxController.this::initFolder);
                } else if (c.wasRemoved()){
                    c.getRemoved().forEach(f->listBtn.removeIf(b->b.getText().equals(f.getName())));
                }
                pnLeftColumn.requestLayout();
            }
        });

    }

    private void initFolder(IMAPFolder folder){
        folder.addMessageCountListener(new MessageCountAdapter() {
            @Override
            public void messagesAdded(MessageCountEvent e) {
                ArrayList<LinkedList<Message>> newMessages = MessageUtility.getClassedMessageArray(e.getMessages());
                for (int i = 0; i < 3 ; i++){
                    logger.info("Prepare to add message data cell ...");
                    updateMessageDataCell(tableList.get(i), newMessages.get(i), true);
                }
            }

            @Override
            public void messagesRemoved(MessageCountEvent e) {
                ArrayList<LinkedList<Message>> newMessages = MessageUtility.getClassedMessageArray(e.getMessages());
                for (int i = 0; i < 3; i++){
                    logger.info("Prepare to delete message data cell ...");
                    updateMessageDataCell(tableList.get(i), newMessages.get(i), false);
                }
            }
        });

        logger.info("Folder " + folder.getName() + " added !");
        pnLeftColumn.getChildren().add(createFolderButton(folder));
    }

    private Button createFolderButton(IMAPFolder folder){
        logger.info("Prepare to create button ....");
        Button btnFolder = new Button(folder.getName());
        btnFolder.getStyleClass().add("btnFolder");
        btnFolder.applyCss();

        btnFolder.setLayoutX(55);
        double currentLayoutY = listBtn.isEmpty()?95:(listBtn.get(listBtn.size()-1).getLayoutY() + 25);
        btnFolder.setLayoutY(currentLayoutY);
        btnFolder.setVisible(true);

        btnFolder.setOnAction(e->{
            for (Button b : listBtn){
                b.getStyleClass().removeIf(c->c.equals("btnFolderSelected"));
            }
            btnFolder.getStyleClass().add("btnFolderSelected");

            new Thread(new Task(){
                @Override
                protected Object call() throws Exception {
                    ArrayList<LinkedList<Message>> classedMessageList = new ArrayList<>();
                    try {
                        if (!folder.isOpen()){
                            folder.open(Folder.READ_WRITE);
                        }
                        logger.info("Prepare to get messages ...");
                        classedMessageList = MessageUtility.getClassedMessageArray(folder.getMessages(folder.getMessageCount()-15,folder.getMessageCount()));
                    } catch (MessagingException ex){
                        ex.printStackTrace();
                    }

                    for (int i = 0; i < 3 ; i++){
                        createMessageDataCell(tableList.get(i), classedMessageList.get(i));
                    }
                    return null;
                }
            }).start();
        });
        listBtn.add(btnFolder);
        return btnFolder;
    }

    private HBox createMailPreview(String content){
        String[] splitContent = content.split("SPLITTER");

        HBox box = new HBox();
        Label subject = new Label(splitContent[0]);
        subject.setStyle("-fx-font-style: italic;");

        Label preview = new Label(splitContent[1]);

        box.getChildren().add(subject);
        box.getChildren().add(preview);
        return box;
    }


    private void createMessageDataCell(TableView<MessageTableModel> table, LinkedList<Message> array ){
        logger.info("Prepare to get message list ...");
        ObservableList<MessageTableModel> messageObservableList = FXCollections.observableArrayList();
        messageObservableList.addListener((ListChangeListener<MessageTableModel>) c -> table.refresh());

        new Thread(() -> {
            sortBySentDate(array);
            for (Message m : array){
                messageObservableList.add(new MessageTableModel(m));
            }
        }).start();

        table.setItems(messageObservableList);
        ObservableList<TableColumn<MessageTableModel,?>> colList = table.getColumns();
        colList.get(5).setCellValueFactory(new PropertyValueFactory<>("date"));
        colList.get(1).setCellValueFactory(new PropertyValueFactory<>("from"));
        colList.get(4).setCellValueFactory(new PropertyValueFactory<>("content"));

        ((TableColumn<MessageTableModel, String>)colList.get(4)).setCellFactory(c -> new TableCell<MessageTableModel, String>(){
                    @Override
                    protected void updateItem(String content, boolean empty){
                        super.updateItem(content, empty);
                        if (!empty){
                            setGraphic(createMailPreview(content));
                        }
                    }
                }
        );
    }

    private void updateMessageDataCell(TableView<MessageTableModel> table, LinkedList<Message> array, boolean isAdded){
        if (isAdded){
            sortBySentDate(array);
            table.getItems().addAll(array.stream().map(MessageTableModel::new).collect(Collectors.toList()));
            logger.info("Messages added to table !");
        } else {
            table.getItems().removeAll(array.stream().map(MessageTableModel::new).collect(Collectors.toList()));
            logger.info("Messages removed from table !");
        }
    }

    private void sortBySentDate(LinkedList<Message> mList){
        mList.sort((o1, o2) -> {
            try {
                return -(o1.getSentDate().compareTo(o2.getSentDate()));
            } catch (MessagingException e){
                return 0;
            }
        });
    }

    // ------------------ END OF TABLE -----------------

    public void setCredentialsList(ArrayList<OAuthCredential> credentialsList) {
        this.credentialsList = credentialsList;
    }

    public void setDb(ConnectDB db){
        this.db = db;
    }
}
