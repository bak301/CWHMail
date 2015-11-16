package Controller;

import Model.GMailFolder;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vn130 on 11/16/2015.
 */
public class FolderButtonController {
    public static String currentName;
    public static ArrayList<IMAPStore> storeList ;
    // ----------- FXML CONTROLLER ------------
    @FXML
    private Pane pnLeftColumn;

    @FXML
    private Label lbFullname;

    @FXML
    public void initialize(){
        System.out.println(currentName);
        lbFullname.setText(currentName);
        lbFullname.setVisible(true);

        for (IMAPStore store : storeList) {
            GMailFolder mainFolder = new GMailFolder(store);
            List<IMAPFolder> folderList = mainFolder.getFolderList();

            int layoutY = 115;
            int layoutX = 40;
            boolean done = false;
            for (Folder f : folderList){
                Button btnFolder = new Button(f.getName());
                btnFolder.setStyle("-fx-background-color: #BBFDC5;" +
                        "    -fx-text-fill: #000000;" +
                        "    -fx-font-weight: bold;");
                btnFolder.applyCss();

                btnFolder.setLayoutX(layoutX);
                btnFolder.setLayoutY(layoutY);

                btnFolder.setVisible(true);
                layoutX = 40;
                layoutY+=25;
                pnLeftColumn.getChildren().add(btnFolder);
            }
        }
    }

    // ----- END OF FXML CONTROLLER ----------
}
