package Model;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

/**
 * Created by vn130 on 11/11/2015.
 */
public class GMailFolder {
    private Store currentStore;
    private Folder GMAILFolder;

    public GMailFolder(Store currentStore) {
        this.currentStore = currentStore;
        try {
            this.GMAILFolder = currentStore.getDefaultFolder().getFolder("[Gmail]");
        } catch (MessagingException e){
            e.printStackTrace();
        }
    }

    public void getAllFolder(){
        try {
            Folder inboxFolder = currentStore.getDefaultFolder().getFolder("INBOX");
            System.out.println("INBOX folder found !");
            inboxFolder.open(Folder.READ_WRITE);
            Message[] inbox = inboxFolder.getMessages();
            System.out.println("Get INBOX completed !");

            Folder trashFolder = currentStore.getDefaultFolder().getFolder("Trash");
            System.out.println("TRASH folder found !");
            trashFolder.open(Folder.READ_WRITE);
            Message[] trash = trashFolder.getMessages();
            System.out.println("Get TRASH completed");

            Folder gmailFolder = currentStore.getDefaultFolder().getFolder("[Gmail]");
            System.out.println("[Gmail] folder found !");

            Folder[] f = currentStore.getDefaultFolder().getFolder("[Gmail]").list();
            for (Folder folder : f){
                System.out.println(folder.getName()+ "\n");
            }

            System.out.println("---------------");

            Folder[] f2 = currentStore.getDefaultFolder().list();
            for (Folder folder : f2){
                System.out.println(folder.getName()+ "\n");
            }

        } catch (MessagingException e){
            e.printStackTrace();
        }
    }
}
