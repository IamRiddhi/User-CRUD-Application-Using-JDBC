/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userentry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RIDDHI DUTTA
 */
public class DBConnection {
    private String username,password,driverClassName,url;

    public DBConnection(String username, String password, String driverClassName, String url) {
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;
        this.url = url;
//        try {
//            Class.forName(driverClassName);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    public Connection createConnection() throws SQLException
    {
        return DriverManager.getConnection(url,username,password);
    }
}