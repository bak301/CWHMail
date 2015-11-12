package Controller;

import Model.OAuthCredential;
import View.GmailLoginStage;
import javafx.concurrent.Worker;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLHeadElement;
import org.w3c.dom.html.HTMLInputElement;
import org.w3c.dom.html.HTMLTitleElement;

/**
 * Created by vn130 on 11/12/2015.
 */
public class GmailLoginController {
    private OAuthCredential credential;
    private boolean addListener = true;
    private GmailLoginStage stage;
    private ConnectDB con;

    public GmailLoginController(GmailLoginStage stage){
        this.stage = stage;
        this.credential = new OAuthCredential();
    }

    public void addListener(){
        stage.engine.getLoadWorker().stateProperty().addListener((ov,oldState,newState) -> {
            // Check if the page is change to another
            if (newState == Worker.State.SUCCEEDED) {
                System.out.println("new state");
                if (addListener){
                    addUsernameToCredential(credential);
                    addListener = false;
                }
                //Check if this page is the server's response page
                if (stage.engine.getLocation().contains("approval")) {
                    HTMLHeadElement head = (HTMLHeadElement) stage.engine.getDocument().getElementsByTagName("head").item(0);
                    HTMLTitleElement title = (HTMLTitleElement) head.getElementsByTagName("title").item(0);

                    credential.setAuthorization_code(title.getTextContent().split("=")[1]);
                    if (credential.getAccessToken()){
                        con = new ConnectDB();
                        con.addCredential(credential);
                    }

                    stage.close();
                    stage.congratulation();
                }
            }
        });
    }

    public void addUsernameToCredential(OAuthCredential credential){
        HTMLInputElement input = (HTMLInputElement) stage.engine.getDocument().getElementById("Email");
        EventTarget nextButton = (EventTarget) stage.engine.getDocument().getElementById("next");
        nextButton.addEventListener("click", e -> {
            credential.username = input.getValue();
            credential.username += (!credential.username.contains("@"))?"@gmail.com":"";
        }, false);
    }

    public OAuthCredential getCredential(){
        return this.credential;
    }
}
