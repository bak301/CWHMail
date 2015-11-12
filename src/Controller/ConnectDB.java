package Controller;

import Model.OAuthCredential;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by vn130 on 11/12/2015.
 */
public class ConnectDB {
    private Connection con;
    private Statement stm;
    private PreparedStatement pstm;

    public ConnectDB(){
        connect();
        init();
    }

    public void connect(){
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:user.db");
        } catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
        System.out.println("Connected to user database !");
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
            stm.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        System.out.println("Table UserAuth init completed !");
    }

    public boolean addCredential(OAuthCredential credential){
        try {
            String addCredential = "INSERT INTO UserAuth (USERNAME,ACCESS_TOKEN,EXPIRE_TIME,REFRESH_TOKEN)" +
                    " VALUES (?,?,?,?)";
            pstm = con.prepareStatement(addCredential);
            pstm.setString(1,credential.username);
            pstm.setString(2,credential.access_token);
            pstm.setString(3,credential.expires_time);
            pstm.setString(4,credential.refresh_token);

            if (pstm.executeUpdate() != 0){
                pstm.close();
                System.out.println("Add new credential successfully !");
                return true;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCredential(OAuthCredential credential){
        try {
            String addCredential = "UPDATE UserAuth" +
                    "SET ACCESS_TOKEN=?, EXPIRE_TIME=?, REFRESH_TOKEN=?" +
                    "WHERE USERNAME=?";
            pstm = con.prepareStatement(addCredential);
            pstm.setString(1,credential.access_token);
            pstm.setString(2,credential.expires_time);
            pstm.setString(3,credential.refresh_token);
            pstm.setString(4,credential.username);

            if (pstm.executeUpdate() != 0){
                pstm.close();
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
                System.out.println("No credentials found !");
                return null;
            } else {
                do {
                    OAuthCredential currentCredential = new OAuthCredential();
                    currentCredential.username = rs.getString("USERNAME");
                    currentCredential.access_token = rs.getString("ACCESS_TOKEN");
                    currentCredential.expires_time = rs.getString("EXPIRE_TIME");
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
}
