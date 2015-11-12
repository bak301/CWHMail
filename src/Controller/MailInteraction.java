package Controller;

import javax.mail.Folder;

/**
 * Created by vn130 on 11/7/2015.
 */
public interface MailInteraction {
    void sendMail();
    boolean checkNewMail();
    Folder loadOfflineMailBox();
    Folder loadOnlineMailBox();
    void updateOfflineMailBox();
}
