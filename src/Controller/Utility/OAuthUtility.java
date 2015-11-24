package Controller.Utility;

import Model.OAuthCredential;
import Model.UserInfo;
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
 * Created by vn130 on 11/17/2015.
 */
public class OAuthUtility {
    public static String GETAuthCode(){
        return "https://accounts.google.com/o/oauth2/auth?" +
                "scope=https://mail.google.com+https://www.googleapis.com/auth/userinfo.profile"+
                "&redirect_uri=" + OAuthCredential.REDIRECT_URI +
                "&response_type=code" +
                "&client_id=" + OAuthCredential.CLIENT_ID;
    }

    public static boolean createNewData(OAuthCredential credential){
        System.out.println("---------------------------- CALLING OAUTH UTILITY ---------------------");
        HttpClient client = new DefaultHttpClient();
//        HttpPost post = new HttpPost("https://accounts.google.com/o/oauth2/token");
        try {
            HttpResponse responsePOST = client.execute(POSTAccessToken(credential.getAuthorization_code(), credential.getRefresh_token()));
            BufferedReader rdPOST = new BufferedReader(new InputStreamReader(responsePOST.getEntity().getContent()));
            parsePOSTresponse(rdPOST, credential);
        } catch (IOException e){
            e.printStackTrace();
        }

        HttpClient client1 = new DefaultHttpClient();
        try {
            HttpResponse responseGET = client1.execute(GETUserinfo(credential.getAccess_token()));
            BufferedReader rdGET = new BufferedReader(new InputStreamReader(responseGET.getEntity().getContent()));
            parseGETresponse(rdGET, credential.getUserInfo());
            return true;
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    private static HttpPost POSTAccessToken(String authorization_code, String refresh_token){
        HttpPost post = new HttpPost("https://www.googleapis.com/oauth2/v3/token");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("client_id", OAuthCredential.CLIENT_ID));
        nameValuePairs.add(new BasicNameValuePair("client_secret", OAuthCredential.CLIENT_SECRET));
        if (refresh_token.equals("")) {
            System.out.println(" Prepare Authorization request !!!!!");
            nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
            nameValuePairs.add(new BasicNameValuePair("redirect_uri", OAuthCredential.REDIRECT_URI));
            nameValuePairs.add(new BasicNameValuePair("code", authorization_code));
        } else {
            System.out.println(" Prepare Refresh Token request !!!!");
            nameValuePairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
            nameValuePairs.add(new BasicNameValuePair("refresh_token", refresh_token));
        }
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return post;
    }

    private static void parsePOSTresponse(BufferedReader rd, OAuthCredential credential) throws IOException{
        String line = "";
        while ((line = rd.readLine()) != null) {
            System.out.println(line);
            if (!line.contains("{") && !line.contains("}")){
                String value = line.split(": ")[1].replace(",","").replace("\"","");
                if (line.contains("access_token")){
                    credential.setAccess_token(value.trim());
                } else if (line.contains("expires_in")){
                    int expires_in = Integer.parseInt(value.trim());
                    long now = System.currentTimeMillis();

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeZone(TimeZone.getTimeZone("Etc/GMT+7"));
                    cal.setTimeInMillis(now);
                    cal.add(Calendar.SECOND, expires_in);

                    credential.setExpires_time(cal.getTime().toString());
                } else if (line.contains("refresh_token")){
                    credential.setRefresh_token(value.trim());
                }
            }
        }
    }

    private static HttpGet GETUserinfo(String access_token){
        System.out.println(access_token);
        System.out.println("-- Doing GET user info request ---");
        return new HttpGet("https://www.googleapis.com/oauth2/v3/userinfo?alt=json&access_token="+access_token);
    }

    private static void parseGETresponse(BufferedReader rd, UserInfo userInfo) throws IOException{
        String line="";
        while ((line = rd.readLine()) != null){
            System.out.println(line);
            if (!line.contains("{") && !line.contains("}")){
                String value = line.split(": ")[1].replace(",","").replace("\"","");
                if (line.contains("sub")){
                    userInfo.setGoogleid(value);
                } else if (line.contains("\"name\"")){
                    userInfo.setName(value);
                } else if (line.contains("link")){
                    userInfo.setLink(value);
                } else if (line.contains("picture")){
                    userInfo.setPicture(value);
                } else if (line.contains("gender")){
                    userInfo.setGender(value);
                } else if (line.contains("locale")){
                    userInfo.setLocale(value);
                }
            }
        }
        System.out.println("User info created !");
    }
}
