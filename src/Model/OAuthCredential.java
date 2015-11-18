package Model;

/**
 * Created by vn130 on 11/7/2015.
 */

public class OAuthCredential {
    public static final String CLIENT_ID = "518474688680-qkb0i43c8ee6nf8b18iut0odg96torhd.apps.googleusercontent.com";
    public static final String CLIENT_SECRET = "Orh8r0OBJQ-10UHbxzJvccRz";
    public static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob:auto";

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

    public String getAuthorization_code() { return authorization_code; }

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
}
