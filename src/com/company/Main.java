package com.company;

import com.configuration.Config;
import com.database.implementation.DatabaseAPI;
import com.parser.Parser;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.utils.BlockFileLoader;

import java.io.File;
import java.util.*;

public class Main {
    public static Properties prop;

    public static void main(String[] args) {
        // write your code here

        //Load the configuration Files
        Config config = new Config();
        prop = config.getConfig();

        /*int i=0;
        while(i<-1) {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {

                        DatabaseAPI db = new DatabaseAPI();
                        db.WriteInput(String.valueOf(System.currentTimeMillis()/Math.random()), "3", "3", "1ADFASDASDAD12e3123123123A", 5);
                        db.WriteOutput(String.valueOf(System.currentTimeMillis()/Math.random()), "3", "3", 4.000, 5);
                        db.closeConnection();
                    }
                }
            }).start();
            i++;
        }*/
        Main.doSomething();


    }


    public static void doSomething() {

        int i =0;

        while (true) {
            NetworkParameters np = new MainNetParams();
            Context.getOrCreate(MainNetParams.get());

            if (Main.getSingleBuildList(i) == null) {
                System.out.println("NO FILE FOUND FOR Block No " + i);

            }

            BlockFileLoader loader = new BlockFileLoader(np, Main.getSingleBuildList(i));
            ArrayList<Thread> parserCollection = new ArrayList<Thread>();

            for (Block block : loader) {

                if (parserCollection.size() >= 100) {
                    Thread lastThread = parserCollection.get(parserCollection.size() - 1);
                    try {
                        lastThread.join();
                        parserCollection.remove(parserCollection.size() - 1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Parser p = new Parser(block);
                Thread t = new Thread(p);
                parserCollection.add(0, t);
                t.start();

            }
            System.out.println("Parsed Completed for Block: "+ i++);
            prop.setProperty("block",String.valueOf(i));
        }


    }

    // The method returns a list of files in a directory according to a certain
    // pattern (block files have name blkNNNNN.dat)
    private static List<File> buildList() {
        List<File> list = new LinkedList<File>();
        for (int i = 0; true; i++) {
            File file = new File(Main.prop.getProperty("rawDataPath") + String.format(Locale.US, "blk%05d.dat", i));
            if (!file.exists())
                break;
            list.add(file);
        }
        return list;
    }
    private static List<File> getSingleBuildList(int i) {
        List<File> list = new LinkedList<File>();
        File file = new File(Main.prop.getProperty("rawDataPath") + String.format(Locale.US, "blk%05d.dat", i));

        if (!file.exists())
            return null;

        list.add(file);

        return list;
    }


}
