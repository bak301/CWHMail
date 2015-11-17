package Controller;

import Model.GMailFolder;
import Model.OAuthCredential;
import Model.UserInfo;
import com.sun.deploy.util.ArrayUtil;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.util.MailConnectException;

import javax.mail.*;
import java.io.*;
import java.util.*;

/**
 * Created by vn130 on 11/12/2015.
 */

public class IMAPController {
    private ArrayList<OAuthCredential> credentialList;
    private ArrayList<UserInfo> userInfoList;
    private ArrayList<IMAPStore> storeList;
    private ConnectDB db;

    public IMAPController(ArrayList<OAuthCredential> credentials){
        this.db = new ConnectDB();
        this.credentialList = credentials;
        this.userInfoList = db.getUserInfoList();
        storeList = new ArrayList<>();
        connectGmailIMAP();
    }

    public ArrayList<IMAPStore> getStoreList(){
        return this.storeList;
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
//            MailBoxController.currentName = credential.getUserInfo().getName();
        }
//        MailBoxController.storeList = this.storeList;

        db.close();
        System.out.println("Successfully connect all credentials to IMAP server !!");
    }

    private void storeConnect(Session session, OAuthCredential credential) {
        try {
            System.out.println("Connecting " + credential.getUsername() + " to IMAP server .....");
            IMAPStore store = (IMAPStore) session.getStore();
            store.connect(credential.getUsername(), credential.getAccess_token());
            storeList.add(store);

            System.out.println("Connect " + credential.getUsername() + " to IMAP server successfully!");
        } catch (MailConnectException e){
            System.out.println("Time out! Maybe server have problem ! Attempt to reconnect ...");
            storeConnect(session, credential);
        } catch (MessagingException e){
            System.out.println("Problem with access token !!");
            // Get the refresh token from database
            credential.setRefresh_token(db.getRefreshToken(credential.getUsername()));
            credential.initCredentialandUserInfo();
            storeConnect(session, credential);
            db.updateCredential(credential);
        }
    }

    private ArrayList<Message> getIBOXMessageArray (List<IMAPFolder> folderList) throws MessagingException{
        for (Folder f : folderList){
            if (f.getFullName().equals("INBOX")){
                f.open(Folder.READ_WRITE);
                ArrayList<Message> listMess = new ArrayList<>(Arrays.asList(f.getMessages()));
                Collections.reverse(listMess);
                return listMess;
            }
        }
        return null;
    }
}
