package com.database.connection;

import java.sql.Connection;


import com.configuration.Config;
import java.sql.*;
import java.util.Properties;

public class DBConnection {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;




    public DBConnection() {
       Properties prop= new Config().getConfig();

        try {
            Class.forName("com.mysql.jdbc.Driver");

            // Setup the connection with the DB
            connect = DriverManager.getConnection(prop.getProperty("URL")+"/"+prop.getProperty("db")+"?user="+prop.getProperty("user")+"&password="+prop.getProperty("password"));

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    public  void close(){

        try {
            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

    }



    public java.sql.Connection getConnect() {
        return connect;
    }

    public Statement getStatement() {
        return statement;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }
}
