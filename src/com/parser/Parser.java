package com.parser;

import com.company.Main;
import com.database.implementation.DatabaseAPI;
import org.apache.commons.codec.digest.DigestUtils;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.spongycastle.crypto.Digest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Parser implements Runnable{

    Block block;

    public Parser(Block b) {
        block = b;
    }

    public void run() {
        String TxHash;
        long TxDate;

        for (Transaction tx : block.getTransactions()) {
            TxHash = tx.getHashAsString();
            TxDate = block.getTime().getTime();

            //Check if there are more inputs than coscriptinbase transactions
            if(tx.getInputs().size()>1){
                String inAddress="N/A - "+System.currentTimeMillis();
                String parentAddress = null;
                String TxHash_AB;


                for(TransactionInput txIn : tx.getInputs()){

                    if(txIn.getScriptSig().getChunks().size()==2) {
                        inAddress= this.generateAddressFromPubKey(txIn.getScriptSig().getPubKey());

                    }
                    parentAddress=txIn.getParentTransaction().getHashAsString();


                    TxHash_AB = DigestUtils.md5Hex(parentAddress+inAddress);

                   /* System.out.println( "***********Input***************\n"+
                                        "TxHashAB: " +TxHash_AB+
                                        "\nTx Hash: "+TxHash+
                                        "\nParent Address: "+parentAddress+
                                        "\nInput Address : "+ inAddress+
                                        "\nTx Date: "+TxDate+
                                        "\n\n\n");*/


                    DatabaseAPI db = new DatabaseAPI();
                    db.WriteInput(TxHash_AB,TxHash,parentAddress,inAddress,TxDate);
                    db.closeConnection();

                }


            }


            for(TransactionOutput txOut : tx.getOutputs()){
                String TxHash_AB;
                String outAddress;

                Script script = txOut.getScriptPubKey();
                Coin value = txOut.getValue();

                outAddress = script.getToAddress(txOut.getParams(),true).toString();

                String btc = String.valueOf((double) value.longValue() / 100000000);



                TxHash_AB = DigestUtils.md5Hex(TxHash+outAddress);

              /*  System.out.println( "***********Output***************\n"+
                                    "TxHashAB: " +TxHash_AB+
                                    "\nTx Hash: "+TxHash+
                                    "\nOutputAddress: "+outAddress+
                                    "\nCoins: "+ btc+
                                    "\nTx Date: "+TxDate+
                                    "\n\n\n");*/

                DatabaseAPI db = new DatabaseAPI();
                db.WriteOutput(TxHash_AB, TxHash,outAddress , Double.valueOf(btc), TxDate);
                db.closeConnection();

            }



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


}
