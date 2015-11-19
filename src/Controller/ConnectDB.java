package Controller;

import Model.CustomLogger;
import Model.OAuthCredential;
import Model.UserInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vn130 on 11/12/2015.
 */
public class ConnectDB {
    private Connection con;
    private Statement stm;
    private PreparedStatement pstm;
    private Logger logger;

    public ConnectDB(){
        this.logger = new CustomLogger("Database").getLogger();
        connect();
        init();
    }

    private void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:user.db");
        } catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Connected to user database !");
    }

    public void close(){
        try {
            con.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void init(){
        try {
            stm = con.createStatement();
            String createUserDataTable = "CREATE TABLE IF NOT EXISTS UserAuth (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "USERNAME TEXT NOT NULL UNIQUE," +
                    "ACCESS_TOKEN TEXT," +
                    "EXPIRE_TIME TEXT," +
                    "REFRESH_TOKEN TEXT," +
                    "ACTIVE INTEGER DEFAULT 1)";
            stm.execute(createUserDataTable);

            String createUserInfoTable = "CREATE TABLE IF NOT EXISTS USERINFO (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "GOOGLEID TEXT NOT NULL," +
                    "NAME TEXT NOT NULL," +
                    "USERNAME TEXT NOT NULL," +
                    "LINK TEXT," +
                    "PICTURE TEXT, " +
                    "GENDER TEXT," +
                    "LOCALE TEXT )";
            stm.execute(createUserInfoTable);
            stm.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Table UserAuth init completed !");
    }

    // ---------------------------- TABLE USERINFO ---------------------------------------
    public boolean addUserInfo(UserInfo userInfo){
        try {
            String addCredential = "INSERT INTO USERINFO (GOOGLEID,NAME,LINK,PICTURE,GENDER,LOCALE,USERNAME)" +
                    " VALUES (?,?,?,?,?,?,?)";
            pstm = con.prepareStatement(addCredential);
            pstm.setString(1,userInfo.getGoogleid());
            pstm.setString(2,userInfo.getName());
            pstm.setString(3,userInfo.getLink());
            pstm.setString(4,userInfo.getPicture());
            pstm.setString(5,userInfo.getGender());
            pstm.setString(6,userInfo.getLocale());
            pstm.setString(7,userInfo.getUsername());

            if (pstm.executeUpdate() != 0){
                pstm.close();
                logger.log(Level.INFO, "Add new user info successfully !");
                return true;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<UserInfo> getUserInfoList(){
        ArrayList<UserInfo> userInfoList = new ArrayList<>();
        try {
            stm = con.createStatement();
            String getCredential = "SELECT * FROM USERINFO";
            ResultSet rs = stm.executeQuery(getCredential);
            if (!rs.next()){
                logger.log(Level.SEVERE, "No credentials found !");
                return null;
            } else {
                do {
                    UserInfo currentUser = new UserInfo();
                    currentUser.setGoogleid(rs.getString("GOOGLEID"));
                    currentUser.setUsername(rs.getString("USERNAME"));
                    currentUser.setName(rs.getString("NAME"));
                    currentUser.setLink(rs.getString("LINK"));
                    currentUser.setPicture(rs.getString("PICTURE"));
                    currentUser.setGender(rs.getString("GENDER"));
                    currentUser.setLocale(rs.getString("LOCALE"));
                    userInfoList.add(currentUser);
                } while (rs.next());
            }
            stm.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return userInfoList;
    }

    // ---------------------------------- TABLE CREDENTIALS ------------------------------------

    public boolean addCredential(OAuthCredential credential){
        try {
            String addCredential = "INSERT INTO UserAuth (USERNAME,ACCESS_TOKEN,EXPIRE_TIME,REFRESH_TOKEN)" +
                    " VALUES (?,?,?,?)";
            pstm = con.prepareStatement(addCredential);
            pstm.setString(1,credential.getUsername());
            pstm.setString(2,credential.getAccess_token());
            pstm.setString(3,credential.getExpires_time());
            pstm.setString(4,credential.getRefresh_token());

            if (pstm.executeUpdate() != 0){
                pstm.close();
                logger.log(Level.SEVERE, "Add new credential successfully !");
                return true;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCredential(OAuthCredential credential){
        try {
            String addCredential = "UPDATE UserAuth " +
                    "SET ACCESS_TOKEN = ?, EXPIRE_TIME = ?, REFRESH_TOKEN = ?" +
                    "WHERE USERNAME = ?";
            pstm = con.prepareStatement(addCredential);
            pstm.setString(1,credential.getAccess_token());
            pstm.setString(2,credential.getExpires_time());
            pstm.setString(3,credential.getRefresh_token());
            pstm.setString(4,credential.getUsername());

            if (pstm.executeUpdate() != 0){
                pstm.close();
                logger.log(Level.INFO, "Updated new Access Token for current session !");
                return true;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<OAuthCredential> getCredentials(){
        ArrayList<OAuthCredential> credentialArrayList = new ArrayList<>();
        try {
            stm = con.createStatement();
            String getCredential = "SELECT USERNAME, ACCESS_TOKEN, EXPIRE_TIME FROM UserAuth WHERE ACTIVE=1";
            ResultSet rs = stm.executeQuery(getCredential);
            if (!rs.next()){
                logger.log(Level.SEVERE, "No credentials found !");
                return null;
            } else {
                do {
                    OAuthCredential currentCredential = new OAuthCredential();
                    currentCredential.setUsername(rs.getString("USERNAME"));
                    currentCredential.setAccess_token(rs.getString("ACCESS_TOKEN"));
                    currentCredential.setExpires_time(rs.getString("EXPIRE_TIME")); ;
                    credentialArrayList.add(currentCredential);
                } while (rs.next());
            }
            stm.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return credentialArrayList;
    }

    public String getRefreshToken(String username){
        String refresh_token = "";
        try {
            String getRT = "SELECT REFRESH_TOKEN FROM UserAuth WHERE USERNAME=?";
            pstm = con.prepareStatement(getRT);

            pstm.setString(1,username);
            refresh_token = pstm.executeQuery().getString("REFRESH_TOKEN");
            pstm.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return refresh_token;
    }

    //----------------------------------------- TABLE USERMESSAGES --------------------------------
}
