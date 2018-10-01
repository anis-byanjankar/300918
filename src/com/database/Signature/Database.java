package com.database.Signature;

public interface Database {

    boolean WriteInput(String hash_AB, String TxHash, String TxHashParent_A,String BTCAddress_B, long date);
    boolean WriteOutput(String hash_AB, String TxHash_A, String BTCAddress_B, Double value, long date);




}
