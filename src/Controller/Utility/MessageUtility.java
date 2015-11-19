package Controller.Utility;

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

    public static int classSender(String from){
        return from.equals("Facebook")?1:2;
    }

    public static ArrayList<LinkedList<Message>> getClassedMessageArray (Message[] messages, int max){
        ArrayList<LinkedList<Message>> list = new ArrayList<>();
        LinkedList<Message> main = new LinkedList<>();
        LinkedList<Message> social = new LinkedList<>();
        LinkedList<Message> other = new LinkedList<>();

        List<Message> messageList = Arrays.asList(messages);
        Collections.reverse(messageList);
        messageList = messageList.subList(0,messageList.size()<=max?messageList.size():max);

        for (Message m : messageList){
            String from = "";
            try {
                from = m.getFrom()[0].toString().split(" <")[0].replace("\"","");
            } catch (MessagingException e){
                e.printStackTrace();
            }

            if (from.contains("Facebook")){
                social.add(m);
            } else if (from.contains("Google")){
                other.add(m);
            } else {
                main.add(m);
            }
        }

        list.add(main);
        list.add(social);
        list.add(other);

        return list;
    }
}
