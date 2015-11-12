package Model;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vn130 on 11/7/2015.
 */

public class OAuthCredential {
    private static final String CLIENT_ID = "518474688680-qkb0i43c8ee6nf8b18iut0odg96torhd.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "Orh8r0OBJQ-10UHbxzJvccRz";
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob:auto";

    public String username;
    private String authorization_code;
    public String access_token;
    public int expires_in;
    private String refresh_token = "";

    public void setAuthorization_code(String code){
        this.authorization_code = code;
    }

    public static String getAuthCodeByURL(){
        return "https://accounts.google.com/o/oauth2/auth?" +
                "scope=https://mail.google.com"+
                "&redirect_uri=" + OAuthCredential.REDIRECT_URI +
                "&response_type=code" +
                "&client_id=" + OAuthCredential.CLIENT_ID;
    }

    public boolean getAccessToken(){
        HttpClient client = new DefaultHttpClient();
//        HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
        HttpPost post = new HttpPost("https://www.googleapis.com/oauth2/v3/token");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("client_id", OAuthCredential.CLIENT_ID));
            nameValuePairs.add(new BasicNameValuePair("client_secret", OAuthCredential.CLIENT_SECRET));
            if (this.refresh_token.equals("")){
                nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
                nameValuePairs.add(new BasicNameValuePair("redirect_uri", OAuthCredential.REDIRECT_URI));
                nameValuePairs.add(new BasicNameValuePair("code", this.authorization_code));
            } else {
                nameValuePairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
                nameValuePairs.add(new BasicNameValuePair("refresh_token", this.refresh_token));
            }
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = client.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
                if (line.contains("access_token")){
                    String toBeProcess = line.split(":")[1];
                    this.access_token = toBeProcess.substring(0, toBeProcess.length() - 1).replace("\"","").trim();
                    System.out.println(this.access_token);
                } else if (line.contains("expires_in")){
                    this.expires_in = Integer.parseInt(line.split(":")[1].replace(",","").trim());
                } else if (line.contains("refresh_token")){
                    this.refresh_token = line.split(":")[1].replace("\"","").trim();
                }
            }
            System.out.println("New refresh token saved : " + this.refresh_token);
            System.out.println("New access token saved : " + this.access_token);
            System.out.println("Acess token will expire in : " + this.expires_in);
            return true;
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }
}
