package Controller;

import Model.GMailFolder;
import Model.OAuthCredential;
import Model.UserInfo;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

import javax.mail.*;
import java.util.*;

/**
 * Created by vn130 on 11/12/2015.
 */
public class MailBoxController {
    private ArrayList<OAuthCredential> credentialList;
    private ArrayList<UserInfo> userInfoList;
    private ArrayList<IMAPStore> storeList;
    private ConnectDB db;

    public MailBoxController(ArrayList<OAuthCredential> credentials, ConnectDB db){
        this.db = db;
        this.credentialList = credentials;
        this.userInfoList = db.getUserInfoList();
        storeList = new ArrayList<>();
        connectGmailIMAP();
    }

    public void connectGmailIMAP(){
        Properties props = System.getProperties();
        props.put("mail.imap.auth", "true");
        props.put("mail.imap.starttls.enable", "true");
        props.put("mail.imap.ssl.enable","true");
        props.put("mail.imap.host", "imap.gmail.com");
        props.put("mail.imap.port", "993");
        props.put("mail.store.protocol","imap");

        // Properties for OAuth 2.0
        props.put("mail.imap.sasl.enable", "true");
        props.put("mail.imap.sasl.mechanisms", "XOAUTH2");
        props.put("mail.imap.auth.login.disable", "true");
        props.put("mail.imap.auth.plain.disable", "true");

        System.out.println("---- There are " + credentialList.size() + " credentials available !");
        for (OAuthCredential credential : credentialList){
            Session session = Session.getInstance(props);
            storeConnect(session, credential);

            // Assign user info to credential
            for (UserInfo userInfo : userInfoList){
                if (credential.getUsername().equals(userInfo.getUsername())){
                    credential.setUserInfo(userInfo);
                }
            }
            FolderButtonController.currentName = credential.getUserInfo().getName();
        }
        FolderButtonController.storeList = this.storeList;

        db.close();
        System.out.println("Successfully connect all credentials to IMAP server !!");
    }

    private void storeConnect(Session session, OAuthCredential credential) {
        try {
            IMAPStore store = (IMAPStore) session.getStore();
            store.connect(credential.getUsername(), credential.getAccess_token());
            storeList.add(store);

            System.out.println("Connect " + credential.getUsername() + " to IMAP server successfully!");
        } catch (MessagingException e){
            e.printStackTrace();
            System.out.println("Problem with access token !!");
            // Get the refresh token from database
            credential.setRefresh_token(db.getRefreshToken(credential.getUsername()));
            credential.initCredentialandUserInfo();
            storeConnect(session, credential);
            db.updateCredential(credential);
        }
    }

    public void test_showAllMail(){
        for (IMAPStore store : storeList){
            GMailFolder mainFolder = new GMailFolder(store);
            List<IMAPFolder> folderList = mainFolder.getFolderList();

            for (Folder f : folderList){
                System.out.println(f.getFullName());
            }
        }
    }
}
