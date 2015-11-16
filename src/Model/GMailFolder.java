package Model;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.xml.internal.ws.binding.FeatureListUtil;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by vn130 on 11/11/2015.
 */
public class GMailFolder {
    private ArrayList<IMAPFolder> folderList;

    public GMailFolder(IMAPStore currentStore){
        initFolderList(currentStore);
    }

    private void initFolderList(IMAPStore currentStore){
        try {
            List<IMAPFolder> commonFolderList = Arrays.asList((IMAPFolder[]) currentStore.getDefaultFolder().list());
            List<IMAPFolder> gmailFolderList = Arrays.asList((IMAPFolder[]) currentStore.getDefaultFolder().getFolder("[Gmail]").list());

            // Add all child folder of [Gmail] to the list
            folderList = new ArrayList<>();
            folderList.addAll(commonFolderList);
            folderList.addAll(gmailFolderList);
            // Remove [Gmail] common folder
            folderList.removeIf(e->e.getName().equals("[Gmail]"));
        } catch (MessagingException e){
            e.printStackTrace();
        }
    }

    public List<IMAPFolder> getFolderList(){
        return folderList;
    }
}
