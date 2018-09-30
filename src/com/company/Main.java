package com.company;

import com.configuration.Config;

public class Main {

    public static void main(String[] args) {
	// write your code here
        System.out.println("TEst");

        //Load the configuration Files
        Config config = new Config();
        config.loadConfig();



    }
}
