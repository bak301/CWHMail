package Controller;

import Model.GMailFolder;
import Model.OAuthCredential;
import org.apache.http.message.BasicNameValuePair;

import javax.mail.*;
import java.util.*;

/**
 * Created by vn130 on 11/12/2015.
 */
public class MailBoxController {
    public ArrayList<OAuthCredential> credentialList;
    public ArrayList<Store> storeList;
    private ConnectDB db;

    public MailBoxController(ArrayList<OAuthCredential> credentials, ConnectDB db){
        this.db = db;
        this.credentialList = credentials;
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
        }
        System.out.println("Successfully connect all credentials to IMAP server !!");
    }

    private void storeConnect(Session session, OAuthCredential credential) {
        try {
            Store store = session.getStore();
            store.connect(credential.username, credential.access_token);
            storeList.add(store);

            System.out.println("Connect " + credential.username + " to IMAP server successfully!");
            System.out.println("Access token expire at : " + credential.expires_time);
        } catch (MessagingException e){
            credential.refresh_token = db.getRefreshToken(credential.username);
            credential.getAccessToken();
            storeConnect(session, credential);
            db.updateCredential(credential);
            db.close();
        }
    }

    public void test_showAllMail(){
        for (Store store : storeList){
            GMailFolder mainFolder = new GMailFolder();
            List<Folder> folderList = mainFolder.getFolderList(store);

            for (Folder f : folderList){
                System.out.println(f.getFullName());
            }
        }
    }
}
