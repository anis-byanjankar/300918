package com.database.implementation;

import com.database.Signature.Database;
import com.database.connection.DBConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class DatabaseAPI implements Database {
    DBConnection connection;


    public DatabaseAPI() throws SQLException, ClassNotFoundException {
        connection = new DBConnection();
    }

    @Override
    public boolean WriteInput(String hash_AB, String TxHash, String TxHashParent_A,String BTCAddress_B, long date) throws SQLException {
        try {
            PreparedStatement preparedStatement;
            preparedStatement=connection.getConnect().prepareStatement("INSERT INTO input(hash_a_b,Tx_Hash,Tx_Hash_Parent_a,BTCAddress_b,date) values( ?, ?, ?  ,? ,?)");

            preparedStatement.setString(1, hash_AB);
            preparedStatement.setString(2, TxHash);
            preparedStatement.setString(3, TxHashParent_A);
            preparedStatement.setString(4, BTCAddress_B);
            preparedStatement.setTimestamp(5, new java.sql.Timestamp(date));

            preparedStatement.executeUpdate();
            preparedStatement.close();
//            System.out.println("Inserted");

        } catch (SQLException e) {
//            System.out.println(e.getMessage());
            throw e;

        }
        return true;
    }

    @Override
    public boolean WriteOutput(String hash_AB, String TxHash_A, String BTCAddress_B, Double value, long date) throws SQLException {

        try {
            PreparedStatement preparedStatement;
            preparedStatement=connection.getConnect().prepareStatement("INSERT INTO output(hash_a_b,Tx_Hash_a,BTCAddress_b,value,date) values( ?, ?, ?  ,? ,?)");


            preparedStatement.setString(1, hash_AB);
            preparedStatement.setString(2, TxHash_A);
            preparedStatement.setString(3, BTCAddress_B);
            preparedStatement.setDouble(4, value);
            preparedStatement.setTimestamp(5, new java.sql.Timestamp(date));

            preparedStatement.executeUpdate();
            preparedStatement.close();

//            System.out.println("Inserted OP");

        } catch (SQLException e) {
            //e.printStackTrace();
            //System.out.println(e.getMessage());
            throw e;
        }
        return true;
    }

    public void closeConnection(){
        connection.close();
    }
}
