package Controller.Utility;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.IOException;

/**
 * Created by vn130 on 11/17/2015.
 */
public class MessageParser {
    public static String getStringContent(Part p) throws IOException, MessagingException{
        if (p.isMimeType("text/plain")) {
//            System.out.println("This is plain text");
//            System.out.println("---------------------------");
            return (String) p.getContent();
        }
        //check if the content has attachment
        else if (p.isMimeType("multipart/*")) {
//            System.out.println("This is a Multipart");
//            System.out.println("---------------------------");
            Multipart mp = (Multipart) p.getContent();
            int count = mp.getCount();

            String sum = "";
            for (int i = 0; i < count; i++) {
                sum += getStringContent(mp.getBodyPart(i));
            }
            return sum;
        }
        //check if the content is a nested message
        else if (p.isMimeType("message/rfc822")) {
//            System.out.println("This is a Nested Message");
//            System.out.println("---------------------------");
            return getStringContent((Part) p.getContent());
        }
        return null;
    }
}
