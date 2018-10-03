package com.parser;

import com.company.Main;
import com.database.implementation.DatabaseAPI;
import org.apache.commons.codec.digest.DigestUtils;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.spongycastle.crypto.Digest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Parser implements Runnable {

    Block block;

    public Parser(Block b) {
        block = b;
    }

    public void run() {
        String TxHash;
        long TxDate;

        TxDate = block.getTime().getTime();

        //Coinbase Transaction

        Transaction coinbaseTx = block.getTransactions().get(0);
        String tmpHash_AB=DigestUtils.md5Hex("0000" + System.currentTimeMillis());
        if (Main.prop.getProperty("persisitInDB").equals("1")) {


            try {

                Main.getDbConnection().WriteInput(tmpHash_AB, coinbaseTx.getHashAsString(), "0000", "COINBASE", TxDate);
            } catch (SQLException e) {
                if(!e.getMessage().contains("Duplicate")){
                    e.printStackTrace();
                }
            }
        }

        if (Main.prop.getProperty("debug").equals("1")) {
            Main.print("***********Input***************\n" +
                    "TxHashAB: " + tmpHash_AB +
                    "\nTx Hash: " + coinbaseTx.getHashAsString() +
                    "\nParent Address: " + "0000"+
                    "\nInput Address : " + "COINBASE"+
                    "\nTx Date: " + TxDate +
                    "\n\n\n");
        }



        this.parseOutput(coinbaseTx.getOutputs(),coinbaseTx.getHashAsString(),TxDate);


        for (Transaction tx : block.getTransactions().subList(1, block.getTransactions().size())) {
            TxHash = tx.getHashAsString();

            this.parseInput(tx.getInputs(),TxHash,TxDate);

            this.parseOutput(tx.getOutputs(),TxHash,TxDate);

        }
    }

    public String generateAddressFromPubKey(byte[] pubKey) {
        byte[] publicKey = pubKey;
        byte[] EncryptedPublicKey = Utils.sha256hash160(publicKey);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            output.write(0x00);
            output.write(EncryptedPublicKey);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] out = output.toByteArray();

        byte[] checksum = Sha256Hash.hashTwice(out);
        output.reset();
        try {
            output.flush();

            output.write(out);
            output.write(checksum[0]);
            output.write(checksum[1]);
            output.write(checksum[2]);
            output.write(checksum[3]);


        } catch (IOException e) {
            e.printStackTrace();
        }

        String s = Base58.encode(output.toByteArray());
        return s;
    }

    public String parseAddress(TransactionInput txIP) {

        Script script = txIP.getScriptSig();
        if (script.isSentToCLTVPaymentChannel()) {
            byte[] pubK = script.getCLTVPaymentChannelRecipientPubKey();
            String recepientPK = this.generateAddressFromPubKey(pubK);


            byte[] pubKS = script.getCLTVPaymentChannelSenderPubKey();
            String senderPK = this.generateAddressFromPubKey(pubKS);
        }


        if (script.isPayToScriptHash()) {
            byte[] pubKG = script.getPubKey();
            String GeneralPK = this.generateAddressFromPubKey(pubKG);
        }


        return "N/A - " + System.currentTimeMillis()/20;

    }

    public String parseAddress(TransactionOutput txIP) {
        Script script = txIP.getScriptPubKey();
        if (script.isSentToCLTVPaymentChannel()) {
            byte[] pubK = script.getCLTVPaymentChannelRecipientPubKey();
            String recepientPK = this.generateAddressFromPubKey(pubK);

            byte[] pubKS = txIP.getScriptPubKey().getCLTVPaymentChannelSenderPubKey();
            String senderPK = this.generateAddressFromPubKey(pubKS);
        }

        if (script.isPayToScriptHash()) {
            byte[] pubKG = script.getPubKey();
            String GeneralPK = this.generateAddressFromPubKey(pubKG);
        }
        return "N/A - " + System.currentTimeMillis()/20;
    }
    void parseInput(List<TransactionInput> txI,String TxHash,long TxDate){
        for (TransactionInput txIn : txI) {
            String inAddress = "N/A - " + System.currentTimeMillis()/20;
            String parentAddress = null;
            String TxHash_AB;

            if (txIn.getScriptSig().getChunks().size() == 2) {
                inAddress = this.generateAddressFromPubKey(txIn.getScriptSig().getPubKey());
            } else {
                inAddress = this.parseAddress(txIn);
            }

            parentAddress = txIn.getOutpoint().getHash().toString();

            TxHash_AB = DigestUtils.md5Hex(parentAddress + inAddress);
            if (Main.prop.getProperty("debug").equals("1")) {
                Main.print("***********Input***************\n" +
                        "TxHashAB: " + TxHash_AB +
                        "\nTx Hash: " + TxHash +
                        "\nParent Address: " + parentAddress +
                        "\nInput Address : " + inAddress +
                        "\nTx Date: " + TxDate +
                        "\n\n\n");
            }

            if (Main.prop.getProperty("persisitInDB").equals("1")) {
//                while(true){
                    try {
                        this.WriteIn(TxHash_AB,TxHash,parentAddress,inAddress,TxDate);

                    } catch (Exception e) {
                        if(e.getMessage().contains("Duplicate")){
                            System.out.println(e.getMessage());
                        }
                        else{
                            e.printStackTrace();
                        }

                    }
               // }
            }
        }
    }
    void parseOutput(List<TransactionOutput> tOut , String TxHash, long TxDate){
        for (TransactionOutput txOut : tOut) {

            String TxHash_AB;
            String outAddress;

            Script script = txOut.getScriptPubKey();
            Coin value = txOut.getValue();

            try {
                outAddress = script.getToAddress(txOut.getParams(), true).toString();
            } catch (Exception e) {
                outAddress = this.parseAddress(txOut);
            }

            String btc = String.valueOf((double) value.longValue() / 100000000);


            TxHash_AB = DigestUtils.md5Hex(TxHash + outAddress);

            if (Main.prop.getProperty("debug").equals("1")) {
                Main.print("***********Output***************\n" +
                        "TxHashAB: " + TxHash_AB +
                        "\nTx Hash: " + TxHash +
                        "\nOutputAddress: " + outAddress +
                        "\nCoins: " + btc +
                        "\nTx Date: " + TxDate +
                        "\n\n\n");
            }

            if (Main.prop.getProperty("persisitInDB").equals("1")) {
                //while(true){
                    try {
                        this.WriteOut(TxHash_AB,TxHash,outAddress,btc,TxDate);
                    } catch (Exception e) {
                        if(!e.getMessage().contains("Duplicate")){
                            if(e.getMessage().contains("Duplicate")){
                                System.out.println(e.getMessage());
                            }
                            else{
                                e.printStackTrace();
                            }
                        }
                        else{
                            System.out.println(e.getMessage());
                        }
                    }
                //}
            }
        }
    }

    public boolean WriteOut(String TxHash_AB, String TxHash, String outAddress, String btc , long TxDate) throws SQLException, ClassNotFoundException {
        Main.getDbConnection().WriteOutput(TxHash_AB, TxHash, outAddress, Double.valueOf(btc), TxDate);

        return true;
    }

    public boolean WriteIn(String TxHash_AB, String TxHash, String parentAddress,String inAddress, long TxDate) throws SQLException, ClassNotFoundException {
        Main.getDbConnection().WriteInput(TxHash_AB, TxHash, parentAddress, inAddress, TxDate);
        return true;
    }
}
