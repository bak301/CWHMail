package Model;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by vn130 on 11/7/2015.
 */

public class OAuthCredential {
    private static final String CLIENT_ID = "518474688680-qkb0i43c8ee6nf8b18iut0odg96torhd.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "Orh8r0OBJQ-10UHbxzJvccRz";
    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob:auto";

    private String username;
    private String authorization_code;
    private String access_token;
    private String expires_time;
    private String refresh_token = "";
    private UserInfo userInfo;

    public OAuthCredential(){
        this.userInfo = new UserInfo();
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getRefresh_token(){
        return refresh_token;
    }

    public String getExpires_time(){
        return expires_time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAccess_token(String access_token){
        this.access_token = access_token;
    }

    public void setExpires_time(String expires_time){
        this.expires_time = expires_time;
    }

    public void setAuthorization_code(String code){
        this.authorization_code = code;
    }

    public static String getAuthCode(){
        return "https://accounts.google.com/o/oauth2/auth?" +
                "scope=https://mail.google.com+https://www.googleapis.com/auth/userinfo.profile"+
                "&redirect_uri=" + OAuthCredential.REDIRECT_URI +
                "&response_type=code" +
                "&client_id=" + OAuthCredential.CLIENT_ID;
    }

    private HttpPost POSTAccessToken(){
        HttpPost post = new HttpPost("https://www.googleapis.com/oauth2/v3/token");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("client_id", OAuthCredential.CLIENT_ID));
        nameValuePairs.add(new BasicNameValuePair("client_secret", OAuthCredential.CLIENT_SECRET));
        if (this.refresh_token.equals("")) {
            System.out.println(" Prepare Authorization request !!!!!");
            nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
            nameValuePairs.add(new BasicNameValuePair("redirect_uri", OAuthCredential.REDIRECT_URI));
            nameValuePairs.add(new BasicNameValuePair("code", this.authorization_code));
        } else {
            System.out.println(" Prepare Refresh Token request !!!!");
            nameValuePairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
            nameValuePairs.add(new BasicNameValuePair("refresh_token", this.refresh_token));
        }
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return post;
    }

    private HttpGet GETUserinfo(){
        return new HttpGet("https://www.googleapis.com/oauth2/v3/userinfo?alt=json&access_token="+this.access_token);
    }

    public boolean initCredentialandUserInfo(){
        HttpClient client = new DefaultHttpClient();
//        HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
        try {
            HttpResponse responsePOST = client.execute(POSTAccessToken());
            BufferedReader rdPOST = new BufferedReader(new InputStreamReader(responsePOST.getEntity().getContent()));
            parsePOSTresponse(rdPOST);
        } catch (IOException e){
            e.printStackTrace();
        }

        HttpClient client1 = new DefaultHttpClient();
        try {
            HttpResponse responseGET = client1.execute(GETUserinfo());
            BufferedReader rdGET = new BufferedReader(new InputStreamReader(responseGET.getEntity().getContent()));
            parseGETresponse(rdGET);
            return true;
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    private void parsePOSTresponse(BufferedReader rd) throws IOException{
        String line = "";
        while ((line = rd.readLine()) != null) {
            System.out.println(line);
            if (!line.contains("{") && !line.contains("}")){
                String value = line.split(":")[1].replace(",","").replace("\"","");
                if (line.contains("access_token")){
                    this.access_token = value.trim();
                    System.out.println(this.access_token);
                } else if (line.contains("expires_in")){
                    int expires_in = Integer.parseInt(value.trim());
                    long now = System.currentTimeMillis();

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeZone(TimeZone.getTimeZone("Etc/GMT+7"));
                    cal.setTimeInMillis(now);
                    cal.add(Calendar.SECOND, expires_in);

                    this.expires_time = cal.getTime().toString();
                } else if (line.contains("refresh_token")){
                    this.refresh_token = value.trim();
                }
            }
        }
        System.out.println("New refresh token saved : " + this.refresh_token);
        System.out.println("New access token saved : " + this.access_token);
        System.out.println("Access token will expire at : " + this.expires_time);
    }

    private void parseGETresponse(BufferedReader rd) throws IOException{
        String line="";
        while ((line = rd.readLine()) != null){
            System.out.println(line);
            if (!line.contains("{") && !line.contains("}")){
                String value = line.split(":")[1].replace(",","").replace("\"","");
                if (line.contains("sub")){
                    this.userInfo.setGoogleid(value);
                } else if (line.contains("\"name\"")){
                    this.userInfo.setName(value);
                } else if (line.contains("link")){
                    this.userInfo.setLink(value);
                } else if (line.contains("picture")){
                    this.userInfo.setPicture(value);
                } else if (line.contains("gender")){
                    this.userInfo.setGender(value);
                } else if (line.contains("locale")){
                    this.userInfo.setLocale(value);
                }
            }
        }
        System.out.println("User info created !");
    }
}
