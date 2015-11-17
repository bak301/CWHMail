package Model;

import com.sun.mail.util.MimeUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by vn130 on 11/17/2015.
 */
public class MessageTableModel {
    private StringProperty from;
    private StringProperty content;
    private StringProperty date;

    public MessageTableModel(Message m) {
        try {
            System.out.println(m.getFrom()[0] + " : " + m.getContent() + " : " + m.getReceivedDate());
            String[] tmp = m.getFrom()[0].toString().split(" ");
            this.from = new SimpleStringProperty(tmp[tmp.length-1]);
            this.content = new SimpleStringProperty(m.getSubject() + "     " + m.getContent());
            this.date = new SimpleStringProperty(convertDate(m.getReceivedDate()));
        } catch (MessagingException | IOException e){
            e.printStackTrace();
        }
    }

    private String convertDate(Date d){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM, HH:mm");
        return sdf.format(d);
    }

    public String getFrom() {
        return from.get();
    }

    public StringProperty fromProperty() {
        return from;
    }

    public String getContent() {
        return content.get();
    }

    public StringProperty contentProperty() {
        return content;
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }
}
