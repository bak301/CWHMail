package Controller;

import Controller.Utility.OAuthUtility;
import Model.CustomLogger;
import Model.OAuthCredential;
import Model.UserInfo;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.util.MailConnectException;

import javax.mail.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vn130 on 11/12/2015.
 */

public class IMAPController {
    private ArrayList<OAuthCredential> credentialList;
    private ArrayList<UserInfo> userInfoList;
    private ArrayList<IMAPStore> storeList;
    private ConnectDB db;
    private Properties props;
    private Logger logger;

    public IMAPController(ArrayList<OAuthCredential> listCredentials, ConnectDB db){
        this.db = db;
        this.credentialList = listCredentials;
        this.userInfoList = db.getUserInfoList();
        storeList = new ArrayList<>();
        this.logger = new CustomLogger("IMAP").getLogger();
        initProperties();
        connectGmailIMAP();
    }

    private void initProperties(){
        props = System.getProperties();
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
    }

    public void connectGmailIMAP(){
        logger.log(Level.INFO, "---- There are " + credentialList.size() + " credentials available !");

        for (OAuthCredential credential : credentialList){
            storeList.add(getStore(credential));
            // Assign user info to credential
            for (UserInfo userInfo : userInfoList){
                if (credential.getUsername().equals(userInfo.getUsername())){
                    credential.setUserInfo(userInfo);
                }
            }
        }
        db.close();

        logger.log(Level.INFO, "Successfully connect all credentials to IMAP server !!");
    }

    private IMAPStore getStore(OAuthCredential credential) {
        logger.log(Level.INFO, "Connecting " + credential.getUsername() + " to IMAP server .....");
        try {
            IMAPStore store = (IMAPStore) Session.getInstance(props).getStore();
            store.connect(credential.getUsername(), credential.getAccess_token());

            logger.log(Level.INFO, "Connect " + credential.getUsername() + " to IMAP store successfully!");
            return store;
        } catch (MailConnectException e){
            logger.log(Level.INFO, "Time out ! Attempt to reconnect ...");
        } catch (MessagingException e){
            logger.log(Level.INFO, "Access Token is expired ! Attempt to refresh it ....");

            // Get the refresh token from database
            credential.setRefresh_token(db.getRefreshToken(credential.getUsername()));
            OAuthUtility.createNewData(credential);
            db.updateCredential(credential);
        }

        //Reconnect
        return getStore(credential);
    }

    public ArrayList<IMAPStore> getStoreList(){
        return this.storeList;
    }

    public ArrayList<IMAPFolder> getFolderList(IMAPStore store){
        ArrayList<IMAPFolder> folderList = new ArrayList<>();
        try {
            List<IMAPFolder> commonFolderList = Arrays.asList((IMAPFolder[]) store.getDefaultFolder().list());
            List<IMAPFolder> gmailFolderList = Arrays.asList((IMAPFolder[]) store.getDefaultFolder().getFolder("[Gmail]").list());

            // Add all child folder of [Gmail] to the list
            folderList.addAll(commonFolderList);
            folderList.addAll(gmailFolderList);
            // Remove [Gmail] common folder
            folderList.removeIf(e->e.getName().equals("[Gmail]"));
        } catch (MessagingException e){
            e.printStackTrace();
        }
        return folderList;
    }
}
