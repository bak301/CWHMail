package Controller.Utility;

import com.sun.mail.imap.IMAPMessage;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.IOException;
import java.util.*;

/**
 * Created by vn130 on 11/17/2015.
 */
public class MessageUtility {
    public static String getTextContent(Part p) throws IOException, MessagingException{
        if (p.isMimeType("text/plain")) {
            return (String) p.getContent();
        }
        //check if the content has attachment
        else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            int count = mp.getCount();

            String sum = "";
            for (int i = 0; i < count; i++) {
                sum += getTextContent(mp.getBodyPart(i));
            }
            return sum;
        }
        //check if the content is a nested message
        else if (p.isMimeType("message/rfc822")) {
            return getTextContent((Part) p.getContent());
        }
        return "";
    }

    public static String getHTMLContent(Part p) throws IOException, MessagingException{
        if (p.isMimeType("text/HTML")) {
            return (String) p.getContent();
        }
        //check if the content has attachment
        else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            int count = mp.getCount();

            String sum = "";
            for (int i = 0; i < count; i++) {
                sum += getHTMLContent(mp.getBodyPart(i));
            }
            return sum;
        }
        //check if the content is a nested message
        else if (p.isMimeType("message/rfc822")) {
            return getHTMLContent((Part) p.getContent());
        }
        return p.getContentType();
    }

    public static ArrayList<LinkedList<Message>> getClassedMessageArray (Message[] messages){
        ArrayList<LinkedList<Message>> list = new ArrayList<>();
        LinkedList<Message> main = new LinkedList<>();
        LinkedList<Message> social = new LinkedList<>();
        LinkedList<Message> other = new LinkedList<>();

        for (Message m : messages){
            String from = "";
            try {
                from = m.getFrom()[0].toString().split(" <")[0].replace("\"","");
            } catch (MessagingException e){
                e.printStackTrace();
            }

            if (from.contains("Facebook")){
                social.add(0,m);
            } else if (from.contains("Google")){
                other.add(0,m);
            } else {
                main.add(0,m);
            }
        }

        list.add(main);
        list.add(social);
        list.add(other);

        return list;
    }
}
