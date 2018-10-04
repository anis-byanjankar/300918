package com.company;

import com.configuration.Config;
import com.database.connection.DbPool;
import com.database.implementation.DatabaseAPI;
import com.parser.Parser;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.utils.BlockFileLoader;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class Main {
    public static Properties prop;

    public static DbPool dbPool;
    public static int connectionSize;

    public static void print(String x) {
        if (prop.getProperty("debug").equals("1")) {
            System.out.println(x);
        }
    }

    public static DatabaseAPI getDbConnection() {
        return dbPool.getConnection();
    }

    public static void main(String[] args) {
        //Start a Database connection Pool

        connectionSize = 200;
        dbPool = new DbPool(connectionSize);

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

        //Close the dataBase Connection
        dbPool.closeConnectionPool();


    }


    public static void doSomething() {

        FileInputStream input = null;
        try {
            input = new FileInputStream("fileNo.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Properties pp = new Properties();
        try {
            pp.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }


        int i = Integer.parseInt(pp.getProperty("blockNo"));
        ArrayList<Thread> parserCollection = new ArrayList<Thread>();

        while (true) {
            NetworkParameters np = new MainNetParams();
            Context.getOrCreate(MainNetParams.get());

            if (Main.getSingleBuildList(i) == null) {
                System.out.println("NO FILE FOUND FOR Block No " + i);

            }
            System.out.println("Loading Block: " + i);

            BlockFileLoader loader = new BlockFileLoader(np, Main.getSingleBuildList(i));

            for (Block block : loader) {

                if (parserCollection.size() >= Integer.valueOf(Main.prop.getProperty("threads"))) {
                    Thread lastThread = parserCollection.get(parserCollection.size() - 1);

                        while(lastThread.isAlive()){
                            try {
                                lastThread.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        parserCollection.remove(parserCollection.size() - 1);

                }

                Parser p = new Parser(block);
                Thread t = new Thread(p);
                parserCollection.add(0, t);
                t.start();

            }


            System.out.println("Parsed Completed for Block: " + i++);

            try {
                FileOutputStream out = new FileOutputStream("fileNo.properties");
                Properties p = new Properties();
                p.setProperty("blockNo", String.valueOf(i));
                p.store(out, null);
                out.close();


                for (Thread pa : parserCollection.subList(0, parserCollection.size())) {
                    try {
                        if(pa.isAlive()){
                            pa.join();
                            parserCollection.remove(pa);

                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                dbPool.closeConnectionPool();
                dbPool = new DbPool(connectionSize);
                //Reset the connections

              /*  if (i % 2 == 0) {




                }*/


            } catch (Exception e) {
                e.printStackTrace();
            }
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
