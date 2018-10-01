package com.company;

import com.configuration.Config;
import com.database.implementation.DatabaseAPI;

public class Main {

    public static void main(String[] args) {
	// write your code here
        System.out.println("TEst");

        //Load the configuration Files
        Config config = new Config();
        config.getConfig();

        DatabaseAPI db = new DatabaseAPI();


        db.WriteInput("6","3","3","1ADFASDASDAD12e3123123123A",5);

        db.WriteOutput("6","3","3",4.000,5);


    }
}
