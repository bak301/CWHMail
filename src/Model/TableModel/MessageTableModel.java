package Model.TableModel;

import Controller.Utility.MessageUtility;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vn130 on 11/17/2015.
 */
public class MessageTableModel {
    private Message message;
    private StringProperty from;
    private StringProperty content;
    private StringProperty date;
    private BooleanProperty attachment;
    private BooleanProperty starred;
    private BooleanProperty isRead;

    public MessageTableModel(Message m) {
        try {
            this.message = m;
            String from = m.getFrom()[0].toString().split(" <")[0].replace("\"","");
            this.from = new SimpleStringProperty((from.contains("=?")?MimeUtility.decodeWord(from):from));
            String ct;
            try {
                String tmp = MessageUtility.getStringContent(m);
                ct = tmp.substring(0,tmp.length()<50?tmp.length():50).trim().replace(System.lineSeparator()," ");
            } catch (NullPointerException e){
                ct = "";
            }
            this.content = new SimpleStringProperty(m.getSubject() + " SPLITTER  " + ct);
            this.date = new SimpleStringProperty(convertDate(m.getReceivedDate()));
            this.isRead = new SimpleBooleanProperty(m.isSet(Flags.Flag.SEEN));
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

    public Message getMessage() {
        return message;
    }
}
