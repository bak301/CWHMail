package Controller; /**
 * Created by vn130 on 11/6/2015.
 */

import Model.GMailFolder;
import Model.OAuthCredential;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class TestIMAP_SMTP {
    private OAuthCredential currentCredential;
    private Session session;
    private Store store;

    public TestIMAP_SMTP(OAuthCredential credential) {
        this.currentCredential = credential;
    }

    private boolean connectSMTP() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable","true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mai.from", currentCredential.username);

        // Properties for OAuth 2.0
        props.put("mail.smtp.sasl.enable", "true");
        props.put("mail.smtp.sasl.mechanisms", "XOAUTH2");
        props.put("mail.smtp.auth.login.disable", "true");
        props.put("mail.smtp.auth.plain.disable", "true");
        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(currentCredential.username, currentCredential.access_token);
            }
        });
        return session.getDebug();
    }

    public boolean connectIMAP(){
        Properties props = new Properties();
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
        session = Session.getInstance(props);
        try {
            store = session.getStore();
            store.connect(currentCredential.username, currentCredential.access_token);
            System.out.println("Connect to IMAP server successfully!");
            System.out.println("Access token expire at : " + currentCredential.expires_time);
        } catch (MessagingException e){
            e.printStackTrace();
        }
        return session.getDebug();
    }

    public void sendTestMail(){
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(currentCredential.username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("vn13014@yahoo.com"));
            message.setSubject("Testing Subject");
            message.setText("Dear Mail Crawler,"
                    + "\n\n No spam to my email, please!");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}