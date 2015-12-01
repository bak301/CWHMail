package Controller;

import Model.OAuthCredential;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

/**
 * Created by Troang on 11/17/2015.
 */
public class SendViaSMTP {
    private Session session;
    private OAuthCredential currentCredential;
    public SendViaSMTP(OAuthCredential currentCredential) {
        this.currentCredential = currentCredential;
    }
    public Session smtpConnection() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable","true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");

        // Properties for OAuth 2.0
        props.put("mail.smtp.sasl.enable", "true");
        props.put("mail.smtp.sasl.mechanisms", "XOAUTH2");
        props.put("mail.smtp.auth.login.disable", "true");
        props.put("mail.smtp.auth.plain.disable", "true");
        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(currentCredential.getUsername(), currentCredential.getAccess_token());
            }
        });
        return session;
    }
}
